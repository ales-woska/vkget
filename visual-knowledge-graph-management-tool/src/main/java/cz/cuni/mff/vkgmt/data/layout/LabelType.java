package cz.cuni.mff.vkgmt.data.layout;

/**
 * Describes what to show as title.
 * @author Ales Woska
 *
 */
public enum LabelType {
	URI("uri"), LABEL("label"), CONSTANT("constant"), PROPERTY("property");

	private String text;

	LabelType(String text) {
		this.text = text;
	}

	public String getText() {
		return this.text;
	}

	public static LabelType fromString(String text) {
		if (text != null) {
			for (LabelType b : LabelType.values()) {
				if (text.equalsIgnoreCase(b.text)) {
					return b;
				}
			}
		}
		return null;
	}
}