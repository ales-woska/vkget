package cz.cuni.mff.vkget.connect;

import cz.cuni.mff.vkget.data.layout.ScreenLayout;
import cz.cuni.mff.vkget.data.model.DataModel;
import cz.cuni.mff.vkget.data.model.RdfFilter;
import cz.cuni.mff.vkget.data.model.RdfTable;

/**
 * Interface for querying external SPARQL endpoints.
 * @author Ales Woska
 *
 */
public interface DataConnector {
	
	/**
	 * Loads whole data model defined in screenLayout.
	 * @param screenLayout Layout definition.
	 * @return
	 */
	DataModel loadDataModel(ScreenLayout screenLayout);

	/**
	 * Loads data model just for on RDF Type - one "table"
	 * @param tableType RDF Type for the "table"
	 * @param filter Table filter
	 * @param screenLayout Layout definition
	 * @return
	 */
	RdfTable loadTableData(String tableType, RdfFilter filter, ScreenLayout screenLayout);
}
