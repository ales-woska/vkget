package cz.cuni.mff.vkget.loader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import cz.cuni.mff.vkget.data.DataModel;
import cz.cuni.mff.vkget.data.RdfObject;
import cz.cuni.mff.vkget.data.RdfObjectProperty;
import cz.cuni.mff.vkget.data.RdfProperty;
import cz.cuni.mff.vkget.layout.GveTable;
import cz.cuni.mff.vkget.rdf.Graph;
import cz.cuni.mff.vkget.rdf.RdfTriple;

public class DataModelLoader {

	public DataModel loadDataModel(Graph graph) {
		DataModel dataModel = new DataModel();
		
		List<GveTable> tables = new ArrayList<GveTable>();
		for (String type: graph.getTypes()) {
			GveTable table = new GveTable(); 
			table.setClassUri(type);
			table.setColumnsURIs(graph.getAllClassProperties(type));
			table.setInstances(getRdfObjects(graph, type));
			tables.add(table);
		}
		dataModel.setTables(tables);
		return dataModel;
	}
	
	private List<RdfObject> getRdfObjects(Graph graph, String type) {
		List<RdfObject> rdfObjcets = new ArrayList<RdfObject>();
		
		for (RdfTriple triple: graph.getRdfTriplesByType(type)) {
			RdfObject rdfObject = new RdfObject();
			rdfObject.setObjectURI(triple.getUri());
			rdfObject.setLiteralProperties(new ArrayList<RdfProperty>());
			rdfObject.setObjectProperties(new ArrayList<RdfObjectProperty>());
			for (Entry<String, Object> e: triple.getProperties().entrySet()) {
				String property = e.getKey();
				Object value = e.getValue();
				RdfProperty rdfProperty = new RdfProperty();
				rdfProperty.setPropertyURI(property);
				rdfProperty.setValue(value);
				rdfObject.getLiteralProperties().add(rdfProperty);
			}
			// TODO object properties
			rdfObjcets.add(rdfObject);
		}
		return rdfObjcets;
	}
	
}
