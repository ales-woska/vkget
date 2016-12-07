package cz.cuni.mff.vkgmt.connect;


public class ConnectionInfo {
	String endpoint;
	EndpointType type;
	boolean useNamedGraph;
	boolean useAutorization;
	String namedGraph;
	String username;
	String password;
	
	public ConnectionInfo() {};
	
	public ConnectionInfo(String endpoint, String username, String password) {
		this.endpoint = endpoint;
		this.username = username;
		this.password = password;
		useAutorization = true;
		useNamedGraph = true;
		type = EndpointType.other;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public EndpointType getType() {
		return type;
	}

	public void setType(EndpointType type) {
		this.type = type;
	}

	public boolean isUseNamedGraph() {
		return useNamedGraph;
	}

	public void setUseNamedGraph(boolean useNamedGraph) {
		this.useNamedGraph = useNamedGraph;
	}

	public boolean isUseAutorization() {
		return useAutorization;
	}

	public void setUseAutorization(boolean useAutorization) {
		this.useAutorization = useAutorization;
	}

	public String getNamedGraph() {
		return namedGraph;
	}

	public void setNamedGraph(String namedGraph) {
		this.namedGraph = namedGraph;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
