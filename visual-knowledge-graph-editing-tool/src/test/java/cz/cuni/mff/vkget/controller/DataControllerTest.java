package cz.cuni.mff.vkget.controller;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import cz.cuni.mff.vkget.connect.EndpointType;
import cz.cuni.mff.vkget.data.common.Type;
import cz.cuni.mff.vkget.data.common.Uri;
import cz.cuni.mff.vkget.data.model.DataModel;
import cz.cuni.mff.vkget.data.model.LoadTableData;
import cz.cuni.mff.vkget.data.model.RdfChange;
import cz.cuni.mff.vkget.data.model.RdfFilter;
import cz.cuni.mff.vkget.data.model.RdfInstance;
import cz.cuni.mff.vkget.service.DataService;

public class DataControllerTest {
	
	private DataService serviceMock;
	private DataController controller;
	
	@Before
	public void init() {
		serviceMock = EasyMock.createMock(DataService.class);
		controller = new DataController();
		controller.setDataService(serviceMock);
	}
	
	@Test
	public void loadDataTest() {
		String endpoint = "http://some-endpoint.com:123";
		EndpointType type = EndpointType.jena;
		Uri layoutUri = new Uri("http://some-uri#aaa");
		
		DataModel model = new DataModel();

		EasyMock.expect(serviceMock.loadDataModel(endpoint, type, layoutUri)).andReturn(model);
		EasyMock.replay(serviceMock);
		
		DataModel result = controller.loadData(endpoint, type, layoutUri);
		assertEquals(model, result);
		
		EasyMock.verify(serviceMock);
	}
	
	@Test
	public void loadTableDataTest() {
		LoadTableData loadTableData = new LoadTableData();
		Type tableType = new Type("", "");
		RdfFilter filter = new RdfFilter();
		String endpoint = "http://some-endpoint.com:123";
		EndpointType type = EndpointType.jena;
		Uri layoutUri = new Uri("http://some-uri#aaa");
		loadTableData.setTableType(tableType);
		loadTableData.setFilter(filter);
		loadTableData.setEndpoint(endpoint);
		loadTableData.setType(type);
		loadTableData.setLayoutUri(layoutUri);
		
		List<RdfInstance> instances = new ArrayList<RdfInstance>();
		
		EasyMock.expect(serviceMock.loadTableData(tableType, filter, endpoint,type, layoutUri)).andReturn(instances);
		EasyMock.replay(serviceMock);
		
		List<RdfInstance> result = controller.loadTableData(loadTableData);
		assertEquals(instances, result);
		EasyMock.verify(serviceMock);
	}
	
	@Test
	public void generateUpdateScriptTest() {
		String script = "rdf-script";
		List<RdfChange> changes = new ArrayList<RdfChange>();

		EasyMock.expect(serviceMock.generateUpdateScript(changes)).andReturn(script);
		EasyMock.replay(serviceMock);
		
		String result = controller.generateUpdateScript(changes);
		assertEquals(script, result);
		
		EasyMock.verify(serviceMock);
	}

}
