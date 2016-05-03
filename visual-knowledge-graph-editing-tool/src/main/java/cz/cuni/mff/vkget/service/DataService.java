package cz.cuni.mff.vkget.service;

import cz.cuni.mff.vkget.connect.EndpointType;
import cz.cuni.mff.vkget.data.model.DataModel;

public interface DataService {
	
	 DataModel loadDataModel(String endpoint, EndpointType type, String layoutUri);
}
