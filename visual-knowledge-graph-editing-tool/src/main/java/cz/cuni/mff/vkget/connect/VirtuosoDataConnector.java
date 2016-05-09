package cz.cuni.mff.vkget.connect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import cz.cuni.mff.vkget.data.layout.BlockLayout;
import cz.cuni.mff.vkget.data.layout.ColumnLayout;
import cz.cuni.mff.vkget.data.layout.LineLayout;
import cz.cuni.mff.vkget.data.layout.TitleType;
import cz.cuni.mff.vkget.data.model.RdfFilter;
import cz.cuni.mff.vkget.sparql.Constants;

/**
 * Implementatinon of @see DataConnector for Virtuoso endpoints.
 * @author Ales Woska
 *
 */
@Service
public class VirtuosoDataConnector extends CommonDataConnector {
	
	public VirtuosoDataConnector() {}
	
	public VirtuosoDataConnector(String endpoint) {
		super(endpoint);
	}
	
	/**
	 * Uses Virtuoso specific funtions.
	 */
	@Override
	protected String constructTableQuery(BlockLayout blockLayout, Map<String, String> namespaces, List<LineLayout> lineLayouts, RdfFilter filter) {
		StringBuilder sb = new StringBuilder();
		for (String prefix: namespaces.keySet()) {
			String namespace = namespaces.get(prefix);
			sb.append("PREFIX ").append(prefix).append(": <").append(namespace).append("> ");
		}
		sb.append("SELECT DISTINCT * WHERE {{ ");
		
		sb.append(" ?uri rdf:type ").append(blockLayout.getForType()).append(" . ");
		sb.append(" ?uri ").append(Constants.RDFS_LABEL).append(" ?typeLabel . ");
		if (blockLayout.getTitleTypes().get(0) == TitleType.PROPERTY) {
			sb.append(" OPTIONAL { ?uri ").append(blockLayout.getTitle()).append(" ?labelProperty . } ");
		}
		
		Map<String, String> propertyVarMap = new HashMap<String, String>();
		
		int j = 0;
		for (ColumnLayout columnLayout: blockLayout.getProperties()) {
			if (columnLayout.getProperty().equals(Constants.RDFS_LABEL)) {
				propertyVarMap.put(Constants.RDFS_LABEL, "typeLabel");
				continue;
			}
			String property = "y" + j;
			propertyVarMap.put(columnLayout.getProperty(), property);
			j++;
			sb.append(" OPTIONAL { ?uri ").append(columnLayout.getProperty()).append(" ?").append(property).append(" . } ");
		}
		
		for (LineLayout lineLayout: lineLayouts) {
			if (!(lineLayout.getFromType().equalsIgnoreCase(blockLayout.getForType()))) {
				continue;
			}
			String varTo = "y" + j;
			sb.append(" OPTIONAL { ?uri ").append(lineLayout.getProperty()).append(" ?").append(varTo).append(" . } ");
		}
		
		StringBuilder filterSb = new StringBuilder("lang(?typeLabel) = 'en'");

		if (filter != null && filter.getUriFilters() != null && filter.getUriFilters().size() > 0) {
			filterSb.append(" && (");
			for (String uriFilter: filter.getUriFilters()) {
				if (uriFilter.isEmpty()) {
					continue;
				}
				if (!filter.getUriFilters().get(0).equals(uriFilter)) {
					filterSb.append("or ?uri = <").append(uriFilter).append("> ");
				} else {
					filterSb.append("?uri = <").append(uriFilter).append("> ");
				}
			}
			filterSb.append(") ");
		}
		
		if (filter != null && filter.getColumnFilters() != null && filter.getColumnFilters().size() > 0) {
			for (String property: filter.getColumnFilters().keySet()) {
				if (property.isEmpty()) {
					continue;
				}
				String filterValue = filter.getColumnFilters().get(property);
				if (filterValue.isEmpty()) {
					continue;
				}
				filterSb.append(" && (?").append(propertyVarMap.get(property)).append(" bif:contains '").append(filterValue).append("') ");
			}
		}
		
		sb.append("} FILTER (").append(filterSb.toString()).append(") ");
		
		int limit = LIMIT;
		if (filter != null && filter.getLimit() > 0) {
			limit = filter.getLimit();
		}
		sb.append("} ORDER BY ?uri LIMIT ").append(limit);
		return sb.toString();
	}
	
}
