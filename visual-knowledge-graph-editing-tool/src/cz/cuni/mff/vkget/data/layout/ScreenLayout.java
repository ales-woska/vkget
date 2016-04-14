package cz.cuni.mff.vkget.data.layout;

import java.util.List;

import cz.cuni.mff.vkget.data.RdfEntity;
import cz.cuni.mff.vkget.sparql.Constants;

public class ScreenLayout extends RdfEntity {
	private String name;
	private List<BlockLayout> blockLayouts;
	private List<LineLayout> lineLayouts;
	
	public ScreenLayout() {
		this.type = Constants.ScreenLayoutType;
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
	
	public BlockLayout findLayoutForType(String type) {
		for (BlockLayout layout: blockLayouts) {
			if (layout.getForType().equals(type)) {
				return layout;
			}
		}
		return null;
	}

}
