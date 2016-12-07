package cz.cuni.mff.vkgmt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class VkgmtController {

	@RequestMapping("/index.html")
	public ModelAndView helloWorld() {
		return new ModelAndView("index");
	}
	
}
