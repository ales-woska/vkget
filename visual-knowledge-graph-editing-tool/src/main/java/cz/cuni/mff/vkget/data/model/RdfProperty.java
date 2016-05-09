package cz.cuni.mff.vkget.data.model;

import java.io.Serializable;

/**
 * Literal value of instance (@see RdfInstance).
 * @author Ales Woska
 *
 */
public class RdfProperty implements Serializable {
	/**
	 * identifier of the property. e.g. rdfs:label or dbpedia-owl:length
	 */
	private String propertyURI;
	
	/**
	 * value ~ table cell
	 */
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
