package cz.cuni.mff.vkget.connect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.springframework.stereotype.Service;

import cz.cuni.mff.vkget.data.common.LabeledProperty;
import cz.cuni.mff.vkget.data.common.Property;
import cz.cuni.mff.vkget.data.common.Type;
import cz.cuni.mff.vkget.data.common.Uri;
import cz.cuni.mff.vkget.data.layout.BlockLayout;
import cz.cuni.mff.vkget.data.layout.ColumnLayout;
import cz.cuni.mff.vkget.data.layout.Label;
import cz.cuni.mff.vkget.data.layout.LabelType;
import cz.cuni.mff.vkget.data.layout.LineLayout;
import cz.cuni.mff.vkget.data.layout.ScreenLayout;
import cz.cuni.mff.vkget.data.model.DataModel;
import cz.cuni.mff.vkget.data.model.RdfFilter;
import cz.cuni.mff.vkget.data.model.RdfInstance;
import cz.cuni.mff.vkget.data.model.RdfLiteralProperty;
import cz.cuni.mff.vkget.data.model.RdfObjectProperty;
import cz.cuni.mff.vkget.data.model.RdfTable;
import cz.cuni.mff.vkget.sparql.Constants;

/**
 * Implementation of @see DataConnector for common types of endpoints.
 * @author Ales Woska
 *
 */
@Service
public class DefaultDataConnector implements DataConnector {
	
	protected SparqlConnector connector;
	
	/**
	 * Default row limit.
	 */
	protected static final int LIMIT = 40;
	
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
			rdfTable.setLabel(this.getLabel(blockLayout.getLabel(), blockLayout.getForType().getType(), screenLayout));
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
			RdfInstance instance = new RdfInstance(); 
			Uri subjectUri = new Uri(solution.get("?uri").asResource().getURI());
			instance.setUri(subjectUri);
			instance.setType(blockLayout.getForType());

			instance.setLiteralProperties(new ArrayList<RdfLiteralProperty>());
			int j = 0;
			for (ColumnLayout columnLayout: blockLayout.getProperties()) {
				String propertyName = "";
				if (columnLayout.isUriColumn()) {
					RdfLiteralProperty rdfProperty = new RdfLiteralProperty();
					rdfProperty.setProperty(columnLayout.getProperty());
					rdfProperty.setValue(subjectUri.getUri());
					instance.getLiteralProperties().add(rdfProperty);
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
				instance.getLiteralProperties().add(rdfProperty);
			}

			instance.setObjectProperties(new ArrayList<RdfObjectProperty>());
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
				instance.getObjectProperties().add(rdfObjectProperty);
			}
			rdfTable.getInstances().add(instance);
		}
		return rdfTable;
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
			sb.append("PREFIX ").append(prefix).append(": <").append(namespace).append("> ");
		}
		sb.append("SELECT DISTINCT * ");
		
		if (StringUtils.isNotEmpty(namedGraph)) {
			sb.append(" FROM NAMED <").append(namedGraph).append("> WHERE {{ GRAPH <").append(namedGraph).append("> {");
		} else {
			sb.append(" WHERE {{ ");
		}
		
		sb.append(" ?uri rdf:type ").append(blockLayout.getForType()).append(" . ");
		
		Map<Property, String> propertyVarMap = new HashMap<Property, String>();
		
		int j = 0;
		for (ColumnLayout columnLayout: blockLayout.getProperties()) {
			if (columnLayout.isUriColumn()) {
				continue;
			}
			String property = "y" + j++;
			propertyVarMap.put(columnLayout.getProperty(), property);
			sb.append(" OPTIONAL { ?uri ").append(columnLayout.getProperty()).append(" ?").append(property).append(" . } ");
		}
		
		for (LineLayout lineLayout: lineLayouts) {
			// include all properties
			if (lineLayout.getFromType().equals(blockLayout.getForType())) {
				
				String varTo = "y" + j++;
				sb.append(" OPTIONAL { ?uri ").append(lineLayout.getProperty()).append(" ?").append(varTo).append(" . } ");
				
				// filter out not selected instances
				if (filter != null && filter.getUriFilters() != null) {
					for (Property uriProperty: filter.getUriFilters().keySet()) {
						if (uriProperty.equals(lineLayout.getProperty())) {
							Uri uriValue = filter.getUriFilters().get(uriProperty);
							if (uriValue == null) {
								continue;
							}
							sb.append(" ?uri ").append(uriProperty.getProperty()).append(" <").append(uriValue.getUri()).append("> . ");
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
							sb.append("<").append(uriValue.getUri()).append("> ").append(uriProperty.getProperty()).append(" ?uri . ");
						}
					}
				}
			}
		}
		
		
		sb.append("} FILTER (1=1 ");
		
		for (ColumnLayout columnLayout: blockLayout.getProperties()) {
			if (columnLayout.getProperty().equals(Constants.RDFS_LABEL)) {
				sb.append(" && (lang(?").append(propertyVarMap.get(columnLayout.getProperty())).append(") = '").append(columnLayout.getLabel().getLang()).append("') ");
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
				sb.append(" && (regex(str(?").append(propertyVarMap.get(property)).append("), \"").append(filterValue).append("\")) ");
				appendContainsFunction(sb, propertyVarMap, property, filterValue);
			}
		}
		
		sb.append(") ");
		
		int limit = LIMIT;
		if (filter != null && filter.getLimit() > 0) {
			limit = filter.getLimit();
		}
		if (StringUtils.isNotEmpty(namedGraph)) {
			sb.append(" } ");
		}
		sb.append("} ORDER BY ?uri LIMIT ").append(limit);
		return sb.toString();
	}
	
	protected void appendContainsFunction(StringBuilder sb, Map<Property, String> propertyVarMap, Property property, String filterValue) {
		sb.append(" && (regex(str(?").append(propertyVarMap.get(property)).append("), \"").append(filterValue).append("\")) ");
	}
	
	protected String getLabel(Label label, String type, ScreenLayout screenLayout) {
		switch (label.getType()) {
			case CONSTANT: return label.getLabelSource();
			case LABEL: return loadLabel(type, Constants.RDFS_LABEL.getProperty(), screenLayout, label.getLang());
			case PROPERTY: return loadLabel(type, label.getLabelSource(), screenLayout, label.getLang());
			case URI: return type;
			default: return null;
		}
	}
	
	private String loadLabel(String type, String property, ScreenLayout screenLayout, String lang) {
		StringBuilder sb = new StringBuilder();
		for (String prefix: screenLayout.getNamespaces().keySet()) {
			String namespace = screenLayout.getNamespaces().get(prefix);
			sb.append("PREFIX ").append(prefix).append(": <").append(namespace).append("> ");
		}
		sb.append("SELECT ?label WHERE { ").append(type).append(" ").append(property).append(" ?label . ");
		if (StringUtils.isNotEmpty(lang)) {
			sb.append("FILTER (lang(?label) = '").append(lang).append("')");
		}
		sb.append("} ");
		String query = sb.toString();
		
		ResultSet results = this.connector.query(query);
		String label = null;
		while (results.hasNext()) {
			QuerySolution solution = results.next();
			label = solution.get("?label").asLiteral().getString();
		}
		return label;
	}
	
}
