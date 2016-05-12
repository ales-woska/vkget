package cz.cuni.mff.vkget.data.layout;

import java.util.List;

import cz.cuni.mff.vkget.data.common.Type;
import cz.cuni.mff.vkget.sparql.Constants;

/**
 * Layout for table
 * @author Ales Woska
 *
 */
public class BlockLayout extends AbstractLayout {
	/**
	 * rdf:type of all instaces
	 */
	private Type forType;
	
	/**
	 * color of table background
	 */
	private String background;
	
	/**
	 * height of the table
	 */
	private int height;
	
	/**
	 * width of the table
	 */
	private int width;
	
	/**
	 * X position
	 */
	private int left;
	
	/**
	 * Y position
	 */
	private int top;
	
	/**
	 * 
	 */
	private List<ColumnLayout> properties;

	public BlockLayout() {
		this.type = Constants.BlockLayoutType;
	}

	public BlockLayout(Type forType, Label label) {
		this.forType = forType;
		this.label = label;
	}

	public Type getForType() {
		return forType;
	}

	public void setForType(Type forClass) {
		this.forType = forClass;
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

	public List<ColumnLayout> getProperties() {
		return properties;
	}

	public void setProperties(List<ColumnLayout> properties) {
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
