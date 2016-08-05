package cz.cuni.mff.vkgmt.connect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cz.cuni.mff.vkgmt.data.common.LabeledProperty;
import cz.cuni.mff.vkgmt.data.common.Property;
import cz.cuni.mff.vkgmt.data.common.Type;
import cz.cuni.mff.vkgmt.data.common.Uri;
import cz.cuni.mff.vkgmt.data.layout.BlockLayout;
import cz.cuni.mff.vkgmt.data.layout.ColumnLayout;
import cz.cuni.mff.vkgmt.data.layout.Label;
import cz.cuni.mff.vkgmt.data.layout.LabelType;
import cz.cuni.mff.vkgmt.data.layout.LineLayout;
import cz.cuni.mff.vkgmt.data.layout.ScreenLayout;
import cz.cuni.mff.vkgmt.data.model.DataModel;
import cz.cuni.mff.vkgmt.data.model.RdfFilter;
import cz.cuni.mff.vkgmt.data.model.RdfInstance;
import cz.cuni.mff.vkgmt.data.model.RdfLiteralProperty;
import cz.cuni.mff.vkgmt.data.model.RdfObjectProperty;
import cz.cuni.mff.vkgmt.data.model.RdfTable;

/**
 * Implementation of @see DataConnector for common types of endpoints.
 * @author Ales Woska
 *
 */
@Service
public class DefaultDataConnector implements DataConnector {
	protected SparqlConnector connector;
	private Logger logger = Logger.getLogger(getClass());
	
	/**
	 * Default row limit.
	 */
	@Value("${rdf.query.limit}")
	protected int limit = 40;
	
	public DefaultDataConnector() {}
	
	public DefaultDataConnector(ConnectionInfo connectionInfo) {
		connector = new SparqlConnector(connectionInfo);
	}
	
	/**
	 * @inheritDoc
	 */
	@Override
	public DataModel loadDataModel(String namedGraph, ScreenLayout screenLayout) {
		DataModel dataModel = new DataModel();
		dataModel.setTables(new ArrayList<RdfTable>());
		
		for (BlockLayout blockLayout: screenLayout.getBlockLayouts()) {
			RdfTable rdfTable = new RdfTable();
			rdfTable.setType(blockLayout.getForType());
			rdfTable.setLabel(this.getLabel(blockLayout.getLabel(), blockLayout.getForType().getType()));
			rdfTable.setInstances(new ArrayList<RdfInstance>());
			rdfTable.setColumns(new ArrayList<Property>());
			for (ColumnLayout columnLayout: blockLayout.getProperties()) {
				String label = columnLayout.getProperty().getProperty();
				if (columnLayout.getLabel().getType().equals(LabelType.CONSTANT)) {
					label = columnLayout.getLabel().getLabelSource();
				}
				rdfTable.getColumns().add(new LabeledProperty(columnLayout.getProperty(), label));
			}
			dataModel.getTables().add(rdfTable);
		}
		return dataModel;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public RdfTable loadTableData(String namedGraph, Type tableType, RdfFilter filter, ScreenLayout screenLayout) {
		for (BlockLayout blockLayout: screenLayout.getBlockLayouts()) {
			if (blockLayout.getForType().equals(tableType)) {
				RdfTable rdfTable = this.loadRdfTable(namedGraph, screenLayout, filter, blockLayout);
				return rdfTable;
			}
		}
		return null;
	}
	
	/**
	 * Loads table data by given parameters.
	 * @param screenLayout
	 * @param filter
	 * @param blockLayout
	 * @return Null if doesn't satisfy filter parameters.
	 */
	protected RdfTable loadRdfTable(String namedGraph, ScreenLayout screenLayout, RdfFilter filter, BlockLayout blockLayout) {
		RdfTable rdfTable = new RdfTable();
		rdfTable.setType(blockLayout.getForType());
		rdfTable.setInstances(new ArrayList<RdfInstance>());
		rdfTable.setColumns(new ArrayList<Property>());
		for (ColumnLayout columnLayout: blockLayout.getProperties()) {
			rdfTable.getColumns().add(columnLayout.getProperty());
		}

		String query = this.constructTableQuery(namedGraph, blockLayout, screenLayout.getNamespaces(), screenLayout.getLineLayouts(), filter);
		ResultSet results = this.connector.query(query);
		while (results.hasNext()) {
			QuerySolution solution = results.next();
			Uri subjectUri = new Uri(solution.get("?uri").asResource().getURI());

			RdfInstance instance = getExistingInstance(rdfTable, subjectUri);
			boolean exists = true;
			
			if (instance == null) {
				instance = new RdfInstance();
				instance.setUri(subjectUri);
				instance.setType(blockLayout.getForType());
				exists = false;
			}

			int j = 0;
			for (ColumnLayout columnLayout: blockLayout.getProperties()) {
				String propertyName = "";
				if (columnLayout.isUriColumn()) {
					RdfLiteralProperty rdfProperty = new RdfLiteralProperty();
					rdfProperty.setProperty(columnLayout.getProperty());
					rdfProperty.setValue(subjectUri.getUri());
					instance.addPropertyIfNotPresent(rdfProperty);
					continue;
				}
				propertyName = "y" + j++;
				
				RDFNode rdfNode = solution.get(propertyName);
				Object value = null;
				
				if (rdfNode != null) {
					if (rdfNode.isResource()) {
						value = rdfNode.asResource().getURI();
					} else if (rdfNode.isLiteral()) {
						value = rdfNode.asLiteral().getValue();
					}
				}
				
				if (value instanceof Number) {
					value = (Number) value;
				}
				Property property = columnLayout.getProperty();
				
				RdfLiteralProperty rdfProperty = new RdfLiteralProperty();
				rdfProperty.setProperty(property);
				rdfProperty.setValue(value);
				instance.addPropertyIfNotPresent(rdfProperty);
			}

			for (LineLayout lineLayout: screenLayout.getLineLayouts()) {
				if (!lineLayout.getFromType().equals(blockLayout.getForType())) {
					continue;
				}

				String varTo = "y" + j++;
				
				RDFNode rdfNode = solution.get(varTo);
				Uri uri = null;
				if (rdfNode != null) {
					uri = new Uri(rdfNode.asResource().getURI());
				}
				
				RdfObjectProperty rdfObjectProperty = new RdfObjectProperty();
				rdfObjectProperty.setSubjectUri(subjectUri);
				rdfObjectProperty.setProperty(lineLayout.getProperty());
				rdfObjectProperty.setObjectUri(uri);
				instance.addPropertyIfNotPresent(rdfObjectProperty);
			}
			
			if (!exists) {
				rdfTable.getInstances().add(instance);
			}
		}
		return rdfTable;
	}
	
	private RdfInstance getExistingInstance(RdfTable table, Uri uri) {
		for (RdfInstance instance: table.getInstances()) {
			if (instance.getUri().equals(uri)) {
				return instance;
			}
		}
		return null;
	}
	
	/**
	 * Constructs query for loading table data.
	 * @param blockLayout
	 * @param namespaces
	 * @param lineLayouts
	 * @param filter
	 * @return
	 */
	protected String constructTableQuery(String namedGraph, BlockLayout blockLayout, Map<String, String> namespaces, List<LineLayout> lineLayouts, RdfFilter filter) {
		// TODO named graph
		StringBuilder sb = new StringBuilder();
		for (String prefix: namespaces.keySet()) {
			String namespace = namespaces.get(prefix);
			sb.append("PREFIX ").append(prefix).append(": <").append(namespace).append(">\n");
		}
		sb.append("\n");
		sb.append("SELECT DISTINCT * ");
		
		if (StringUtils.isNotEmpty(namedGraph)) {
			sb.append(" FROM NAMED <").append(namedGraph).append("> WHERE {{ GRAPH <").append(namedGraph).append("> {\n");
		} else {
			sb.append(" WHERE {{\n");
		}
		
		sb.append("\t?uri rdf:type ").append(blockLayout.getForType()).append(" .\n");
		
		Map<Property, String> propertyVarMap = new HashMap<Property, String>();
		
		int j = 0;
		for (ColumnLayout columnLayout: blockLayout.getProperties()) {
			if (columnLayout.isUriColumn()) {
				continue;
			}
			String property = "y" + j++;
			propertyVarMap.put(columnLayout.getProperty(), property);
			sb.append("\tOPTIONAL { ?uri ").append(columnLayout.getProperty()).append(" ?").append(property).append(" . }\n");
		}
		
		for (LineLayout lineLayout: lineLayouts) {
			// include all properties
			if (lineLayout.getFromType().equals(blockLayout.getForType())) {
				
				String varTo = "y" + j++;
				sb.append("\tOPTIONAL { ?uri ").append(lineLayout.getProperty()).append(" ?").append(varTo).append(" . }\n");
				
				// filter out not selected instances
				if (filter != null && filter.getUriFilters() != null) {
					for (Property uriProperty: filter.getUriFilters().keySet()) {
						if (uriProperty.equals(lineLayout.getProperty())) {
							Uri uriValue = filter.getUriFilters().get(uriProperty);
							if (uriValue == null) {
								continue;
							}
							sb.append("\t?uri ").append(uriProperty.getProperty()).append(" <").append(uriValue.getUri()).append("> .\n");
						}
					}
				}
			}
			
			if (lineLayout.getToType().equals(blockLayout.getForType())) {

				// filter out not selected instances
				if (filter != null && filter.getUriFilters() != null) {
					for (Property uriProperty: filter.getUriFilters().keySet()) {
						if (uriProperty.equals(lineLayout.getProperty())) {
							Uri uriValue = filter.getUriFilters().get(uriProperty);
							if (uriValue == null) {
								continue;
							}
							sb.append("\t<").append(uriValue.getUri()).append("> ").append(uriProperty.getProperty()).append(" ?uri .\n");
						}
					}
				}
			}
		}
		
		
		sb.append("} FILTER (1=1 ");
		
		for (ColumnLayout columnLayout: blockLayout.getProperties()) {
			if (columnLayout.isUriColumn()) {
				continue;
			}
			if (StringUtils.isNotEmpty(columnLayout.getLabel().getLang()) && !columnLayout.getLabel().getLang().equals("null")) {
				String varName = propertyVarMap.get(columnLayout.getProperty());
				String lang = columnLayout.getLabel().getLang();
				sb.append(" && (lang(?").append(varName).append(") = '' || lang(?").append(varName).append(") = '").append(lang).append("') ");
			}
		}
		
		if (filter != null && filter.getColumnFilters() != null) {
			for (Property property: filter.getColumnFilters().keySet()) {
				if (property == null) {
					continue;
				}
				String filterValue = filter.getColumnFilters().get(property);
				if (filterValue.isEmpty()) {
					continue;
				}
				appendContainsFunction(sb, propertyVarMap, property, filterValue);
			}
		}
		
		sb.append(") ");
		
		int limit = this.limit;
		if (filter != null && filter.getLimit() > 0) {
			limit = filter.getLimit();
		}
		if (StringUtils.isNotEmpty(namedGraph)) {
			sb.append(" }\n");
		}
		sb.append("\n} ORDER BY ?uri LIMIT ").append(limit).append(" OFFSET ").append(filter.getOffset());
		String query = sb.toString();
		logger.debug(query);
		return query;
	}
	
	protected void appendContainsFunction(StringBuilder sb, Map<Property, String> propertyVarMap, Property property, String filterValue) {
		String varName = propertyVarMap.get(property);
		sb.append(" && (regex(str(?").append(varName).append("), '").append(filterValue).append("', 'i')) ");
	}
	
	protected String getLabel(Label label, String type) {
		if (label.getType().equals(LabelType.CONSTANT)) {
			return label.getLabelSource();
		} else {
			return type;
		}
	}
	
	public void setConnector(SparqlConnector connector) {
		this.connector = connector;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}
	
}
