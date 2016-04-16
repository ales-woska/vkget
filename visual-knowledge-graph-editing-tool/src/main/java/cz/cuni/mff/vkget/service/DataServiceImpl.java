package cz.cuni.mff.vkget.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.stereotype.Service;

import cz.cuni.mff.vkget.connect.CityCountryConnector;
import cz.cuni.mff.vkget.data.layout.GveTable;
import cz.cuni.mff.vkget.data.model.DataModel;
import cz.cuni.mff.vkget.data.model.Graph;
import cz.cuni.mff.vkget.data.model.RdfObject;
import cz.cuni.mff.vkget.data.model.RdfObjectProperty;
import cz.cuni.mff.vkget.data.model.RdfProperty;
import cz.cuni.mff.vkget.data.model.RdfTriple;

@Service
public class DataServiceImpl implements DataService {

	@Override
	public DataModel loadDataModel() {
		Graph graph = new CityCountryConnector().loadCitiesWithCountries();
		DataModel dataModel = new DataModel();
		
		List<GveTable> tables = new ArrayList<GveTable>();
		for (String type: graph.getTypes()) {
			GveTable table = new GveTable(); 
			table.setTypeUri(type);
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
