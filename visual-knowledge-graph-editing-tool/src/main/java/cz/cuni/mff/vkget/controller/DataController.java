package cz.cuni.mff.vkget.controller;

import java.util.List;

import cz.cuni.mff.vkget.connect.EndpointType;
import cz.cuni.mff.vkget.data.common.Uri;
import cz.cuni.mff.vkget.data.model.DataModel;
import cz.cuni.mff.vkget.data.model.LoadTableData;
import cz.cuni.mff.vkget.data.model.RdfChange;
import cz.cuni.mff.vkget.data.model.RdfInstance;

/**
 * Controller for business data operatins.
 * @author Ales Woska
 *
 */
public interface DataController {

	/**
	 * Loads whole data model
	 * @param endpoint
	 * @param type
	 * @param layoutUri
	 * @return
	 */
	public DataModel loadData(String endpoint, EndpointType type, Uri layoutUri);
	
	/**
	 * Loads new instances by given filter within loadTableData
	 * @param loadTableData
	 * @return
	 */
	public List<RdfInstance> loadTableData(LoadTableData loadTableData);
	
	/**
	 * Generates SPARQL update script for given changes.
	 * @param changes
	 * @return
	 */
	public String generateUpdateScript(List<RdfChange> changes);

}