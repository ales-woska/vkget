package cz.cuni.mff.vkget.data.model;

import java.io.Serializable;

public class RdfChange implements Serializable {

	private String objectUri;
	private String property;
	private String oldValue;
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
