package cz.cuni.mff.vkget.data;

import java.io.Serializable;

public abstract class RdfEntity implements Serializable {
	protected String uri;
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
