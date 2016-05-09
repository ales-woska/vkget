package cz.cuni.mff.vkget.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.cuni.mff.vkget.connect.CommonDataConnector;
import cz.cuni.mff.vkget.connect.DataConnector;
import cz.cuni.mff.vkget.connect.EndpointType;
import cz.cuni.mff.vkget.data.layout.ScreenLayout;
import cz.cuni.mff.vkget.data.model.DataModel;
import cz.cuni.mff.vkget.data.model.RdfChange;

@Service
public class DataServiceImpl implements DataService {
	
	@Autowired
	private LayoutService layoutService;
	
	@Override
	public String generateUpdateScript(List<RdfChange> changes) {
		StringBuilder updates = new StringBuilder("UPDATE {\n");
		StringBuilder inserts = new StringBuilder("INSERT {\n");
		StringBuilder deletes = new StringBuilder("DELETE {\n");
		
		int updatesCount = 0;
		int insertsCount = 0;
		int deletesCount = 0;
		
		
		for (RdfChange change: changes) {
			boolean oldValueEmpty = change.getOldValue() == null || change.getOldValue().isEmpty();
			boolean newValueEmpty = change.getNewValue() == null || change.getNewValue().isEmpty();
			
			if (oldValueEmpty && !newValueEmpty) {
				insertsCount++;
				inserts.append("\t<").append(change.getObjectUri()).append("> ").append(change.getProperty());
				if (change.getNewValue().contains("http://")) {
					inserts.append(" <").append(change.getNewValue()).append("> .\n");
				} else {
					inserts.append(" '").append(change.getNewValue()).append("' .\n");
				}
			} else if (newValueEmpty && !oldValueEmpty) {
				deletesCount++;
				deletes.append("\t<").append(change.getObjectUri()).append("> ").append(change.getProperty());
				if (change.getOldValue().contains("http://")) {
					deletes.append(" <").append(change.getOldValue()).append("> .\n");
				} else {
					deletes.append(" '").append(change.getOldValue()).append("' .\n");
				}
			} else {
				updatesCount++;
				updates.append("\t<").append(change.getObjectUri()).append("> ").append(change.getProperty());
				if (change.getNewValue().contains("http://")) {
					updates.append(" <").append(change.getNewValue()).append("> .\n");
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

	@Override
	public DataModel loadDataModel(String endpoint, EndpointType endpointType, String layoutUri) {
		ScreenLayout screenLayout = layoutService.getLayout(layoutUri);
		
		DataConnector connector = null;
		switch (endpointType) {
			case virtuoso: connector = new CommonDataConnector(endpoint); break;
			case jena: connector = new CommonDataConnector(endpoint); break;
			default: connector = new CommonDataConnector(endpoint); break;
		}
		
		DataModel dataModel = connector.loadDataModel(screenLayout);
		return dataModel;
	}
	
}