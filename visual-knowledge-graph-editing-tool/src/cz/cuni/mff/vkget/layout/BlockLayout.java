package cz.cuni.mff.vkget.layout;

import java.util.List;

public class BlockLayout extends AbstractLayout {
	private String forType;
	private String title;
	private String background;
	private int height;
	private int width;
	private int left;
	private int top;
	private List<RowLayout> properties;

	public BlockLayout() {
	}

	public BlockLayout(String forClass, String title) {
		this.forType = forClass;
		this.title = title;
	}

	public RowLayout getRowLayout(String name) {
		for (RowLayout r : properties) {
			if (r.getPropertyName().equals(name)) {
				return r;
			}
		}
		return null;
	}

	public String getForType() {
		return forType;
	}

	public void setForType(String forClass) {
		this.forType = forClass;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public List<RowLayout> getProperties() {
		return properties;
	}

	public void setProperties(List<RowLayout> properties) {
		this.properties = properties;
	}

	public int getLeft() {
		return left;
	}

	public void setLeft(int left) {
		this.left = left;
	}

	public int getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

}
