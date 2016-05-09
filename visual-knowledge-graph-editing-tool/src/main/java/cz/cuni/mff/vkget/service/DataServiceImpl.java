package cz.cuni.mff.vkget.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.cuni.mff.vkget.connect.CommonDataConnector;
import cz.cuni.mff.vkget.connect.DataConnector;
import cz.cuni.mff.vkget.connect.EndpointType;
import cz.cuni.mff.vkget.data.layout.LineLayout;
import cz.cuni.mff.vkget.data.layout.ScreenLayout;
import cz.cuni.mff.vkget.data.model.DataModel;
import cz.cuni.mff.vkget.data.model.Graph;
import cz.cuni.mff.vkget.data.model.RdfTable;
import cz.cuni.mff.vkget.data.model.RdfChange;
import cz.cuni.mff.vkget.data.model.RdfInstance;
import cz.cuni.mff.vkget.data.model.RdfObjectProperty;
import cz.cuni.mff.vkget.data.model.RdfProperty;
import cz.cuni.mff.vkget.data.model.RdfTriple;

@Service
public class DataServiceImpl implements DataService {
	
	@Autowired
	private LayoutService layoutService;
	
	@Override
	public String generateUpdateScript(List<RdfChange> changes) {
		StringBuilder updates = new StringBuilder("UPDATE {\n");
		StringBuilder inserts = new StringBuilder("INSERT {\n");
		StringBuilder deletes = new StringBuilder("DELETE {\n");
		
		int updatesCount = 0;
		int insertsCount = 0;
		int deletesCount = 0;
		
		
		for (RdfChange change: changes) {
			boolean oldValueEmpty = change.getOldValue() == null || change.getOldValue().isEmpty();
			boolean newValueEmpty = change.getNewValue() == null || change.getNewValue().isEmpty();
			
			if (oldValueEmpty && !newValueEmpty) {
				insertsCount++;
				inserts.append("\t<").append(change.getObjectUri()).append("> ").append(change.getProperty());
				if (change.getNewValue().contains("http://")) {
					inserts.append(" <").append(change.getNewValue()).append("> .\n");
				} else {
					inserts.append(" '").append(change.getNewValue()).append("' .\n");
				}
			} else if (newValueEmpty && !oldValueEmpty) {
				deletesCount++;
				deletes.append("\t<").append(change.getObjectUri()).append("> ").append(change.getProperty());
				if (change.getOldValue().contains("http://")) {
					deletes.append(" <").append(change.getOldValue()).append("> .\n");
				} else {
					deletes.append(" '").append(change.getOldValue()).append("' .\n");
				}
			} else {
				updatesCount++;
				updates.append("\t<").append(change.getObjectUri()).append("> ").append(change.getProperty());
				if (change.getNewValue().contains("http://")) {
					updates.append(" <").append(change.getNewValue()).append("> .\n");
				} else {
					updates.append(" '").append(change.getNewValue()).append("' .\n");
				}
			}
		}
		updates.append("}\n");
		inserts.append("}\n");
		deletes.append("}\n");
		
		StringBuilder result = new StringBuilder();
		if (updatesCount > 0) {
			result.append(updates.toString());
		}
		if (insertsCount > 0) {
			result.append(inserts.toString());
		}
		if (deletesCount > 0) {
			result.append(deletes.toString());
		}
		return result.toString();
	}

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
		
		List<RdfTable> tables = new ArrayList<RdfTable>();
		for (String type: graph.getTypes()) {
			RdfTable table = new RdfTable(); 
			table.setTypeUri(type);
			table.setColumnsURIs(graph.getAllClassProperties(type));
			table.setInstances(getRdfObjects(graph, type));
			tables.add(table);
		}
		dataModel.setTables(tables);
		
		for (LineLayout lineLayout: screenLayout.getLineLayouts()) {
			fillRdfObjectProperties(dataModel, graph, lineLayout);
		}
		
		return dataModel;
	}
	
	private void fillRdfObjectProperties(DataModel dataModel, Graph graph, LineLayout lineLayout) {
		RdfTable sourceTable = dataModel.getTableByType(lineLayout.getFromType());
		RdfTable targetTable = dataModel.getTableByType(lineLayout.getToType());
				
		if (sourceTable == null || targetTable == null) {
			return;
		}
		
		for (RdfTriple triple: graph.getRdfTriplesByType(sourceTable.getTypeUri())) {
			RdfInstance rdfObject = sourceTable.getInstanceByUri(triple.getUri());
			if (rdfObject == null) {
				continue;
			}
			
			rdfObject.setObjectProperties(new ArrayList<RdfObjectProperty>());
			for (Entry<String, Object> e: triple.getProperties().entrySet()) {
				String property = e.getKey();
				Object value = e.getValue();

				if (value instanceof RdfTriple) {
					RdfObjectProperty rdfProperty = new RdfObjectProperty();
					rdfProperty.setSubjectUri(rdfObject.getObjectURI());
					rdfProperty.setProperty(property);
					rdfProperty.setObjectUri(((RdfTriple) value).getUri());
					rdfObject.getObjectProperties().add(rdfProperty);
				}
				
			}
		}
		
	}
	
	private List<RdfInstance> getRdfObjects(Graph graph, String type) {
		List<RdfInstance> rdfObjects = new ArrayList<RdfInstance>();
		
		for (RdfTriple triple: graph.getRdfTriplesByType(type)) {
			RdfInstance rdfObject = new RdfInstance();
			rdfObject.setObjectURI(triple.getUri());
			rdfObject.setType(type);
			
			rdfObject.setLiteralProperties(new ArrayList<RdfProperty>());
			for (Entry<String, Object> e: triple.getProperties().entrySet()) {
				String property = e.getKey();
				Object value = e.getValue();

				if (value instanceof RdfTriple) {
					continue;
					
				}
				
				RdfProperty rdfProperty = new RdfProperty();
				rdfProperty.setPropertyURI(property);
				rdfProperty.setValue(value);
				rdfObject.getLiteralProperties().add(rdfProperty);
			}

			rdfObjects.add(rdfObject);
		}
		
		return rdfObjects;
	}
	
}