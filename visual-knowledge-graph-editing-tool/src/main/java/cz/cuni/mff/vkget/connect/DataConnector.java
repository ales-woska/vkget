package cz.cuni.mff.vkget.connect;

import cz.cuni.mff.vkget.data.layout.ScreenLayout;
import cz.cuni.mff.vkget.data.model.DataModel;
import cz.cuni.mff.vkget.data.model.RdfFilter;
import cz.cuni.mff.vkget.data.model.RdfTable;

public interface DataConnector {
	
	DataModel loadDataModel(ScreenLayout screenLayout);

	RdfTable loadTableData(String tableType, RdfFilter filter, ScreenLayout screenLayout);
}
