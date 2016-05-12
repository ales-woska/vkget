package cz.cuni.mff.vkget.data.model;

import java.io.Serializable;

import cz.cuni.mff.vkget.connect.EndpointType;
import cz.cuni.mff.vkget.data.common.Type;
import cz.cuni.mff.vkget.data.common.Uri;

/**
 * Container for web service.
 * @author Ales Woska
 *
 */
public class LoadTableData implements Serializable {
	/**
	 * rdf:type for which table to load
	 */
	private Type tableType;
	
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
	private Uri layoutUri;

	public Type getTableType() {
		return tableType;
	}

	public void setTableType(Type tableType) {
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

	public Uri getLayoutUri() {
		return layoutUri;
	}

	public void setLayoutUri(Uri layoutUri) {
		this.layoutUri = layoutUri;
	}

}
