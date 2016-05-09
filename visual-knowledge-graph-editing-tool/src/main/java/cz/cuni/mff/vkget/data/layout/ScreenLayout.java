package cz.cuni.mff.vkget.data.layout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.cuni.mff.vkget.data.RdfEntity;
import cz.cuni.mff.vkget.sparql.Constants;

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
	public String getUri() {
		return uri;
	}

	@Override
	public void setUri(String uri) {
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

	public BlockLayout findLayoutForType(String type) {
		for (BlockLayout layout: blockLayouts) {
			if (layout.getForType().equals(type)) {
				return layout;
			}
		}
		return null;
	}
	
}
