package cz.cuni.mff.vkgmt.connect;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.atlas.web.auth.HttpAuthenticator;
import org.apache.jena.atlas.web.auth.SimpleAuthenticator;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Connection to SPARQL endpoint on the lowest level.
 * @author Ales Woska
 *
 */
@Component(value = "settingsConnector")
public class SparqlConnector {
	
	@Value("${sparql.endpoint.url}")
	private String endpoint = "";
	
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
	}

	public ResultSet query(String sparqlQuery) {
        Query query = QueryFactory.create(sparqlQuery);
        QueryExecution qExe = null;
        if (StringUtils.isNotEmpty(user)) {
        	HttpAuthenticator authenticator = new SimpleAuthenticator(user, password.toCharArray());
        	qExe = QueryExecutionFactory.sparqlService(endpoint, query, authenticator);
        } else {
        	qExe = QueryExecutionFactory.sparqlService(endpoint, query);
        }
        ResultSet results = qExe.execSelect();
		return results;
	}
	
	public void executeQuery(String sparqlQuery) {
		UpdateRequest update = new UpdateRequest();
		update.add(sparqlQuery);
		UpdateProcessor ue = null;
		if (StringUtils.isNotEmpty(user)) {
			HttpAuthenticator authenticator = new SimpleAuthenticator(user, password.toCharArray());
			ue = UpdateExecutionFactory.createRemote(update, endpoint + "/update", authenticator);
		} else {
			ue = UpdateExecutionFactory.createRemote(update, endpoint + "/update");
		}
		ue.execute();
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
