package cz.cuni.mff.vkget.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import cz.cuni.mff.vkget.data.DataModel;
import cz.cuni.mff.vkget.layout.ScreenLayout;
import cz.cuni.mff.vkget.loader.DataModelLoader;
import cz.cuni.mff.vkget.persistence.ScreenLayoutDao;
import cz.cuni.mff.vkget.rdf.CityCountryConnector;
import cz.cuni.mff.vkget.rdf.Graph;
import cz.cuni.mff.vkget.render.HtmlRenderer;

@Controller
public class WorkController {
	
	private CityCountryConnector cacConnector = new CityCountryConnector();
	private DataModelLoader dataLoader = new DataModelLoader();
	private HtmlRenderer renderer = new HtmlRenderer();
	private ScreenLayoutDao layoutDao = new ScreenLayoutDao();

	@RequestMapping("/work")
	public ModelAndView showWorkbench() {
		Graph cac = cacConnector.loadCitiesWithCountries();
		
		ScreenLayout screenLayout = layoutDao.loadScreenLayouts().get(0);
		
		DataModel dataModel = dataLoader.loadDataModel(cac);
		String tables = renderer.render(dataModel, screenLayout);
		
		return new ModelAndView("work", "tables", tables);
	}
}
