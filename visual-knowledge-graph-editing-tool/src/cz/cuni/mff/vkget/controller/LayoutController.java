package cz.cuni.mff.vkget.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import cz.cuni.mff.vkget.layout.ScreenLayout;
import cz.cuni.mff.vkget.persistence.ScreenLayoutDao;

@Controller
public class LayoutController {
	private ScreenLayoutDao layoutDao = new ScreenLayoutDao();

	@RequestMapping(path = "/layout")
	public ModelAndView editLayout(@RequestParam(value = "uri", required = false) String uri) {
		if (uri == null || uri.isEmpty()) {
			List<ScreenLayout> layouts = layoutDao.loadScreenLayouts();
			return new ModelAndView("layout", "layouts", layouts);
			
		} else {
			ScreenLayout screenLayout = layoutDao.loadScreenLayouts().get(0);
			ModelAndView model = new ModelAndView("edit_layout");
			model.addObject("uri", uri);
			model.addObject("screenLayout", screenLayout);
			return model;
		}
	}

	@RequestMapping(path = "/layout/new")
	public ModelAndView showNewLayoutForm() {
		return new ModelAndView("edit_layout");
	}
}
