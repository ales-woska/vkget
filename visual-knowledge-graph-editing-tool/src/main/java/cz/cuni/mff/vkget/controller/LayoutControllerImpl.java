package cz.cuni.mff.vkget.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cz.cuni.mff.vkget.data.common.Uri;
import cz.cuni.mff.vkget.data.layout.ScreenLayout;
import cz.cuni.mff.vkget.service.LayoutService;

/**
 * Implementation of @see LayoutController.
 * @author Ales Woska
 *
 */
@CrossOrigin(origins = "*")
@RestController
public class LayoutControllerImpl implements LayoutController {
	
	@Autowired
	private LayoutService service;

	/**
	 * @inheritDoc
	 */
	@Override
	@RequestMapping("/layouts")
	public List<ScreenLayout> getLayouts() {
		return service.getLayoutList();
	}

	/**
	 * @inheritDoc
	 */
	@Override
	@RequestMapping("/layout")
	public ScreenLayout getLayout(@RequestParam("uri") String uri) {
		ScreenLayout layout = service.getLayout(new Uri(uri));
		return layout;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	@RequestMapping(value = "/layout/save", method = RequestMethod.POST)
	public void saveLayout(@RequestBody ScreenLayout layout) {
		service.saveOrUpdateLayout(layout);
	}
	
	/**
	 * @inheritDoc
	 */
	@Override
	@RequestMapping(value = "/layout/remove", method = RequestMethod.POST)
	public void removeLayout(@RequestBody ScreenLayout layout) {
		service.removeLayout(layout);
	}
	
}
