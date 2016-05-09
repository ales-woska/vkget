package cz.cuni.mff.vkget.data.model;

import java.util.List;
import java.util.Map;

/**
 * Filter for loading instances.
 * @author Ales Woska
 */
public class RdfFilter {
	
	/**
	 * Pairs of property:value where property is identifier of property by which to filter and value is string value to search by.
	 */
	private Map<String, String> columnFilters;
	
	/**
	 * Only instances with there URIs ~ RdfInstace.objectUri
	 */
	private List<String> uriFilters;
	
	/**
	 * Max limit of loaded isntances.
	 */
	private int limit;

	public Map<String, String> getColumnFilters() {
		return columnFilters;
	}

	public void setColumnFilters(Map<String, String> columnFilters) {
		this.columnFilters = columnFilters;
	}

	public List<String> getUriFilters() {
		return uriFilters;
	}

	public void setUriFilters(List<String> uriFilters) {
		this.uriFilters = uriFilters;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

}
