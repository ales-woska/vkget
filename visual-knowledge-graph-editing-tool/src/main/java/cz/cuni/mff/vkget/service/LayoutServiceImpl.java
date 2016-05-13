package cz.cuni.mff.vkget.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.cuni.mff.vkget.data.common.Uri;
import cz.cuni.mff.vkget.data.layout.ScreenLayout;
import cz.cuni.mff.vkget.persistence.ScreenLayoutDao;

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
		if (dao.exists(layout)) {
			dao.update(layout);
		} else {
			dao.insert(layout);
		}
	}
	
	/**
	 * @inheritDoc
	 */
	@Override
	public void removeLayout(ScreenLayout layout) {
		dao.delete(layout);
	}
	
}
