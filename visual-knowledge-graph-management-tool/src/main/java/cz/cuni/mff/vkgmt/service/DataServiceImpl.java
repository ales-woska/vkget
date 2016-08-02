package cz.cuni.mff.vkgmt.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.cuni.mff.vkgmt.connect.ConnectionInfo;
import cz.cuni.mff.vkgmt.connect.DataConnector;
import cz.cuni.mff.vkgmt.connect.DefaultDataConnector;
import cz.cuni.mff.vkgmt.connect.VirtuosoDataConnector;
import cz.cuni.mff.vkgmt.data.common.Type;
import cz.cuni.mff.vkgmt.data.common.Uri;
import cz.cuni.mff.vkgmt.data.layout.ScreenLayout;
import cz.cuni.mff.vkgmt.data.model.DataModel;
import cz.cuni.mff.vkgmt.data.model.RdfChange;
import cz.cuni.mff.vkgmt.data.model.RdfFilter;
import cz.cuni.mff.vkgmt.data.model.RdfInstance;
import cz.cuni.mff.vkgmt.data.model.RdfTable;

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
		StringBuilder result = new StringBuilder();
		
		for (RdfChange change: changes) {
			boolean oldValueEmpty = change.getOldValue() == null;
			boolean newValueEmpty = change.getNewValue() == null;
			
			if (oldValueEmpty && !newValueEmpty) {
				result.append("INSERT DATA {\n\t<").append(change.getUri()).append("> ").append(change.getProperty());
				if (change.getNewValue() instanceof Uri) {
					result.append(" <").append((Uri)change.getNewValue()).append("> .\n");
				} else {
					result.append(" '").append(change.getNewValue()).append("' .\n");
				}
				result.append("}\n");
			} else if (newValueEmpty && !oldValueEmpty) {
				result.append("DELETE DATA {\n\t<").append(change.getUri()).append("> ").append(change.getProperty());
				if (change.getOldValue() instanceof Uri) {
					result.append(" <").append((Uri)change.getOldValue()).append("> .\n");
				} else {
					result.append(" '").append(change.getOldValue()).append("' .\n");
				}
				result.append("}\n");
			} else {
				result.append("DELETE DATA {\n\t<").append(change.getUri()).append("> ").append(change.getProperty());
				if (change.getNewValue() instanceof Uri) {
					result.append(" <").append((Uri)change.getNewValue()).append("> .\n");
				} else {
					result.append(" '").append(change.getNewValue()).append("' .\n");
				}
				result.append("}\n");
				result.append("INSERT DATA {\n\t<").append(change.getUri()).append("> ").append(change.getProperty());
				if (change.getNewValue() instanceof Uri) {
					result.append(" <").append((Uri)change.getNewValue()).append("> .\n");
				} else {
					result.append(" '").append(change.getNewValue()).append("' .\n");
				}
				result.append("}\n");
			}
		}
		return result.toString();
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public DataModel loadDataModel(ConnectionInfo connectionInfo, Uri layoutUri) {
		ScreenLayout screenLayout = layoutService.getLayout(layoutUri);
		DataConnector connector = getConnector(connectionInfo);
		DataModel dataModel = connector.loadDataModel(connectionInfo.getNamedGraph(), screenLayout);
		return dataModel;
	}
	
	/**
	 * @inheritDoc
	 */
	@Override
	public List<RdfInstance> loadTableData(Type tableType, RdfFilter filter, ConnectionInfo connectionInfo, Uri layoutUri) {
		ScreenLayout screenLayout = layoutService.getLayout(layoutUri);
		DataConnector connector = getConnector(connectionInfo);
		RdfTable table = connector.loadTableData(connectionInfo.getNamedGraph(), tableType, filter, screenLayout);
		if (table == null) {
			return new ArrayList<RdfInstance>();
		} else {
			List<RdfInstance> instances = table.getInstances();
			return instances;
		}
	}
	
	private DataConnector getConnector(ConnectionInfo connectionInfo) {
		switch (connectionInfo.getType()) {
			case virtuoso: return new VirtuosoDataConnector(connectionInfo);
			case jena: return new DefaultDataConnector(connectionInfo);
			default: return new DefaultDataConnector(connectionInfo);
		}
	}
	
}