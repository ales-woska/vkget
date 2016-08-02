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
				deletes.append("\t<").append(change.getUri()).append("> ").append(change.getProperty());
				if (change.getNewValue() instanceof Uri) {
					deletes.append(" <").append((Uri)change.getNewValue()).append("> .\n");
				} else {
					deletes.append(" '").append(change.getNewValue()).append("' .\n");
				}
				inserts.append("\t<").append(change.getUri()).append("> ").append(change.getProperty());
				if (change.getNewValue() instanceof Uri) {
					inserts.append(" <").append((Uri)change.getNewValue()).append("> .\n");
				} else {
					inserts.append(" '").append(change.getNewValue()).append("' .\n");
				}
			}
		}
		inserts.append("}\n");
		deletes.append("}\n");
		
		StringBuilder result = new StringBuilder();
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