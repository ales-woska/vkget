package cz.cuni.mff.vkgmt.data.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
	
	private List<RdfError> errors = new ArrayList<>();

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
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof RdfObjectProperty)) {
			return false;
		} else {
			RdfObjectProperty p = (RdfObjectProperty) o;
			return p.getProperty().equals(property) && p.getObjectUri().equals(objectUri) && p.getSubjectUri().equals(subjectUri);
		}
	}

	public List<RdfError> getErrors() {
		return errors;
	}

	public void setErrors(List<RdfError> errors) {
		this.errors = errors;
	}

}
