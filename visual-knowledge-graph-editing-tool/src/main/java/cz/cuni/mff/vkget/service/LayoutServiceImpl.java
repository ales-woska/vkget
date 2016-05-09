package cz.cuni.mff.vkget.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
		return dao.loadScreenLayouts();
	}
	
	/**
	 * @inheritDoc
	 */
	@Override
	public ScreenLayout getLayout(String uri) {
		return dao.load(uri);
	}
	
	/**
	 * @inheritDoc
	 */
	@Override
	public void saveOrUpdateLayout(ScreenLayout layout) {
		dao.insert(layout);
	}
	
}
