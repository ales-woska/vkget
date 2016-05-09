package cz.cuni.mff.vkget.controller;

import java.util.List;

import cz.cuni.mff.vkget.data.layout.ScreenLayout;

public interface LayoutController {
	
	List<ScreenLayout> getLayouts();

	ScreenLayout getLayout(String uri);

	void saveLayout(ScreenLayout layout);
	
}
