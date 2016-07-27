package cz.cuni.mff.vkgmt.data.layout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.cuni.mff.vkgmt.data.common.RdfEntity;
import cz.cuni.mff.vkgmt.data.common.Uri;
import cz.cuni.mff.vkgmt.sparql.Constants;

/**
 * Whole layout definition.
 * @author Ales Woska
 */
public class ScreenLayout extends RdfEntity {
	/**
	 * Layout name
	 */
	private String name;
	
	/**
	 * Namespaces used in layout.
	 */
	private Map<String, String> namespaces;
	
	/**
	 * How many tables will be affected by selecting an instance or entering a filter.
	 */
	private PropagationType filterPropagation = PropagationType.NEIGHBOURS;
	
	/**
	 * Definitions of tables ~ @see RdfTable
	 */
	private List<BlockLayout> blockLayouts;
	
	/**
	 * Definitions of properties connecting tables @see RdfObjectProperty
	 */
	private List<LineLayout> lineLayouts;
	
	public ScreenLayout() {
		this.type = Constants.ScreenLayoutType;
		namespaces = new HashMap<String, String>();
		namespaces.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		namespaces.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Uri getUri() {
		return uri;
	}

	@Override
	public void setUri(Uri uri) {
		this.uri = uri;
	}

	public List<BlockLayout> getBlockLayouts() {
		return blockLayouts;
	}

	public void setBlockLayouts(List<BlockLayout> blockLayouts) {
		this.blockLayouts = blockLayouts;
	}

	public List<LineLayout> getLineLayouts() {
		return lineLayouts;
	}

	public void setLineLayouts(List<LineLayout> lineLayouts) {
		this.lineLayouts = lineLayouts;
	}

	public Map<String, String> getNamespaces() {
		return namespaces;
	}

	public String getNamespacesAsString() {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String key: namespaces.keySet()) {
			if (!first) {
				sb.append(",");
			} else {
				first = false;
			}
			sb.append(key);
			sb.append(":");
			sb.append(namespaces.get(key));
		}
		return sb.toString();
	}

	public void setNamespaces(Map<String, String> namespaces) {
		this.namespaces = namespaces;
	}
	
	public PropagationType getFilterPropagation() {
		return filterPropagation;
	}

	public void setFilterPropagation(PropagationType filterPropagation) {
		this.filterPropagation = filterPropagation;
	}

	public void setNamespacesFromString(String namespaces) {
		if (namespaces == null || namespaces.isEmpty()) {
			return;
		}
		Map<String, String> map = new HashMap<String, String>();
		for (String pair: namespaces.split(",")) {
			String[] pairArray = pair.split(":", 2);
			map.put(pairArray[0], pairArray[1]);
		}
		this.namespaces = map;
	}

}
