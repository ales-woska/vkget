package cz.cuni.mff.vkget.layout;

import java.util.List;

public class ScreenLayout {
	private String name;
	private String uri;
	private List<BlockLayout> blockLayouts;
	private List<LineLayout> lineLayouts;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUri() {
		return uri;
	}

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
	
	public BlockLayout findLayoutForType(String type) {
		for (BlockLayout layout: blockLayouts) {
			if (layout.getForType().equals(type)) {
				return layout;
			}
		}
		return null;
	}

}
