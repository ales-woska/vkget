package cz.cuni.mff.vkget.controller;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import cz.cuni.mff.vkget.data.common.Uri;
import cz.cuni.mff.vkget.data.layout.ScreenLayout;
import cz.cuni.mff.vkget.service.LayoutService;

public class LayoutControllerTest {
	
	private LayoutController controller;
	private LayoutService serviceMock;
	
	@Before
	public void init() {
		serviceMock = EasyMock.createMock(LayoutService.class);
		controller = new LayoutController();
		controller.setService(serviceMock);
	}
	
	@Test
	public void getLayoutsTest() {
		List<ScreenLayout> layouts = new ArrayList<ScreenLayout>();
		
		EasyMock.expect(serviceMock.getLayoutList()).andReturn(layouts);
		EasyMock.replay(serviceMock);
		
		List<ScreenLayout> result = controller.getLayouts();
		assertEquals(layouts, result);
		
		EasyMock.verify(serviceMock);
	}

	@Test
	public void getLayoutTest() {
		String uriString = "http://some-uri#aaa";
		Uri uri = new Uri(uriString);
		ScreenLayout screenLayout = new ScreenLayout();

		EasyMock.expect(serviceMock.getLayout(uri)).andReturn(screenLayout);
		EasyMock.replay(serviceMock);
		
		ScreenLayout result = controller.getLayout(uriString);
		assertEquals(screenLayout, result);
		
		EasyMock.verify(serviceMock);
	}

	@Test
	public void saveLayout() {
		ScreenLayout screenLayout = new ScreenLayout();

		serviceMock.saveOrUpdateLayout(screenLayout);
		EasyMock.expectLastCall();
		EasyMock.replay(serviceMock);
		
		controller.saveLayout(screenLayout);
		
		EasyMock.verify(serviceMock);
	}

	@Test
	public void removeLayout() {
		ScreenLayout screenLayout = new ScreenLayout();

		serviceMock.removeLayout(screenLayout);
		EasyMock.expectLastCall();
		EasyMock.replay(serviceMock);
		
		controller.removeLayout(screenLayout);
		
		EasyMock.verify(serviceMock);
	}

}
