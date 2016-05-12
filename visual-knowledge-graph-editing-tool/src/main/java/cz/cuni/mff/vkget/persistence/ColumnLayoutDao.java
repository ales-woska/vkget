package cz.cuni.mff.vkget.persistence;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.springframework.stereotype.Repository;

import cz.cuni.mff.vkget.connect.SparqlConnector;
import cz.cuni.mff.vkget.data.common.Property;
import cz.cuni.mff.vkget.data.common.Uri;
import cz.cuni.mff.vkget.data.layout.AggregateFunction;
import cz.cuni.mff.vkget.data.layout.ColumnLayout;
import cz.cuni.mff.vkget.data.layout.Label;
import cz.cuni.mff.vkget.data.layout.LabelType;
import cz.cuni.mff.vkget.sparql.Constants;

/**
 * ColumnLayout DAO
 * @author Ales Woska
 *
 */
@Repository
public class ColumnLayoutDao implements SparqlDao<ColumnLayout> {
	private final String LABEL_SOURCE = Constants.VKGET_Prefix + ":" + "labelSource";
	private final String LABEL_TYPE = Constants.VKGET_Prefix + ":" + "labelType";
	private final String LABEL_LANG = Constants.VKGET_Prefix + ":" + "labelLang";
	private final String PROPERTY = Constants.VKGET_Prefix + ":" + "property";
	private final String AGGREGATE_FUNCTIONS = Constants.VKGET_Prefix + ":" + "aggregateFunctions";

	private SparqlConnector sparql = SparqlConnector.getLocalFusekiConnector();
	
	/**
	 * @inheritDoc
	 */
	@Override
	public ColumnLayout load(Uri uri) {
		String loadRowQuery = 
				Constants.PREFIX_PART
				+ "SELECT DISTINCT * WHERE { "
				+ " <" + uri + "> ?property ?value . "
				+ "}";
		ResultSet results = sparql.query(loadRowQuery);
		ColumnLayout layout = new ColumnLayout();
		layout.setUri(uri);
		layout.setLabel(new Label());
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
				case LABEL_SOURCE: layout.getLabel().setLabelSource(value); break;
				case LABEL_LANG: layout.getLabel().setLang(value); break;
				case LABEL_TYPE: layout.getLabel().setType(LabelType.fromString(value)); break;
				case PROPERTY: layout.setProperty(new Property(value)); break;
				case AGGREGATE_FUNCTIONS: layout.setAggregateFunction(AggregateFunction.fromString(value)); break;
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
		insertQuery.append(" ").append(LABEL_SOURCE).append(" \"").append(layout.getLabel().getLabelSource()).append("\"; ");
		insertQuery.append(" ").append(LABEL_TYPE).append(" \"").append(layout.getLabel().getType()).append("\"; ");
		insertQuery.append(" ").append(LABEL_LANG).append(" \"").append(layout.getLabel().getLang()).append("\"; ");
		insertQuery.append(" ").append(PROPERTY).append(" \"").append(layout.getProperty()).append("\"; ");
		insertQuery.append(" ").append(AGGREGATE_FUNCTIONS).append(" \"").append(layout.getAggregateFunction().name()).append("\". ");
		
		insertQuery.append(" }");
		
		sparql.insertQuery(insertQuery.toString());
		
		
    }
}
