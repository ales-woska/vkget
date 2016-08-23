package cz.cuni.mff.vkgmt.data.model;

import java.io.Serializable;

import cz.cuni.mff.vkgmt.connect.ConnectionInfo;
import cz.cuni.mff.vkgmt.data.common.Type;
import cz.cuni.mff.vkgmt.data.common.Uri;

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
	
	private ConnectionInfo connectionInfo;
	
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

	public Uri getLayoutUri() {
		return layoutUri;
	}

	public void setLayoutUri(Uri layoutUri) {
		this.layoutUri = layoutUri;
	}

	public ConnectionInfo getConnectionInfo() {
		return connectionInfo;
	}

	public void setConnectionInfo(ConnectionInfo connectionInfo) {
		this.connectionInfo = connectionInfo;
	}

}
