package cz.cuni.mff.vkget.persistence;

import java.util.List;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

import cz.cuni.mff.vkget.connect.SparqlConnector;
import cz.cuni.mff.vkget.data.common.RdfEntity;
import cz.cuni.mff.vkget.data.common.Uri;
import cz.cuni.mff.vkget.sparql.Constants;

public abstract class AbstractDao<T extends RdfEntity> implements SparqlDao<T> {
	protected abstract SparqlConnector getSparqlConnector();
    
    @Override
    public void delete(T layout) {
    	StringBuilder deleteQuery = new StringBuilder(Constants.PREFIX_PART);
    	deleteQuery.append("DELETE { <").append(layout.getUri()).append("> ?p ?o");
    	deleteQuery.append(" } WHERE { <").append(layout.getUri()).append("> ?p ?o . }");
    	getSparqlConnector().executeQuery(deleteQuery.toString());
    }

	@Override
	public boolean exists(T layout) {
		StringBuilder existsQuery = new StringBuilder(Constants.PREFIX_PART);
		existsQuery.append("SELECT DISTINCT (count(?type) as ?typeCount)  WHERE { <");
		existsQuery.append(layout.getUri().getUri());
		existsQuery.append("> rdf:type ?type . } GROUP BY ?type");
		ResultSet results = getSparqlConnector().query(existsQuery.toString());
		while (results.hasNext()) {
			QuerySolution solution = results.next();
			int resultCount = solution.getLiteral("typeCount").asLiteral().getInt();
			if (resultCount == 0) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public abstract void insert(T entity);

	@Override
	public abstract void update(T entity);

	@Override
	public abstract T get(Uri uri);

	@Override
	public abstract List<T> getAll();

}
