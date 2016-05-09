package cz.cuni.mff.vkget.controller;

import java.util.List;

import cz.cuni.mff.vkget.connect.EndpointType;
import cz.cuni.mff.vkget.data.model.DataModel;
import cz.cuni.mff.vkget.data.model.LoadTableData;
import cz.cuni.mff.vkget.data.model.RdfChange;
import cz.cuni.mff.vkget.data.model.RdfInstance;

public interface DataController {

	public DataModel loadData(String endpoint, EndpointType type, String layoutUri);
	
	public List<RdfInstance> loadTableData(LoadTableData loadTableData);
	
	public String generateUpdateScript(List<RdfChange> changes);

}