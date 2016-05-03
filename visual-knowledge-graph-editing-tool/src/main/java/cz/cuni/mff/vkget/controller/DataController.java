package cz.cuni.mff.vkget.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cz.cuni.mff.vkget.data.model.DataModel;
import cz.cuni.mff.vkget.service.DataService;

@CrossOrigin(origins = "*")
@RestController
public class DataController {

	@Autowired
	private DataService dataService;

	@RequestMapping("/data")
	public DataModel loadData(@RequestParam("endpoint") String endpoint, @RequestParam("type") String type, @RequestParam("layoutUri") String layoutUri) {
		DataModel dataModel = dataService.loadDataModel(endpoint, type, layoutUri);
		return dataModel;
	}

}
