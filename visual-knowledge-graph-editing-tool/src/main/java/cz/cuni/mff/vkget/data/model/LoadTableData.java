package cz.cuni.mff.vkget.data.model;

import java.io.Serializable;

import cz.cuni.mff.vkget.connect.EndpointType;

public class LoadTableData implements Serializable {
	private String tableType;
	private RdfFilter filter;
	private String endpoint;
	private EndpointType type;
	private String layoutUri;

	public String getTableType() {
		return tableType;
	}

	public void setTableType(String tableType) {
		this.tableType = tableType;
	}

	public RdfFilter getFilter() {
		return filter;
	}

	public void setFilter(RdfFilter filter) {
		this.filter = filter;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public EndpointType getType() {
		return type;
	}

	public void setType(EndpointType type) {
		this.type = type;
	}

	public String getLayoutUri() {
		return layoutUri;
	}

	public void setLayoutUri(String layoutUri) {
		this.layoutUri = layoutUri;
	}

}
