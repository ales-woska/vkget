package cz.cuni.mff.vkgmt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.cuni.mff.vkgmt.data.common.Uri;
import cz.cuni.mff.vkgmt.data.layout.ScreenLayout;
import cz.cuni.mff.vkgmt.persistence.ScreenLayoutDao;
import cz.cuni.mff.vkgmt.service.LayoutService;

/**
 * Implementation of @see LayoutService.
 * @author Ales Woska
 *
 */
@Service
public class LayoutServiceImpl implements LayoutService {

	@Autowired
	private ScreenLayoutDao dao;
	
	/**
	 * @inheritDoc
	 */
	@Override
	public List<ScreenLayout> getLayoutList() {
		return dao.getAll();
	}
	
	/**
	 * @inheritDoc
	 */
	@Override
	public ScreenLayout getLayout(Uri uri) {
		return dao.get(uri);
	}
	
	/**
	 * @inheritDoc
	 */
	@Override
	public void saveOrUpdateLayout(ScreenLayout layout) {
		dao.insertOrUpdate(layout, null);
	}
	
	/**
	 * @inheritDoc
	 */
	@Override
	public void removeLayout(ScreenLayout layout) {
		dao.delete(layout);
	}
	
}
