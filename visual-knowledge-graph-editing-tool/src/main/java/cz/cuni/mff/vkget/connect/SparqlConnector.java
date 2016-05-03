package cz.cuni.mff.vkget.connect;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;

public class SparqlConnector {

	private static SparqlConnector fusekiInstance = new SparqlConnector("http://localhost:3030/vkget");
	
//	private static final String user = "dba";
//	private static final String password = "dba";
	private String endpoint;
	
	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public SparqlConnector() {
	}

	public SparqlConnector(String endpoint) {
		this.endpoint = endpoint;
	}
	
	public static SparqlConnector getLocalFusekiConnector() {
		return fusekiInstance;
	}

	public ResultSet query(String sparqlQuery) {
        Query query = QueryFactory.create(sparqlQuery);
//		HttpAuthenticator authenticator = new SimpleAuthenticator(user, password.toCharArray());
        QueryExecution qExe = QueryExecutionFactory.sparqlService(endpoint, query);
        ResultSet results = qExe.execSelect();
		return results;
	}
	
	public void insertQuery(String sparqlQuery) {
		UpdateRequest update = new UpdateRequest();
		update.add(sparqlQuery);
//		HttpAuthenticator authenticator = new SimpleAuthenticator(user, password.toCharArray());
		UpdateProcessor ue = UpdateExecutionFactory.createRemote(update, endpoint + "/update");
		ue.execute();
	}

}
