package cz.cuni.mff.vkget.data.common;

public class Uri {
	private String uri;

	public Uri(String uri) {
		this.uri = uri;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
	
	@Override
	public String toString() {
		return uri;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Uri)) {
			return false;
		} else {
			Uri rdfUri = (Uri) o;
			return rdfUri.getUri().equals(this.uri);
		}
	}

}
