package cz.cuni.mff.vkgmt.connect;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.atlas.web.auth.SimpleAuthenticator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateProcessor;
import com.hp.hpl.jena.update.UpdateRequest;

/**
 * Connection to SPARQL endpoint on the lowest level.
 * @author Ales Woska
 *
 */
@Component(value = "settingsConnector")
public class SparqlConnector {
	
	@Value("${sparql.endpoint.url}")
	private String endpoint = "";
	
	@Value("${sparql.endpoint.url.auth}")
	private String endpointAuth = "";
	
	@Value("${sparql.endpoint.graph}")
	private String graph = "";
	
	@Value("${sparql.endpoint.user}")
	private String user = "";
	
	@Value("${sparql.endpoint.pass}")
	private String password = "";
	
	public SparqlConnector() {
	}
	
	public SparqlConnector(ConnectionInfo connectionInfo) {
		this.endpoint = connectionInfo.getEndpoint();
		this.user = connectionInfo.getUsername();
		this.password = connectionInfo.getPassword();
		if (connectionInfo.isUseNamedGraph()) {
			this.graph = connectionInfo.getNamedGraph();
		}
	}

	public ResultSet query(String sparqlQuery) {
		Query query = QueryFactory.create(sparqlQuery);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, query);
		ResultSet results = qexec.execSelect();
		return results;
	}
	
	public void executeQuery(String sparqlQuery) {
		String query = sparqlQuery;
		if (sparqlQuery.contains("INSERT DATA {")) {
			query = sparqlQuery.replace("INSERT DATA {", "INSERT DATA { GRAPH <" + graph + "> {") + "}";
			
		} else if (sparqlQuery.contains("DELETE {") && sparqlQuery.contains("INSERT {")) {
			query = sparqlQuery
					.replace("DELETE {", "DELETE { GRAPH <" + graph + "> {")
					.replace("} INSERT {", "}} INSERT { GRAPH <" + graph + "> {")
					.replace("} WHERE", "}} WHERE");
			
		} else if (sparqlQuery.contains("DELETE {")) {
			query = sparqlQuery.replace("DELETE {", "DELETE { GRAPH <" + graph + "> {").replace("} WHERE", "}} WHERE");
			
		} else if (sparqlQuery.contains("INSERT {")) {
			query = sparqlQuery.replace("INSERT {", "INSERT { GRAPH <" + graph + "> {").replace("} WHERE", "}} WHERE");
		}
		UpdateRequest request = UpdateFactory.create(query);
		UpdateProcessor processor = null;
		if (StringUtils.isNotEmpty(user)) {
			SimpleAuthenticator auth = new SimpleAuthenticator(user, password.toCharArray());
			processor = UpdateExecutionFactory.createRemote(request, endpointAuth, auth);
		} else {
			processor = UpdateExecutionFactory.createRemote(request, endpointAuth);
		}
		processor.execute();
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	

}
