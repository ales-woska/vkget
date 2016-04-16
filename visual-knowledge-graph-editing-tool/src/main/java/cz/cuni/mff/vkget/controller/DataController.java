package cz.cuni.mff.vkget.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cz.cuni.mff.vkget.data.model.DataModel;
import cz.cuni.mff.vkget.service.DataService;

@CrossOrigin(origins = "*")
@RestController
public class DataController {

	@Autowired
	private DataService dataService;

	@RequestMapping("/data/{url}")
	public DataModel loadData(@PathVariable String url) {
		// TODO load also other types of data
		DataModel dataModel = dataService.loadDataModel();
		return dataModel;
	}

}
