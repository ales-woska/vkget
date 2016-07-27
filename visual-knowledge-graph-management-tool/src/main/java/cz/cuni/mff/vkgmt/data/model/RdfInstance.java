package cz.cuni.mff.vkgmt.data.model;

import java.io.Serializable;
import java.util.List;

import cz.cuni.mff.vkgmt.data.common.Type;
import cz.cuni.mff.vkgmt.data.common.Uri;

/**
 * Instance of RDF object ~ table row
 * @author Ales Woska
 *
 */
public class RdfInstance implements Serializable {
	
	/**
	 * Instance URI
	 */
	private Uri uri;
	
	/**
	 * rdf:type, is same as RdfTable.type
	 */
	private Type type;
	
	/**
	 * properties leading to other table
	 */
	private List<RdfObjectProperty> objectProperties;
	
	/**
	 * instance properties ~ table cells
	 */
	private List<RdfLiteralProperty> literalProperties;

	public Uri getUri() {
		return uri;
	}

	public void setUri(Uri uri) {
		this.uri = uri;
	}

	public List<RdfObjectProperty> getObjectProperties() {
		return objectProperties;
	}

	public void setObjectProperties(List<RdfObjectProperty> objectProperties) {
		this.objectProperties = objectProperties;
	}

	public List<RdfLiteralProperty> getLiteralProperties() {
		return literalProperties;
	}

	public void setLiteralProperties(List<RdfLiteralProperty> literalProperties) {
		this.literalProperties = literalProperties;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Object getValueForProperty(String property) {
		for (RdfLiteralProperty rp: literalProperties) {
			if (rp.getProperty().equals(property)) {
				return rp.getValue();
			}
		}
		return null;
	}

}
