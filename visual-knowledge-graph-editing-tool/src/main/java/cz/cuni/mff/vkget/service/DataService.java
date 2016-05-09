package cz.cuni.mff.vkget.service;

import java.util.List;

import cz.cuni.mff.vkget.connect.EndpointType;
import cz.cuni.mff.vkget.data.model.DataModel;
import cz.cuni.mff.vkget.data.model.RdfChange;
import cz.cuni.mff.vkget.data.model.RdfFilter;
import cz.cuni.mff.vkget.data.model.RdfInstance;

public interface DataService {

	DataModel loadDataModel(String endpoint, EndpointType type, String layoutUri);

	String generateUpdateScript(List<RdfChange> changes);

	List<RdfInstance> loadTableData(String tableType, RdfFilter filter, String endpoint, EndpointType endpointType, String layoutUri);
}
