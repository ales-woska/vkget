package cz.cuni.mff.vkget.service;

import java.util.List;

import cz.cuni.mff.vkget.data.layout.ScreenLayout;

public interface LayoutService {

	List<ScreenLayout> getLayoutList();

	ScreenLayout getLayout(String uri);

	void saveOrUpdateLayout(ScreenLayout layout);

}
