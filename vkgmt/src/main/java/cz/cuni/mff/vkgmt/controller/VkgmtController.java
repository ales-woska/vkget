package cz.cuni.mff.vkgmt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class VkgmtController {

	@RequestMapping("/index.html")
	public ModelAndView index() {
		return new ModelAndView("index");
	}
	
	@RequestMapping("/menu.html")
	public ModelAndView menu() {
		return new ModelAndView("menu");
	}
	
	@RequestMapping("/block_form.html")
	public ModelAndView blockForm() {
		return new ModelAndView("block_form");
	}
	
	@RequestMapping("/line_form.html")
	public ModelAndView lineForm() {
		return new ModelAndView("line_form");
	}
	
	@RequestMapping("/lines_svg.html")
	public ModelAndView linesSvg() {
		return new ModelAndView("lines_svg");
	}
	
}
