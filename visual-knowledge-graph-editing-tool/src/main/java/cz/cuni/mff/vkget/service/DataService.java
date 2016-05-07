package cz.cuni.mff.vkget.service;

import java.util.List;

import cz.cuni.mff.vkget.connect.EndpointType;
import cz.cuni.mff.vkget.data.model.DataModel;
import cz.cuni.mff.vkget.data.model.RdfChange;

public interface DataService {

	DataModel loadDataModel(String endpoint, EndpointType type, String layoutUri);

	String generateUpdateScript(List<RdfChange> changes);
}
