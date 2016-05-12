package cz.cuni.mff.vkget.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.cuni.mff.vkget.connect.CommonDataConnector;
import cz.cuni.mff.vkget.connect.DataConnector;
import cz.cuni.mff.vkget.connect.EndpointType;
import cz.cuni.mff.vkget.connect.VirtuosoDataConnector;
import cz.cuni.mff.vkget.data.common.Type;
import cz.cuni.mff.vkget.data.common.Uri;
import cz.cuni.mff.vkget.data.layout.ScreenLayout;
import cz.cuni.mff.vkget.data.model.DataModel;
import cz.cuni.mff.vkget.data.model.RdfChange;
import cz.cuni.mff.vkget.data.model.RdfFilter;
import cz.cuni.mff.vkget.data.model.RdfInstance;
import cz.cuni.mff.vkget.data.model.RdfTable;

/**
 * Implementation of @see DataService
 * @author Ales Woska
 *
 */
@Service
public class DataServiceImpl implements DataService {
	
	@Autowired
	private LayoutService layoutService;
	
	/**
	 * @inheritDoc
	 */
	@Override
	public String generateUpdateScript(List<RdfChange> changes) {
		StringBuilder updates = new StringBuilder("MODIFY {\n");
		StringBuilder inserts = new StringBuilder("INSERT DATA {\n");
		StringBuilder deletes = new StringBuilder("DELETE DATA {\n");
		
		int insertsCount = 0;
		int updatesCount = 0;
		int deletesCount = 0;
		
		for (RdfChange change: changes) {
			boolean oldValueEmpty = change.getOldValue() == null;
			boolean newValueEmpty = change.getNewValue() == null;
			
			if (oldValueEmpty && !newValueEmpty) {
				insertsCount++;
				inserts.append("\t<").append(change.getUri()).append("> ").append(change.getProperty());
				if (change.getNewValue() instanceof Uri) {
					inserts.append(" <").append((Uri)change.getNewValue()).append("> .\n");
				} else {
					inserts.append(" '").append(change.getNewValue()).append("' .\n");
				}
			} else if (newValueEmpty && !oldValueEmpty) {
				deletesCount++;
				deletes.append("\t<").append(change.getUri()).append("> ").append(change.getProperty());
				if (change.getOldValue() instanceof Uri) {
					deletes.append(" <").append((Uri)change.getOldValue()).append("> .\n");
				} else {
					deletes.append(" '").append(change.getOldValue()).append("' .\n");
				}
			} else {
				updatesCount++;
				updates.append("\t<").append(change.getUri()).append("> ").append(change.getProperty());
				if (change.getNewValue() instanceof Uri) {
					updates.append(" <").append((Uri)change.getNewValue()).append("> .\n");
				} else {
					updates.append(" '").append(change.getNewValue()).append("' .\n");
				}
			}
		}
		updates.append("}\n");
		inserts.append("}\n");
		deletes.append("}\n");
		
		StringBuilder result = new StringBuilder();
		if (updatesCount > 0) {
			result.append(updates.toString());
		}
		if (insertsCount > 0) {
			result.append(inserts.toString());
		}
		if (deletesCount > 0) {
			result.append(deletes.toString());
		}
		return result.toString();
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public DataModel loadDataModel(String endpoint, EndpointType endpointType, Uri layoutUri) {
		ScreenLayout screenLayout = layoutService.getLayout(layoutUri);
		
		DataConnector connector = getConnector(endpoint, endpointType);
		
		DataModel dataModel = connector.loadDataModel(screenLayout);
		return dataModel;
	}
	
	/**
	 * @inheritDoc
	 */
	@Override
	public List<RdfInstance> loadTableData(Type tableType, RdfFilter filter, String endpoint, EndpointType endpointType, Uri layoutUri) {
		ScreenLayout screenLayout = layoutService.getLayout(layoutUri);
		DataConnector connector = getConnector(endpoint, endpointType);
		RdfTable table = connector.loadTableData(tableType, filter, screenLayout);
		if (table == null) {
			return new ArrayList<RdfInstance>();
		} else {
			List<RdfInstance> instances = table.getInstances();
			return instances;
		}
	}
	
	private DataConnector getConnector(String endpoint, EndpointType type) {
		switch (type) {
			case virtuoso: return new VirtuosoDataConnector(endpoint);
			case jena: return new CommonDataConnector(endpoint);
			default: return new CommonDataConnector(endpoint);
		}
	}
	
}