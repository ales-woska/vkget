package cz.cuni.mff.vkget.data.model;

import java.io.Serializable;

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
	private String subjectUri;
	
	/**
	 * URI of target instance
	 */
	private String objectUri;
	
	/**
	 * identifier of property which connects them.
	 */
	private String property;

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getSubjectUri() {
		return subjectUri;
	}

	public void setSubjectUri(String subjectUri) {
		this.subjectUri = subjectUri;
	}

	public String getObjectUri() {
		return objectUri;
	}

	public void setObjectUri(String objectUri) {
		this.objectUri = objectUri;
	}

}
