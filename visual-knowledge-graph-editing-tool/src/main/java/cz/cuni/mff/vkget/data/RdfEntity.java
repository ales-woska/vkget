package cz.cuni.mff.vkget.data;

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
	protected String uri;
	
	/**
	 * rdf:type
	 */
	protected String type;

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
