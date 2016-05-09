package cz.cuni.mff.vkget.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import cz.cuni.mff.vkget.connect.EndpointType;
import cz.cuni.mff.vkget.data.model.DataModel;
import cz.cuni.mff.vkget.data.model.LoadTableData;
import cz.cuni.mff.vkget.data.model.RdfChange;
import cz.cuni.mff.vkget.data.model.RdfInstance;
import cz.cuni.mff.vkget.service.DataService;

/**
 * Implementation of @see DataController
 * @author Ales Woska
 *
 */
@CrossOrigin(origins = "*")
@RestController
public class DataControllerImpl implements DataController {

	@Autowired
	private DataService dataService;

	/**
	 * @inheritDoc
	 */
	@Override
	@RequestMapping("/data")
	public DataModel loadData(@RequestParam("endpoint") String endpoint, @RequestParam("type") EndpointType type, @RequestParam("layoutUri") String layoutUri) {
		DataModel dataModel = dataService.loadDataModel(endpoint, type, layoutUri);
		return dataModel;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	@ResponseBody
	@RequestMapping(value = "/data/table", method = RequestMethod.POST)
	public List<RdfInstance> loadTableData(@RequestBody LoadTableData loadTableData) {
		List<RdfInstance> instances = dataService.loadTableData(loadTableData.getTableType(), loadTableData.getFilter(), loadTableData.getEndpoint(), loadTableData.getType(), loadTableData.getLayoutUri());
		return instances;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	@ResponseBody
	@RequestMapping(value = "/changes", method = RequestMethod.POST, produces = "text/plain; charset=utf-8")
	public String generateUpdateScript(@RequestBody List<RdfChange> changes) {
		String updateScript = dataService.generateUpdateScript(changes);
		return updateScript;
	}

}