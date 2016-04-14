package cz.cuni.mff.vkget.layout;

public enum LineType {
	SOLID("solid"), DOTTED("dotted"), DASHED("dashed");

	private String text;

	LineType(String text) {
		this.text = text;
	}

	public String getText() {
		return this.text;
	}

	public static LineType fromString(String text) {
		if (text != null) {
			for (LineType b : LineType.values()) {
				if (text.equalsIgnoreCase(b.text)) {
					return b;
				}
			}
		}
		return null;
	}
}
