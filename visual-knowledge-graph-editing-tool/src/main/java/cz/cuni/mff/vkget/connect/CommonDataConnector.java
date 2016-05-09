package cz.cuni.mff.vkget.connect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.springframework.stereotype.Service;

import cz.cuni.mff.vkget.data.layout.BlockLayout;
import cz.cuni.mff.vkget.data.layout.LineLayout;
import cz.cuni.mff.vkget.data.layout.RowLayout;
import cz.cuni.mff.vkget.data.layout.ScreenLayout;
import cz.cuni.mff.vkget.data.layout.TitleType;
import cz.cuni.mff.vkget.data.model.DataModel;
import cz.cuni.mff.vkget.data.model.RdfFilter;
import cz.cuni.mff.vkget.data.model.RdfInstance;
import cz.cuni.mff.vkget.data.model.RdfObjectProperty;
import cz.cuni.mff.vkget.data.model.RdfProperty;
import cz.cuni.mff.vkget.data.model.RdfTable;

@Service
public class CommonDataConnector implements DataConnector {
	
	private SparqlConnector connector;
	private static final int LIMIT = 40;
	
	public CommonDataConnector() {}
	
	public CommonDataConnector(String endpoint) {
		connector = new SparqlConnector(endpoint);
	}
	
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

	@Override
	public RdfTable loadTableData(String tableType, RdfFilter filter, ScreenLayout screenLayout) {
		for (BlockLayout blockLayout: screenLayout.getBlockLayouts()) {
			if (blockLayout.getForType().equals(tableType)) {
				RdfTable rdfTable = this.loadRdfTable(screenLayout, filter, blockLayout);
				return rdfTable;
			}
		}
		return null;
	}
	
	private RdfTable loadRdfTable(ScreenLayout screenLayout, RdfFilter filter, BlockLayout blockLayout) {
		RdfTable rdfTable = new RdfTable();
		String query = this.constructTableQuery(blockLayout, screenLayout.getNamespaces(), screenLayout.getLineLayouts(), filter);
		ResultSet results = this.connector.query(query);
		rdfTable.setTypeUri(blockLayout.getForType());
		rdfTable.setInstances(new ArrayList<RdfInstance>());
		
		rdfTable.setColumnsURIs(new ArrayList<String>());
		for (RowLayout rowLayout: blockLayout.getProperties()) {
			rdfTable.getColumnsURIs().add(rowLayout.getProperty());
		}
		
		while (results.hasNext()) {
			QuerySolution solution = results.next();
			RdfInstance instance = new RdfInstance(); 
			String objectUri = solution.get("?uri").asResource().getURI();
			instance.setObjectURI(objectUri);
			instance.setType(blockLayout.getForType());

			instance.setLiteralProperties(new ArrayList<RdfProperty>());
			if (blockLayout.getTitleTypes().get(0) == TitleType.PROPERTY) {
				RdfProperty rdfProperty = new RdfProperty();
				RDFNode rdfNode = solution.get("labelProperty");
				rdfProperty.setPropertyURI(blockLayout.getTitle());
				rdfProperty.setValue(rdfNode.asLiteral().getString());
				instance.getLiteralProperties().add(rdfProperty);
			}
			int j = 0;
			for (RowLayout rowLayout: blockLayout.getProperties()) {
				String property = "";
				if (rowLayout.getProperty().equals("rdfs:label")) {
					property = "typeLabel";
				} else {
					property = "y" + j;
					j++;
				}
				
				RDFNode rdfNode = solution.get(property);
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
				String propertyUri = rowLayout.getProperty();
				
				RdfProperty rdfProperty = new RdfProperty();
				rdfProperty.setPropertyURI(propertyUri);
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
				String uri = null;
				if (rdfNode != null) {
					uri = rdfNode.asResource().getURI();
				}
				
				RdfObjectProperty rdfObjectProperty = new RdfObjectProperty();
				rdfObjectProperty.setSubjectUri(objectUri);
				rdfObjectProperty.setProperty(lineLayout.getProperty());
				rdfObjectProperty.setObjectUri(uri);
				instance.getObjectProperties().add(rdfObjectProperty);
			}
			rdfTable.getInstances().add(instance);
		}
		return rdfTable;
	}
	
	private String constructTableQuery(BlockLayout blockLayout, Map<String, String> namespaces, List<LineLayout> lineLayouts, RdfFilter filter) {
		StringBuilder sb = new StringBuilder();
		for (String prefix: namespaces.keySet()) {
			String namespace = namespaces.get(prefix);
			sb.append("PREFIX ").append(prefix).append(": <").append(namespace).append("> ");
		}
		sb.append("SELECT DISTINCT * WHERE {{ ");
		
		sb.append(" ?uri rdf:type ").append(blockLayout.getForType()).append(" . ");
		sb.append(" ?uri rdfs:label ?typeLabel . ");
		if (blockLayout.getTitleTypes().get(0) == TitleType.PROPERTY) {
			sb.append(" OPTIONAL { ?uri ").append(blockLayout.getTitle()).append(" ?labelProperty . } ");
		}
		
		Map<String, String> propertyVarMap = new HashMap<String, String>();
		
		int j = 0;
		for (RowLayout rowLayout: blockLayout.getProperties()) {
			if (rowLayout.getProperty().equals("rdfs:label")) {
				propertyVarMap.put("rdfs:label", "typeLabel");
				continue;
			}
			String property = "y" + j;
			propertyVarMap.put(rowLayout.getProperty(), property);
			j++;
			sb.append(" OPTIONAL { ?uri ").append(rowLayout.getProperty()).append(" ?").append(property).append(" . } ");
		}
		
		for (LineLayout lineLayout: lineLayouts) {
			if (!(lineLayout.getFromType().equalsIgnoreCase(blockLayout.getForType()))) {
				continue;
			}
			String varTo = "y" + j;
			sb.append(" OPTIONAL { ?uri ").append(lineLayout.getProperty()).append(" ?").append(varTo).append(" . } ");
		}
		
		StringBuilder filterSb = new StringBuilder("lang(?typeLabel) = 'en'");

		if (filter != null && filter.getUriFilters() != null && filter.getUriFilters().size() > 0) {
			filterSb.append(" && (");
			for (String uriFilter: filter.getUriFilters()) {
				if (uriFilter.isEmpty()) {
					continue;
				}
				if (!filter.getUriFilters().get(0).equals(uriFilter)) {
					filterSb.append("or ?uri = <").append(uriFilter).append("> ");
				} else {
					filterSb.append("?uri = <").append(uriFilter).append("> ");
				}
			}
			filterSb.append(") ");
		}
		
		if (filter != null && filter.getColumnFilters() != null && filter.getColumnFilters().size() > 0) {
			for (String property: filter.getColumnFilters().keySet()) {
				if (property.isEmpty()) {
					continue;
				}
				String filterValue = filter.getColumnFilters().get(property);
				if (filterValue.isEmpty()) {
					continue;
				}
				filterSb.append(" && (regex(str(?").append(propertyVarMap.get(property)).append("), \"").append(filterValue).append("\")) ");
			}
		}
		
		sb.append("} FILTER (").append(filterSb.toString()).append(") ");
		
		int limit = LIMIT;
		if (filter != null && filter.getLimit() > 0) {
			limit = filter.getLimit();
		}
		sb.append("} ORDER BY ?uri LIMIT ").append(limit);
		return sb.toString();
	}
	
}
