package cz.cuni.mff.vkget.connect;

import cz.cuni.mff.vkget.data.layout.ScreenLayout;
import cz.cuni.mff.vkget.data.model.Graph;

public interface DataConnector {
	
	Graph loadGraph(ScreenLayout screenLayout);
}
