package cz.cuni.mff.vkget.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import cz.cuni.mff.vkget.data.DataModel;
import cz.cuni.mff.vkget.layout.ScreenLayout;
import cz.cuni.mff.vkget.loader.DataModelLoader;
import cz.cuni.mff.vkget.persistence.ScreenLayoutDao;
import cz.cuni.mff.vkget.render.HtmlRenderer;

@Controller
public class WorkController {

	@Autowired
	private DataModelLoader dataLoader ;
	
	@Autowired
	private HtmlRenderer renderer;

	@Autowired
	private ScreenLayoutDao layoutDao;

	@RequestMapping("/work")
	public ModelAndView showWorkbench() {
		ScreenLayout screenLayout = layoutDao.loadScreenLayouts().get(0);
		DataModel dataModel = dataLoader.loadDataModel();
		String tables = renderer.render(dataModel, screenLayout);
		return new ModelAndView("work", "tables", tables);
	}
}
