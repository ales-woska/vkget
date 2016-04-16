package cz.cuni.mff.vkget.data.model;

import java.io.Serializable;
import java.util.List;

/**
 * Rdf class instance - line in GveTable, its literal properties makes columns.
 * 
 * @param objectURI
 *            URI of the instance
 */
public class RdfObject implements Serializable {
	private String objectURI;
	private List<RdfObjectProperty> objectProperties;
	private List<RdfProperty> literalProperties;

	public String getObjectURI() {
		return objectURI;
	}

	public void setObjectURI(String objectURI) {
		this.objectURI = objectURI;
	}

	public List<RdfObjectProperty> getObjectProperties() {
		return objectProperties;
	}

	public void setObjectProperties(List<RdfObjectProperty> objectProperties) {
		this.objectProperties = objectProperties;
	}

	public List<RdfProperty> getLiteralProperties() {
		return literalProperties;
	}

	public void setLiteralProperties(List<RdfProperty> literalProperties) {
		this.literalProperties = literalProperties;
	}
	
	public Object getValueForProperty(String property) {
		for (RdfProperty rp: literalProperties) {
			if (rp.getPropertyURI().equals(property)) {
				return rp.getValue();
			}
		}
		return null;
	}

}
