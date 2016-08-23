package cz.cuni.mff.vkgmt.service;

import java.util.List;

import cz.cuni.mff.vkgmt.data.common.Uri;
import cz.cuni.mff.vkgmt.data.layout.ScreenLayout;

/**
 * Interface for working with layouts.
 * @author Ales Woska
 *
 */
public interface LayoutService {

	/**
	 * Loads list of all screen layouts, without subcollections - they're null.
	 * @return
	 */
	List<ScreenLayout> getLayoutList();

	/**
	 * Return detail of screen layous including all subcollections.
	 * @param uri
	 * @return
	 */
	ScreenLayout getLayout(Uri uri);

	/**
	 * Saves changes into storage and creates record if doesn't exists.
	 * @param layout
	 */
	void saveOrUpdateLayout(ScreenLayout layout);

	/**
	 * Removes layout
	 * @param layout
	 */
	void removeLayout(ScreenLayout layout);

}
