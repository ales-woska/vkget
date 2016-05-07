package cz.cuni.mff.vkget.data.model;

import java.io.Serializable;

/**
 * Property which value is RdfSubject
 * 
 * @author Ales Woska
 *
 */
public class RdfObjectProperty implements Serializable {
	private String property;
	private String subjectUri;
	private String objectUri;

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
