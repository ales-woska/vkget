package cz.cuni.mff.vkgmt.data.model;

import java.io.Serializable;

import cz.cuni.mff.vkgmt.data.common.Property;

/**
 * Literal value of instance (@see RdfInstance).
 * @author Ales Woska
 *
 */
public class RdfLiteralProperty implements Serializable {
	/**
	 * identifier of the property. e.g. rdfs:label or dbpedia-owl:length
	 */
	private Property property;
	
	/**
	 * value ~ table cell
	 */
	private Object value;

	public Property getProperty() {
		return property;
	}

	public void setProperty(Property property) {
		this.property = property;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
