package cz.cuni.mff.vkgmt.data.model;

import cz.cuni.mff.vkgmt.data.common.Property;
import cz.cuni.mff.vkgmt.data.common.Uri;

public class RdfError {

	private Uri uri;
	private float value;
	private RdfErrorSeverity severity;
	private String description;
	private Uri subject;
	private Property property;
	private Object object;

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public RdfErrorSeverity getSeverity() {
		return severity;
	}

	public void setSeverity(RdfErrorSeverity severity) {
		this.severity = severity;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Uri getSubject() {
		return subject;
	}

	public void setSubject(Uri subject) {
		this.subject = subject;
	}

	public Property getProperty() {
		return property;
	}

	public void setProperty(Property property) {
		this.property = property;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public Uri getUri() {
		return uri;
	}

	public void setUri(Uri uri) {
		this.uri = uri;
	}

}
