package cz.cuni.mff.vkget.rdf;

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

public class SparqlConnector {

	private static final String user = "dba";
	private static final String password = "dba";
	private String endpoint;
	
	public SparqlConnector(String endpoint) {
		this.endpoint = endpoint;
	}

	public ResultSet query(String sparqlQuery) {
        Query query = QueryFactory.create(sparqlQuery);
		HttpAuthenticator authenticator = new SimpleAuthenticator(user, password.toCharArray());
        QueryExecution qExe = QueryExecutionFactory.sparqlService(endpoint, query, authenticator);
        ResultSet results = qExe.execSelect();
		return results;
	}
	
	public void insertQuery(String sparqlQuery) {
		UpdateRequest update = new UpdateRequest();
		update.add(sparqlQuery);
		HttpAuthenticator authenticator = new SimpleAuthenticator(user, password.toCharArray());
		UpdateProcessor ue = UpdateExecutionFactory.createRemote(update, endpoint, authenticator);
		ue.execute();
	}

}
