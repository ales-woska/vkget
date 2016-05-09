package cz.cuni.mff.vkget.data.model;

import java.util.List;
import java.util.Map;

/**
 * @author Ales Woska
 */
public class RdfFilter {
	private Map<String, String> columnFilters;
	private List<String> uriFilters;
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
