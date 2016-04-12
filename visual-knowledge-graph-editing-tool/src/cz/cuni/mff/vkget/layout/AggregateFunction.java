package cz.cuni.mff.vkget.layout;

public enum AggregateFunction {
	NOTHING("nothing"), MIN("min"), MAX("max"), AVG("avg");

	private String text;

	AggregateFunction(String text) {
		this.text = text;
	}

	public String getText() {
		return this.text;
	}

	public static AggregateFunction fromString(String text) {
		if (text != null) {
			for (AggregateFunction b : AggregateFunction.values()) {
				if (text.equalsIgnoreCase(b.text)) {
					return b;
				}
			}
		}
		return null;
	}
}