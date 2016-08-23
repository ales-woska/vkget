package cz.cuni.mff.vkgmt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class WorkController {
 
	@RequestMapping("/work")
	public ModelAndView work() {
		String message = "";
		return new ModelAndView("work", "message", message);
	}
}