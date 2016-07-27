package cz.cuni.mff.vkgmt.data.model;

import java.io.Serializable;

import cz.cuni.mff.vkgmt.data.common.Property;
import cz.cuni.mff.vkgmt.data.common.Uri;

/**
 * Property containing reference to other type's instance via its URI.
 * 
 * @author Ales Woska
 *
 */
public class RdfObjectProperty implements Serializable {

	/**
	 * URI of source instance
	 */
	private Uri subjectUri;

	/**
	 * URI of target instance
	 */
	private Uri objectUri;

	/**
	 * identifier of property which connects them.
	 */
	private Property property;

	public Property getProperty() {
		return property;
	}

	public void setProperty(Property property) {
		this.property = property;
	}

	public Uri getSubjectUri() {
		return subjectUri;
	}

	public void setSubjectUri(Uri subjectUri) {
		this.subjectUri = subjectUri;
	}

	public Uri getObjectUri() {
		return objectUri;
	}

	public void setObjectUri(Uri objectUri) {
		this.objectUri = objectUri;
	}

}
