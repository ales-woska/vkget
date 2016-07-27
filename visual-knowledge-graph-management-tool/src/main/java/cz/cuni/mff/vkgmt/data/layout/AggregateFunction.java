package cz.cuni.mff.vkgmt.data.layout;

/**
 * types of aggregate functions that can be used.
 * @author Ales Woska
 *
 */
public enum AggregateFunction {
	NOTHING("nothing"), MIN("min"), MAX("max"), AVG("avg"), MEDIAN("med");

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