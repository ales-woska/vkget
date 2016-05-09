package cz.cuni.mff.vkget.data.layout;

/**
 * Describes what to show as title.
 * @author Ales Woska
 *
 */
public enum TitleType {
	URI("uri"), LABEL("label"), CONSTANT("constant"), PROPERTY("property");

	private String text;

	TitleType(String text) {
		this.text = text;
	}

	public String getText() {
		return this.text;
	}

	public static TitleType fromString(String text) {
		if (text != null) {
			for (TitleType b : TitleType.values()) {
				if (text.equalsIgnoreCase(b.text)) {
					return b;
				}
			}
		}
		return null;
	}
}