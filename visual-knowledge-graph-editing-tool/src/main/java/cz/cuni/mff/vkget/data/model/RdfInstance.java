package cz.cuni.mff.vkget.data.model;

import java.io.Serializable;
import java.util.List;

/**
 * Instance of RDF object ~ table row
 * @author Ales Woska
 *
 */
public class RdfInstance implements Serializable {
	
	/**
	 * Instance URI
	 */
	private String objectURI;
	
	/**
	 * rdf:type, is same as RdfTable.typeURI
	 */
	private String type;
	
	/**
	 * properties leading to other table
	 */
	private List<RdfObjectProperty> objectProperties;
	
	/**
	 * instance properties ~ table cells
	 */
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
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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
