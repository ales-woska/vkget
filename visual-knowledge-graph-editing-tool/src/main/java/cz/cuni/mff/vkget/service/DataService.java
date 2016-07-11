package cz.cuni.mff.vkget.service;

import java.util.List;

import cz.cuni.mff.vkget.connect.ConnectionInfo;
import cz.cuni.mff.vkget.data.common.Type;
import cz.cuni.mff.vkget.data.common.Uri;
import cz.cuni.mff.vkget.data.model.DataModel;
import cz.cuni.mff.vkget.data.model.RdfChange;
import cz.cuni.mff.vkget.data.model.RdfFilter;
import cz.cuni.mff.vkget.data.model.RdfInstance;

/**
 * Service intergace for business data operations.
 * @author Ales Woska
 *
 */
public interface DataService {

	/**
	 * Loads whole data model.
	 * @param connectionInfo
	 * @param layoutUri
	 * @return
	 */
	DataModel loadDataModel(ConnectionInfo connectionInfo, Uri layoutUri);

	/**
	 * Generates SPARQL update script from changes.
	 * @param changes
	 * @return
	 */
	String generateUpdateScript(List<RdfChange> changes);

	/**
	 * Load concrete table.
	 * @param tableType
	 * @param filter
	 * @param connectionInfo
	 * @param layoutUri
	 * @return
	 */
	List<RdfInstance> loadTableData(Type tableType, RdfFilter filter, ConnectionInfo connectionInfo, Uri layoutUri);
}
