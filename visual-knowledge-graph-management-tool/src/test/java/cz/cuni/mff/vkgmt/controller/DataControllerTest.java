package cz.cuni.mff.vkgmt.controller;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import cz.cuni.mff.vkgmt.connect.ConnectionInfo;
import cz.cuni.mff.vkgmt.connect.EndpointType;
import cz.cuni.mff.vkgmt.data.common.Type;
import cz.cuni.mff.vkgmt.data.common.Uri;
import cz.cuni.mff.vkgmt.data.model.DataModel;
import cz.cuni.mff.vkgmt.data.model.LoadTableData;
import cz.cuni.mff.vkgmt.data.model.RdfChange;
import cz.cuni.mff.vkgmt.data.model.RdfFilter;
import cz.cuni.mff.vkgmt.data.model.RdfInstance;
import cz.cuni.mff.vkgmt.service.DataService;

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
		ConnectionInfo connectionInfo = new ConnectionInfo();
		String endpoint = "http://some-endpoint.com:123";
		EndpointType type = EndpointType.jena;
		Uri layoutUri = new Uri("http://some-uri#aaa");
		
		DataModel model = new DataModel();

		EasyMock.expect(serviceMock.loadDataModel(connectionInfo, layoutUri)).andReturn(model);
		EasyMock.replay(serviceMock);
		
		String useNamedGraph = null;
		String useAutorization = null;
		String namedGraph = null;
		String username = null;
		String password = null;
		DataModel result = controller.loadData(endpoint, type, useNamedGraph, useAutorization, namedGraph, username, password, layoutUri);
				
		assertEquals(model, result);
		
		EasyMock.verify(serviceMock);
	}
	
	@Test
	public void loadTableDataTest() {
		ConnectionInfo connectionInfo = new ConnectionInfo();
		LoadTableData loadTableData = new LoadTableData();
		Type tableType = new Type("", "");
		RdfFilter filter = new RdfFilter();
//		String endpoint = "http://some-endpoint.com:123";
//		EndpointType type = EndpointType.jena;
		Uri layoutUri = new Uri("http://some-uri#aaa");
		loadTableData.setTableType(tableType);
		loadTableData.setFilter(filter);
		loadTableData.setConnectionInfo(connectionInfo);
		loadTableData.setLayoutUri(layoutUri);
		
		List<RdfInstance> instances = new ArrayList<RdfInstance>();
		
		EasyMock.expect(serviceMock.loadTableData(tableType, filter, connectionInfo, layoutUri)).andReturn(instances);
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
