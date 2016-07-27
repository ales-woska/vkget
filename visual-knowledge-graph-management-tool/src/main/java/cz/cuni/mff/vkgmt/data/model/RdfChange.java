package cz.cuni.mff.vkgmt.data.model;

import java.io.Serializable;

import cz.cuni.mff.vkgmt.data.common.Property;
import cz.cuni.mff.vkgmt.data.common.Uri;

/**
 * Change done in frontend.
 * 
 * @author Ales Woska
 *
 */
public class RdfChange implements Serializable {

	/**
	 * URI of changed instance.
	 */
	private Uri uri;

	/**
	 * Changed property. If it's an URI then it is reference to other
	 * RdfInstance.
	 */
	private Property property;

	/**
	 * Value before change. If is empty then the property is created.
	 */
	private Object oldValue;

	/**
	 * Value adter change. If is empty then the property is deleted.
	 */
	private Object newValue;

	public Uri getUri() {
		return uri;
	}

	public void setUri(Uri uri) {
		this.uri = uri;
	}

	public Property getProperty() {
		return property;
	}

	public void setProperty(Property property) {
		this.property = property;
	}

	public Object getOldValue() {
		return oldValue;
	}

	public void setOldValue(Object oldValue) {
		this.oldValue = oldValue;
	}

	public Object getNewValue() {
		return newValue;
	}

	public void setNewValue(Object newValue) {
		this.newValue = newValue;
	}

}
