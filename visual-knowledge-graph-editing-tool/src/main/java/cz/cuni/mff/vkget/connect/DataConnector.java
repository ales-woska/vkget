package cz.cuni.mff.vkget.connect;

import cz.cuni.mff.vkget.data.layout.ScreenLayout;
import cz.cuni.mff.vkget.data.model.DataModel;

public interface DataConnector {
	
	DataModel loadDataModel(ScreenLayout screenLayout);
}
