package cz.cuni.mff.vkgmt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LayoutController {
 
	@RequestMapping("/layout")
	public ModelAndView layoutList() {
		String message = "";
		return new ModelAndView("layout", "message", message);
	}
	
	@RequestMapping("/edit_layout")
	public ModelAndView editLayout() {
		String message = "";
		return new ModelAndView("edit_layout", "message", message);
	}
}