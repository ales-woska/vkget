package cz.cuni.mff.vkget.persistence;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.springframework.stereotype.Repository;

import cz.cuni.mff.vkget.connect.SparqlConnector;
import cz.cuni.mff.vkget.data.layout.ColumnLayout;
import cz.cuni.mff.vkget.sparql.Constants;

/**
 * ColumnLayout DAO
 * @author Ales Woska
 *
 */
@Repository
public class ColumnLayoutDao implements SparqlDao<ColumnLayout> {
	private final String TITLE = Constants.VKGET_Prefix + ":" + "title";
	private final String TITLE_TYPES = Constants.VKGET_Prefix + ":" + "titleTypes";
	private final String PROPERTY = Constants.VKGET_Prefix + ":" + "property";
	private final String AGGREGATE_FUNCTIONS = Constants.VKGET_Prefix + ":" + "aggregateFunctions";

	private SparqlConnector sparql = SparqlConnector.getLocalFusekiConnector();
	
	/**
	 * @inheritDoc
	 */
	@Override
	public ColumnLayout load(String uri) {
		String loadRowQuery = 
				Constants.PREFIX_PART
				+ "SELECT DISTINCT * WHERE { "
				+ " <" + uri + "> ?property ?value . "
				+ "}";
		ResultSet results = sparql.query(loadRowQuery);
		ColumnLayout layout = new ColumnLayout();
		layout.setUri(uri);
		while (results.hasNext()) {
			QuerySolution solution = results.next();
			
			Resource resource = solution.get("property").asResource();
			String property = Constants.VKGET_Prefix + ":" + resource.getLocalName();
			
			RDFNode node = solution.get("value");
			String value = (node.isLiteral()) ? node.asLiteral().getString() : node.asResource().getURI();
			if (value == "null") {
				continue;
			}

			switch (property) {
				case TITLE: layout.setTitle(value); break;
				case TITLE_TYPES: layout.setTitleTypesFromString(value); break;
				case PROPERTY: layout.setProperty(value); break;
				case AGGREGATE_FUNCTIONS: layout.setAggregateFunctionsFromString(value); break;
			}
		}
		return layout;
	}
	
	/**
	 * @inheritDoc
	 */
    @Override
	public void insert(ColumnLayout layout) {
		
		StringBuilder insertQuery = new StringBuilder(Constants.PREFIX_PART);
		insertQuery.append("INSERT DATA { ");
		insertQuery.append("<").append(layout.getUri()).append("> ");
		
		insertQuery.append(" ").append(Constants.RDF_TYPE).append(" \"").append(Constants.ColumnLayoutType).append("\"; ");
		insertQuery.append(" ").append(TITLE).append(" \"").append(layout.getTitle()).append("\"; ");
		insertQuery.append(" ").append(TITLE_TYPES).append(" \"").append(layout.getTitleTypesAsString()).append("\"; ");
		insertQuery.append(" ").append(PROPERTY).append(" \"").append(layout.getProperty()).append("\"; ");
		insertQuery.append(" ").append(AGGREGATE_FUNCTIONS).append(" \"").append(layout.getAggregateFunctionsAsString()).append("\". ");
		
		insertQuery.append(" }");
		
		sparql.insertQuery(insertQuery.toString());
		
		
    }
}
