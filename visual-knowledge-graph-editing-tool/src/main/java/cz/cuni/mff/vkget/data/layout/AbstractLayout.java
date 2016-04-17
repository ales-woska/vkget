package cz.cuni.mff.vkget.data.layout;

import java.util.ArrayList;
import java.util.List;

import cz.cuni.mff.vkget.data.RdfEntity;

public abstract class AbstractLayout extends RdfEntity {
	protected String title;
	protected List<TitleType> titleTypes = new ArrayList<TitleType>();
	protected String fontColor;
	protected int fontSize;
	protected String lineColor;
	protected LineType lineType = LineType.SOLID;
	protected int lineThickness;

	public List<TitleType> getTitleTypes() {
		return titleTypes;
	}

	public String getTitleTypesAsString() {
		StringBuilder sb = new  StringBuilder();
		for (TitleType type: this.titleTypes) {
			sb.append(" ").append(type.getText());
		}
		return sb.toString().trim();
	}
	
	public void setTitleTypes(List<TitleType> titleTypes) {
		this.titleTypes = titleTypes;
	}
	
	public void setTitleTypesFromString(String titleTypes) {
		if (titleTypes == null) {
			return;
		}
		String[] types = titleTypes.split(" ");
		this.titleTypes = new ArrayList<TitleType>();
		for (String type: types) {
			this.titleTypes.add(TitleType.fromString(type));
		}
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

	public String getLineTypeAsString() {
		return lineType.getText();
	}

	public void setLineType(LineType lineType) {
		this.lineType = lineType;
	}

	public void setLineTypeFromString(String lineType) {
		this.lineType = LineType.fromString(lineType);
	}

	public int getLineThickness() {
		return lineThickness;
	}

	public void setLineThickness(int lineThickness) {
		this.lineThickness = lineThickness;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}