package cz.cuni.mff.vkgmt.connect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

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
import cz.cuni.mff.vkgmt.data.model.RdfError;
import cz.cuni.mff.vkgmt.data.model.RdfErrorSeverity;
import cz.cuni.mff.vkgmt.data.model.RdfFilter;
import cz.cuni.mff.vkgmt.data.model.RdfInstance;
import cz.cuni.mff.vkgmt.data.model.RdfLiteralProperty;
import cz.cuni.mff.vkgmt.data.model.RdfObjectProperty;
import cz.cuni.mff.vkgmt.data.model.RdfTable;
import cz.cuni.mff.vkgmt.utils.SparqlSelectQueryBuilder;

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
	
	@Override
	public void loadErrors(RdfTable table, String namedGraph, Map<String, String> namespaces) {
		String query = this.getErrorsQuery(table, namedGraph, namespaces);
		ResultSet results = null;
		try {
			results = this.connector.query(query);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return;
		}
		List<RdfError> errors = new ArrayList<RdfError>();
		while (results.hasNext()) {
			QuerySolution solution = results.next();
			RdfError error = new RdfError();
			error.setUri(new Uri(solution.get("?uri").asResource().getURI()));
			error.setDescription(solution.get("?desc").asLiteral().getString());
			
			RDFNode objekt = solution.get("?object");
			Object objektValue = null;
			if (objekt != null) {
				if (objekt.isResource()) {
					objektValue = new Uri(objekt.asResource().getURI());
				} else if (objekt.isLiteral()) {
					objektValue = objekt.asLiteral().getValue();
				}
			}
			error.setObject(objektValue);
			
			String property = solution.get("?property").asResource().getURI();
			error.setProperty(new Property(property));
			
			String severity = solution.get("?severity").asResource().getURI();
			if (StringUtils.isNotEmpty(severity)) {
				error.setSeverity(RdfErrorSeverity.fromString(severity));
			}
			
			String subject = solution.get("?subject").asResource().getURI();
			error.setSubject(new Uri(subject));
			error.setValue(solution.get("?value").asLiteral().getFloat());
			errors.add(error);

		}
		fillErrorsIntoInstances(table, errors, namespaces);
	}
	
	private void fillErrorsIntoInstances(RdfTable table, List<RdfError> errors, Map<String, String> namespaces) {
		for (RdfInstance instance: table.getInstances()) {
			for (RdfError error: errors) {
				
				// not an error
				if (error.getValue() == 1f) {
					continue;
				}
				
				if (instance.getUri().equals(error.getSubject())) {
					for (RdfLiteralProperty lp: instance.getLiteralProperties()) {
						String propName = namespaces.get(lp.getProperty().getPrefix()) + lp.getProperty().getName();
						if (propName.equals(error.getProperty().getProperty())) {
							lp.getErrors().add(error);
						}
					}
					for (RdfObjectProperty op: instance.getObjectProperties()) {
						String propName = namespaces.get(op.getProperty().getPrefix()) + op.getProperty().getName();
						if (propName.equals(error.getProperty().getProperty())) {
							op.getErrors().add(error);
						}
					}
				}
			}
		}
	}
	
	private String getErrorsQuery(RdfTable table, String namedGraph, Map<String, String> namespaces) {
		SparqlSelectQueryBuilder queryBuilder = new SparqlSelectQueryBuilder();

		queryBuilder.addNamespace("daq", "http://purl.org/eis/vocab/daq#");
		queryBuilder.addNamespace("dc", "http://purl.org/dc/elements/1.1/");
		queryBuilder.addNamespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		for (String prefix: namespaces.keySet()) {
			String namespace = namespaces.get(prefix);
			queryBuilder.addNamespace(prefix, namespace);
		}
		
		queryBuilder.addSelect("uri");
		queryBuilder.addSelect("subject");
		queryBuilder.addSelect("property");
		queryBuilder.addSelect("object");
		queryBuilder.addSelect("value");
		queryBuilder.addSelect("severity");
		queryBuilder.addSelect("desc");
		
		queryBuilder.setGraph(namedGraph);
		
		queryBuilder.addWhere("?uri", "a", "daq:Observation");
		queryBuilder.addWhere("?uri", "dc:description", "?desc");
		queryBuilder.addWhere("?uri", "daq:problemDescription", "_:node1");
		queryBuilder.addWhere("_:node1", "a", "rdf:Statement");
		queryBuilder.addWhere("_:node1", "rdf:subject", "?subject");
		queryBuilder.addWhere("_:node1", "rdf:predicate", "?property");
		queryBuilder.addWhere("_:node1", "rdf:object", "?object");
		queryBuilder.addWhere("?uri", "daq:value", "?value");
		queryBuilder.addWhere("?uri", "daq:severity", "?severity");
		queryBuilder.addWhere("?subject", "a", table.getType().getType());
		
		if (table.getInstances() != null && table.getInstances().size() > 0) {
			List<String> values = new ArrayList<String>();
			for (RdfInstance instance: table.getInstances()) {
				values.add(instance.getUri().toString());
			}
			queryBuilder.addWhereValue("?subject", values);
		}
		
		return queryBuilder.toString();
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
					rdfProperty.setValue(subjectUri.toString());
					instance.addPropertyIfNotPresent(rdfProperty);
					continue;
				}
				propertyName = "?y" + j++;
				
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

				String varTo = "?y" + j++;
				
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
		SparqlSelectQueryBuilder queryBuilder = new SparqlSelectQueryBuilder();
		
		for (String prefix: namespaces.keySet()) {
			String namespace = namespaces.get(prefix);
			queryBuilder.addNamespace(prefix, namespace);
		}
		
		queryBuilder.setSelectDistinct(true);
		queryBuilder.selectAll();
		queryBuilder.setGraph(namedGraph);
		
		queryBuilder.addWhere("?uri", "rdf:type", blockLayout.getForType().toString());
		
		Map<Property, String> propertyVarMap = new HashMap<Property, String>();
		int j = 0;
		for (ColumnLayout columnLayout: blockLayout.getProperties()) {
			if (columnLayout.isUriColumn()) {
				continue;
			}
			String property = "?y" + j++;
			propertyVarMap.put(columnLayout.getProperty(), property);
			queryBuilder.addWhereOptional("?uri", columnLayout.getProperty().toString(), property);
		}
		
		for (LineLayout lineLayout: lineLayouts) {
			// include all properties
			if (lineLayout.getFromType().equals(blockLayout.getForType())) {
				
				String varTo = "?y" + j++;
				queryBuilder.addWhereOptional("?uri", lineLayout.getProperty().toString(), varTo);
				
				// filter out not selected instances
				if (filter != null && filter.getUriFilters() != null) {
					for (Property uriProperty: filter.getUriFilters().keySet()) {
						if (uriProperty.equals(lineLayout.getProperty())) {
							Uri uriValue = filter.getUriFilters().get(uriProperty);
							if (uriValue == null) {
								continue;
							}
							queryBuilder.addWhere("?uri", uriProperty.getProperty(), uriValue.toString());
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
							queryBuilder.addWhere(uriValue.toString(), uriProperty.getProperty().toString(), "?uri");
						}
					}
				}
			}
		}
		
		for (ColumnLayout columnLayout: blockLayout.getProperties()) {
			if (columnLayout.isUriColumn()) {
				continue;
			}
			if (StringUtils.isNotEmpty(columnLayout.getLabel().getLang()) && !columnLayout.getLabel().getLang().equals("null")) {
				String varName = propertyVarMap.get(columnLayout.getProperty());
				String lang = columnLayout.getLabel().getLang();
				String langFilter = createLangFilter(varName, lang);
				queryBuilder.addFilter(langFilter);
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
				String contains = createContainsFilter(propertyVarMap, property, filterValue);
				queryBuilder.addFilter(contains);
			}
		}
		
		if (filter != null && filter.getLimit() > 0) {
			limit = filter.getLimit();
			queryBuilder.setLimit(filter.getLimit());
		}
		queryBuilder.setOffset(filter.getOffset());
		
		String query = queryBuilder.toString();
		logger.debug(query);
		return query;
	}
	
	protected String createContainsFilter(Map<Property, String> propertyVarMap, Property property, String filterValue) {
		String varName = "";
		if (property.isUriProperty()) {
			varName = "?uri";
		} else {
			varName = propertyVarMap.get(property);
		}
		return "(regex(str(" + varName + "), '" + filterValue + "', 'i'))";
	}
	
	protected String createLangFilter(String varName, String lang) {
		return "(lang(" + varName + ") = '' || lang(" + varName + ") = '" + lang + "')";
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
