package cz.cuni.mff.vkget.data.common;

import java.io.Serializable;

/**
 * Common parent class for entities to be stored into SPARQL storage.
 * @author Ales Woska
 *
 */
public abstract class RdfEntity implements Serializable {
	/**
	 * unique resource identifier
	 */
	protected Uri uri;
	
	/**
	 * rdf:type
	 */
	protected Type type;

	public Uri getUri() {
		return uri;
	}

	public void setUri(Uri uri) {
		this.uri = uri;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

}
