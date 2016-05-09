package cz.cuni.mff.vkget.data.model;

import java.io.Serializable;

import cz.cuni.mff.vkget.connect.EndpointType;

/**
 * Container for web service.
 * @author Ales Woska
 *
 */
public class LoadTableData implements Serializable {
	/**
	 * rdf:type for which table to load
	 */
	private String tableType;
	
	/**
	 * table filter
	 */
	private RdfFilter filter;
	
	/**
	 * data source
	 */
	private String endpoint;
	
	/**
	 * data source type
	 */
	private EndpointType type;
	
	/**
	 * layout definition
	 */
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
