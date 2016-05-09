package cz.cuni.mff.vkget.controller;

import java.util.List;

import cz.cuni.mff.vkget.data.layout.ScreenLayout;

/**
 * Controller for layout operations
 * @author Ales Woska
 *
 */
public interface LayoutController {
	
	/**
	 * Gets list of all layouts.
	 * @return
	 */
	List<ScreenLayout> getLayouts();

	/**
	 * Gets etail of the layout with given uri/
	 * @param uri
	 * @return
	 */
	ScreenLayout getLayout(String uri);

	/**
	 * Saves or updates layout.
	 * @param layout
	 */
	void saveLayout(ScreenLayout layout);
	
}
