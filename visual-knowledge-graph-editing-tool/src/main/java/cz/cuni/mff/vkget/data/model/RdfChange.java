package cz.cuni.mff.vkget.data.model;

import java.io.Serializable;

/**
 * Change done in frontend.
 * @author Ales Woska
 *
 */
public class RdfChange implements Serializable {

	/**
	 * URI of changed instance.
	 */
	private String objectUri;
	
	/**
	 * Changed property. If it's an URI then it is reference to other RdfInstance.
	 */
	private String property;
	
	/**
	 * Value before change. If is empty then the property is created.
	 */
	private String oldValue;
	
	/**
	 * Value adter change. If is empty then the property is deleted.
	 */
	private String newValue;

	public String getObjectUri() {
		return objectUri;
	}

	public void setObjectUri(String objectUri) {
		this.objectUri = objectUri;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getOldValue() {
		return oldValue;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

}
