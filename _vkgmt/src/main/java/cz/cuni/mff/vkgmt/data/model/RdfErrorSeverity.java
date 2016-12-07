package cz.cuni.mff.vkgmt.data.model;


public enum RdfErrorSeverity {
	INFO("http://purl.org/eis/vocab/daq#info"),
	WARNING("http://purl.org/eis/vocab/daq#warning"),
	ERROR("http://purl.org/eis/vocab/daq#error");

	private String text;

	RdfErrorSeverity(String text) {
		this.text = text;
	}

	public String getText() {
		return this.text;
	}

	public static RdfErrorSeverity fromString(String text) {
		if (text != null) {
			for (RdfErrorSeverity b : RdfErrorSeverity.values()) {
				if (text.equalsIgnoreCase(b.text)) {
					return b;
				}
			}
		}
		return null;
	}
}
