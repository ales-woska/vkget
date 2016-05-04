package cz.cuni.mff.vkget.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.cuni.mff.vkget.connect.CommonDataConnector;
import cz.cuni.mff.vkget.connect.DataConnector;
import cz.cuni.mff.vkget.connect.EndpointType;
import cz.cuni.mff.vkget.data.layout.GveTable;
import cz.cuni.mff.vkget.data.layout.ScreenLayout;
import cz.cuni.mff.vkget.data.model.DataModel;
import cz.cuni.mff.vkget.data.model.Graph;
import cz.cuni.mff.vkget.data.model.RdfObject;
import cz.cuni.mff.vkget.data.model.RdfObjectProperty;
import cz.cuni.mff.vkget.data.model.RdfProperty;
import cz.cuni.mff.vkget.data.model.RdfTriple;

@Service
public class DataServiceImpl implements DataService {
	
	@Autowired
	private LayoutService layoutService;

	@Override
	public DataModel loadDataModel(String endpoint, EndpointType endpointType, String layoutUri) {
		ScreenLayout screenLayout = layoutService.getLayout(layoutUri);
		
		DataConnector connector = null;
		switch (endpointType) {
			case virtuoso: connector = new CommonDataConnector(endpoint); break;
			case jena: connector = new CommonDataConnector(endpoint); break;
			default: connector = new CommonDataConnector(endpoint); break;
		}
		
		Graph graph = connector.loadGraph(screenLayout);
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
			rdfObject.setType(type);
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
