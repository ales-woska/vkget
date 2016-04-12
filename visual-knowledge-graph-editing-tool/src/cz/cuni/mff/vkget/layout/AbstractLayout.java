package cz.cuni.mff.vkget.layout;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractLayout {
	private String uri;
	private List<TitleType> titleTypes;
	private String fontColor;
	private int fontSize;
	private String lineColor;
	private String lineType;
	private int lineThickness;

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

	public String getLineType() {
		return lineType;
	}

	public void setLineType(String lineType) {
		this.lineType = lineType;
	}

	public int getLineThickness() {
		return lineThickness;
	}

	public void setLineThickness(int lineThickness) {
		this.lineThickness = lineThickness;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

}