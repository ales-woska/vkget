package cz.cuni.mff.vkgmt.data.layout;

import cz.cuni.mff.vkgmt.data.common.RdfEntity;

/**
 * common properties for @see LineLayout and @see BlockLayout
 * 
 * @author Ales Woska
 *
 */
public abstract class AbstractLayout extends RdfEntity {

	protected Label label;
	protected String fontColor = "#000000";
	protected int fontSize = 10;
	protected String lineColor = "#000000";
	protected LineType lineType = LineType.SOLID;
	protected int lineThickness = 1;

	public Label getLabel() {
		return label;
	}

	public void setLabel(Label label) {
		this.label = label;
	}

	public String getFontColor() {
		return fontColor;
	}

	public void setFontColor(String fontColor) {
		this.fontColor = fontColor;
	}

	public int getFontSize() {
		return fontSize;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	public String getLineColor() {
		return lineColor;
	}

	public void setLineColor(String lineColor) {
		this.lineColor = lineColor;
	}

	public LineType getLineType() {
		return lineType;
	}

	public void setLineType(LineType lineType) {
		this.lineType = lineType;
	}

	public int getLineThickness() {
		return lineThickness;
	}

	public void setLineThickness(int lineThickness) {
		this.lineThickness = lineThickness;
	}

}