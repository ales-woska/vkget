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

/**
 * Connection to SPARQL endpoint on the lowest level.
 * @author Ales Woska
 *
 */
public class SparqlConnector {
	private static SparqlConnector fusekiInstance = new SparqlConnector(new ConnectionInfo("http://localhost:3030/vkgmt", "", ""));
	
	private String user = "";
	private String password = "";
	
	/**
	 * Endpoint address.
	 */
	private String endpoint;
	
	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public SparqlConnector() {
	}

	public SparqlConnector(ConnectionInfo connectionInfo) {
		this.endpoint = connectionInfo.getEndpoint();
		this.user = connectionInfo.getUsername();
		this.password = connectionInfo.getPassword();
	}
	
	public static SparqlConnector getLocalFusekiConnector() {
		return fusekiInstance;
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

}
