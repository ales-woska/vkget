package cz.cuni.mff.vkget.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cz.cuni.mff.vkget.data.layout.ScreenLayout;
import cz.cuni.mff.vkget.service.LayoutService;

@CrossOrigin(origins = "*")
@RestController
public class LayoutController {
	
	@Autowired
	private LayoutService service;

	@RequestMapping("/layouts")
	public List<ScreenLayout> getLayouts() {
		return service.getLayoutList();
	}

	@RequestMapping("/layout")
	public ScreenLayout getLayout(@RequestParam("uri") String uri) {
		return service.getLayout(uri);
	}

	@RequestMapping("/layout/new")
	public void saveLayout(ScreenLayout layout) {
		service.saveOrUpdateLayout(layout);
	}
	
}
