package cz.cuni.mff.vkget.connect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import cz.cuni.mff.vkget.data.common.Property;
import cz.cuni.mff.vkget.data.common.Uri;
import cz.cuni.mff.vkget.data.layout.BlockLayout;
import cz.cuni.mff.vkget.data.layout.ColumnLayout;
import cz.cuni.mff.vkget.data.layout.LabelType;
import cz.cuni.mff.vkget.data.layout.LineLayout;
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
		if (blockLayout.getLabel().getType() == LabelType.PROPERTY) {
			sb.append(" OPTIONAL { ?uri ").append(blockLayout.getLabel().getLabelSource()).append(" ?labelProperty . } ");
		}
		
		Map<Property, String> propertyVarMap = new HashMap<Property, String>();
		
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
			if (!(lineLayout.getFromType().getType().equals(blockLayout.getForType()))) {
				continue;
			}
			String varTo = "y" + j;
			sb.append(" OPTIONAL { ?uri ").append(lineLayout.getProperty()).append(" ?").append(varTo).append(" . } ");
		}

		if (filter != null && filter.getUriFilters() != null && filter.getUriFilters().size() > 0) {
			for (Property uriProperty: filter.getUriFilters().keySet()) {
				Uri uriValue = filter.getUriFilters().get(uriProperty);
				if (uriValue == null) {
					continue;
				}
				sb.append("<").append(uriValue.getUri()).append("> ").append(uriProperty.getProperty()).append(" ?uri . ");
			}
		}
		
		sb.append("} FILTER (lang(?typeLabel) = 'en'");
		
		if (filter != null && filter.getColumnFilters() != null && filter.getColumnFilters().size() > 0) {
			for (Property property: filter.getColumnFilters().keySet()) {
				if (property == null) {
					continue;
				}
				String filterValue = filter.getColumnFilters().get(property);
				if (filterValue.isEmpty()) {
					continue;
				}
				sb.append(" && (?").append(propertyVarMap.get(property)).append(" bif:contains '").append(filterValue).append("') ");
			}
		}
		
		sb.append(") ");
		
		int limit = LIMIT;
		if (filter != null && filter.getLimit() > 0) {
			limit = filter.getLimit();
		}
		sb.append("} ORDER BY ?uri LIMIT ").append(limit);
		return sb.toString();
	}
	
}
