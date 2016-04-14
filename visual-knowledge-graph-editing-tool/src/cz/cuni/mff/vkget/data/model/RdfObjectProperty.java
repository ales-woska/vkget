package cz.cuni.mff.vkget.data.model;

/**
 * Property which value is RdfSubject
 * 
 * @param propertyUri
 *            URI of this property
 * @param subject
 *            RDF subject (source)
 * @param property
 *            RDF object (destination)
 */
public class RdfObjectProperty {
	private String propertyUri;
	private RdfObject subject;
	private RdfObject property;

	public String getPropertyUri() {
		return propertyUri;
	}

	public void setPropertyUri(String propertyUri) {
		this.propertyUri = propertyUri;
	}

	public RdfObject getSubject() {
		return subject;
	}

	public void setSubject(RdfObject subject) {
		this.subject = subject;
	}

	public RdfObject getProperty() {
		return property;
	}

	public void setProperty(RdfObject property) {
		this.property = property;
	}

}
