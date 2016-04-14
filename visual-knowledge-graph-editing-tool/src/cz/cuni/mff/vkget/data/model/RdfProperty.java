package cz.cuni.mff.vkget.data.model;

/**
 * Concrete cell in GveTable. propertyUrl is same as columnsURIs in GveTable.
 * 
 * @param propertyURI
 *            URI of this property
 * @param value
 *            concrete property value - content of table cell
 */
public class RdfProperty {
	private String propertyURI;
	private Object value;

	public String getPropertyURI() {
		return propertyURI;
	}

	public void setPropertyURI(String propertyURI) {
		this.propertyURI = propertyURI;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
