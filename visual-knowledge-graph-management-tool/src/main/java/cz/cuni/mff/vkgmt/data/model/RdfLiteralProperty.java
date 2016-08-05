package cz.cuni.mff.vkgmt.data.model;

import java.io.Serializable;

import cz.cuni.mff.vkgmt.data.common.Property;

/**
 * Literal value of instance (@see RdfInstance).
 * @author Ales Woska
 *
 */
public class RdfLiteralProperty implements Serializable {
	
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
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof RdfLiteralProperty)) {
			return false;
		} else {
			RdfLiteralProperty p = (RdfLiteralProperty) o;
			return p.getProperty().equals(property) && p.getValue().equals(value);
		}
	}

}
