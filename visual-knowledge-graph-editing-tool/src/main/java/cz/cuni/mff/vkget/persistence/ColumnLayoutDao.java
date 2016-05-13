package cz.cuni.mff.vkget.persistence;

import java.util.List;

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
public class ColumnLayoutDao extends AbstractDao<ColumnLayout> {
	private final Property LABEL_SOURCE = new Property(Constants.VKGET_Prefix, "labelSource");
	private final Property LABEL_TYPE = new Property(Constants.VKGET_Prefix, "labelType");
	private final Property LABEL_LANG = new Property(Constants.VKGET_Prefix, "labelLang");
	private final Property PROPERTY = new Property(Constants.VKGET_Prefix, "property");
	private final Property AGGREGATE_FUNCTIONS = new Property(Constants.VKGET_Prefix, "aggregateFunctions");
	
	private SparqlConnector sparql = SparqlConnector.getLocalFusekiConnector();
	
	@Override
	protected SparqlConnector getSparqlConnector() {
		return sparql;
	}
	
	/**
	 * @inheritDoc
	 */
	@Override
	public ColumnLayout get(Uri uri) {
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

			if (property.equals(LABEL_SOURCE)) {
				layout.getLabel().setLabelSource(value);
			}
			if (property.equals(LABEL_LANG)) {
				layout.getLabel().setLang(value);
			}
			if (property.equals(LABEL_TYPE)) {
				layout.getLabel().setType(LabelType.fromString(value));
			}
			if (property.equals(PROPERTY)) {
				layout.setProperty(new Property(value));
			}
			if (property.equals(AGGREGATE_FUNCTIONS)) {
				layout.setAggregateFunction(AggregateFunction.fromString(value));
			}
		}
		return layout;
	}
	
	/**
	 * @inheritDoc
	 */
    @Override
	public void insert(ColumnLayout layout) {
		StringBuilder updateQuery = new StringBuilder(Constants.PREFIX_PART);
		updateQuery.append("INSERT DATA { ");
		updateQuery.append("<").append(layout.getUri()).append("> ");
		
		updateQuery.append(" ").append(Constants.RDF_TYPE).append(" \"").append(Constants.ColumnLayoutType).append("\"; ");
		updateQuery.append(" ").append(LABEL_SOURCE).append(" \"").append(layout.getLabel().getLabelSource()).append("\"; ");
		updateQuery.append(" ").append(LABEL_TYPE).append(" \"").append(layout.getLabel().getType()).append("\"; ");
		updateQuery.append(" ").append(LABEL_LANG).append(" \"").append(layout.getLabel().getLang()).append("\"; ");
		updateQuery.append(" ").append(PROPERTY).append(" \"").append(layout.getProperty()).append("\"; ");
		updateQuery.append(" ").append(AGGREGATE_FUNCTIONS).append(" \"").append(layout.getAggregateFunction().name()).append("\". ");
		
		updateQuery.append(" }");
		
		sparql.executeQuery(updateQuery.toString());
    }

	@Override
	public void update(ColumnLayout layout) {
		StringBuilder updateQuery = new StringBuilder(Constants.PREFIX_PART);
		updateQuery.append("DELETE { <").append(layout.getUri()).append("> ?p ?o");
		updateQuery.append("INSERT { <").append(layout.getUri()).append("> ");

		updateQuery.append(" ").append(Constants.RDF_TYPE).append(" \"").append(Constants.ColumnLayoutType).append("\"; ");
		updateQuery.append(" ").append(LABEL_SOURCE).append(" \"").append(layout.getLabel().getLabelSource()).append("\"; ");
		updateQuery.append(" ").append(LABEL_TYPE).append(" \"").append(layout.getLabel().getType()).append("\"; ");
		updateQuery.append(" ").append(LABEL_LANG).append(" \"").append(layout.getLabel().getLang()).append("\"; ");
		updateQuery.append(" ").append(PROPERTY).append(" \"").append(layout.getProperty()).append("\"; ");
		updateQuery.append(" ").append(AGGREGATE_FUNCTIONS).append(" \"").append(layout.getAggregateFunction().name()).append("\". ");
		
		updateQuery.append(" } WHERE { <").append(layout.getUri()).append("> ?p ?o . }");
    	
    	sparql.executeQuery(updateQuery.toString());
	}

	@Override
	public List<ColumnLayout> getAll() {
		// TODO Auto-generated method stub
		return null;
	}
}