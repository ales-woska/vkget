package cz.cuni.mff.vkgmt.data.model;

import java.util.Map;

import cz.cuni.mff.vkgmt.data.common.Property;
import cz.cuni.mff.vkgmt.data.common.Uri;

/**
 * Filter for loading instances.
 * @author Ales Woska
 */
public class RdfFilter {
	
	/**
	 * Pairs of property:value where property is identifier of property by which to filter and value is string value to search by.
	 */
	private Map<Property, String> columnFilters;
	
	/**
	 * Only instances with there URIs ~ RdfInstace.uri
	 */
	private Map<Property, Uri> uriFilters;
	
	/**
	 * Max limit of loaded isntances.
	 */
	private int limit;
	private int offset = 0;

	public Map<Property, String> getColumnFilters() {
		return columnFilters;
	}

	public void setColumnFilters(Map<Property, String> columnFilters) {
		this.columnFilters = columnFilters;
	}

	public Map<Property, Uri> getUriFilters() {
		return uriFilters;
	}

	public void setUriFilters(Map<Property, Uri> uriFilters) {
		this.uriFilters = uriFilters;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

}
