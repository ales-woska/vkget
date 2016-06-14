package cz.cuni.mff.vkget.connect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.springframework.stereotype.Service;

import cz.cuni.mff.vkget.data.common.Property;
import cz.cuni.mff.vkget.data.common.Type;
import cz.cuni.mff.vkget.data.common.Uri;
import cz.cuni.mff.vkget.data.layout.BlockLayout;
import cz.cuni.mff.vkget.data.layout.ColumnLayout;
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
public class CommonDataConnector implements DataConnector {
	
	protected SparqlConnector connector;
	
	/**
	 * Default row limit.
	 */
	protected static final int LIMIT = 40;
	
	public CommonDataConnector() {}
	
	public CommonDataConnector(String endpoint) {
		connector = new SparqlConnector(endpoint);
	}
	
	/**
	 * @inheritDoc
	 */
	@Override
	public DataModel loadDataModel(ScreenLayout screenLayout) {
		DataModel dataModel = new DataModel();
		dataModel.setTables(new ArrayList<RdfTable>());
		
		for (BlockLayout blockLayout: screenLayout.getBlockLayouts()) {
			RdfTable rdfTable = this.loadRdfTable(screenLayout, null, blockLayout);
			dataModel.getTables().add(rdfTable);
		}
		return dataModel;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public RdfTable loadTableData(Type tableType, RdfFilter filter, ScreenLayout screenLayout) {
		for (BlockLayout blockLayout: screenLayout.getBlockLayouts()) {
			if (blockLayout.getForType().equals(tableType)) {
				RdfTable rdfTable = this.loadRdfTable(screenLayout, filter, blockLayout);
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
	protected RdfTable loadRdfTable(ScreenLayout screenLayout, RdfFilter filter, BlockLayout blockLayout) {
		RdfTable rdfTable = new RdfTable();
		String query = this.constructTableQuery(blockLayout, screenLayout.getNamespaces(), screenLayout.getLineLayouts(), filter);
		ResultSet results = this.connector.query(query);
		rdfTable.setType(blockLayout.getForType());
		rdfTable.setInstances(new ArrayList<RdfInstance>());
		
		rdfTable.setColumns(new ArrayList<Property>());
		for (ColumnLayout columnLayout: blockLayout.getProperties()) {
			rdfTable.getColumns().add(columnLayout.getProperty());
		}
		
		while (results.hasNext()) {
			QuerySolution solution = results.next();
			RdfInstance instance = new RdfInstance(); 
			Uri subjectUri = new Uri(solution.get("?uri").asResource().getURI());
			instance.setUri(subjectUri);
			instance.setType(blockLayout.getForType());

			instance.setLiteralProperties(new ArrayList<RdfLiteralProperty>());
			if (blockLayout.getLabel().getType() == LabelType.PROPERTY) {
				RdfLiteralProperty rdfProperty = new RdfLiteralProperty();
				RDFNode rdfNode = solution.get("labelProperty");
				rdfProperty.setProperty(new Property(blockLayout.getLabel().getLabelSource()));
				rdfProperty.setValue(rdfNode.asLiteral().getString());
				instance.getLiteralProperties().add(rdfProperty);
			}
			int j = 0;
			for (ColumnLayout columnLayout: blockLayout.getProperties()) {
				String propertyName = "";
				if (columnLayout.getProperty().equals(Constants.RDFS_LABEL)) {
					propertyName = "typeLabel";
				} else {
					propertyName = "y" + j;
					j++;
				}
				
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

				String varTo = "y" + j;
				
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
	protected String constructTableQuery(BlockLayout blockLayout, Map<String, String> namespaces, List<LineLayout> lineLayouts, RdfFilter filter) {
		StringBuilder sb = new StringBuilder();
		for (String prefix: namespaces.keySet()) {
			String namespace = namespaces.get(prefix);
			sb.append("PREFIX ").append(prefix).append(": <").append(namespace).append("> ");
		}
		sb.append("SELECT DISTINCT * WHERE {{ ");
		
		sb.append(" ?uri rdf:type ").append(blockLayout.getForType()).append(" . ");
		sb.append(" ?uri ").append(Constants.RDFS_LABEL).append(" ?typeLabel . ");
		if (blockLayout.getLabel().getType() == LabelType.PROPERTY) {
			sb.append(" OPTIONAL { ?uri ").append(blockLayout.getLabel().getType()).append(" ?labelProperty . } ");
		}
		
		Map<Property, String> propertyVarMap = new HashMap<Property, String>();
		
		int j = 0;
		for (ColumnLayout columnLayout: blockLayout.getProperties()) {
			if (columnLayout.getProperty().equals(Constants.RDFS_LABEL)) {
				propertyVarMap.put(Constants.RDFS_LABEL, "typeLabel");
				continue;
			}
			String property = "y" + j;
			propertyVarMap.put(columnLayout.getProperty(), property);
			j++;
			sb.append(" OPTIONAL { ?uri ").append(columnLayout.getProperty()).append(" ?").append(property).append(" . } ");
		}
		
		for (LineLayout lineLayout: lineLayouts) {
			if (!(lineLayout.getFromType().equals(blockLayout.getForType()))) {
				continue;
			}
			String varTo = "y" + j;
			sb.append(" OPTIONAL { ?uri ").append(lineLayout.getProperty()).append(" ?").append(varTo).append(" . } ");
		}

		if (filter != null && filter.getUriFilters() != null && filter.getUriFilters().size() > 0) {
			for (Property uriProperty: filter.getUriFilters().keySet()) {
				Uri uriValue = filter.getUriFilters().get(uriProperty);
				if (uriValue == null) {
					continue;
				}
				sb.append("<").append(uriValue.getUri()).append("> ").append(uriProperty.getProperty()).append(" ?uri . ");
			}
		}
		
		sb.append("} FILTER (lang(?typeLabel) = 'en'");
		
		if (filter != null && filter.getColumnFilters() != null && filter.getColumnFilters().size() > 0) {
			for (Property property: filter.getColumnFilters().keySet()) {
				if (property == null) {
					continue;
				}
				String filterValue = filter.getColumnFilters().get(property);
				if (filterValue.isEmpty()) {
					continue;
				}
				sb.append(" && (regex(str(?").append(propertyVarMap.get(property)).append("), \"").append(filterValue).append("\")) ");
			}
		}
		
		sb.append(") ");
		
		int limit = LIMIT;
		if (filter != null && filter.getLimit() > 0) {
			limit = filter.getLimit();
		}
		sb.append("} ORDER BY ?uri LIMIT ").append(limit);
		return sb.toString();
	}
	
}
