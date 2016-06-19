package cz.cuni.mff.vkget.persistence;

import java.util.List;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.springframework.stereotype.Repository;

import cz.cuni.mff.vkget.connect.SparqlConnector;
import cz.cuni.mff.vkget.data.common.Property;
import cz.cuni.mff.vkget.data.common.RdfEntity;
import cz.cuni.mff.vkget.data.common.Type;
import cz.cuni.mff.vkget.data.common.Uri;
import cz.cuni.mff.vkget.data.layout.Label;
import cz.cuni.mff.vkget.data.layout.LabelType;
import cz.cuni.mff.vkget.data.layout.LineLayout;
import cz.cuni.mff.vkget.data.layout.LineType;
import cz.cuni.mff.vkget.sparql.Constants;

/**
 * LineLayout DAO
 * @author Ales Woska
 *
 */
@Repository
public class LineLayoutDao extends AbstractDao<LineLayout> {
	private final Property FONT_COLOR = new Property(Constants.VKGET_Prefix, "fontColor");
	private final Property FONT_SIZE = new Property(Constants.VKGET_Prefix, "fontSize");
	private final Property FROM_TYPE = new Property(Constants.VKGET_Prefix, "fromType");
	private final Property PROPERTY = new Property(Constants.VKGET_Prefix, "property");
	private final Property TO_TYPE = new Property(Constants.VKGET_Prefix, "toType");
	private final Property LINE_COLOR = new Property(Constants.VKGET_Prefix, "lineColor");
	private final Property LINE_TYPE = new Property(Constants.VKGET_Prefix, "lineType");
	private final Property LINE_THICKNESS = new Property(Constants.VKGET_Prefix, "lineThickness");
	private final Property LABEL_SOURCE = new Property(Constants.VKGET_Prefix, "labelSource");
	private final Property LABEL_TYPE = new Property(Constants.VKGET_Prefix, "labelType");
	private final Property LABEL_LANG = new Property(Constants.VKGET_Prefix, "labelLang");
	private final Property POINTS = new Property(Constants.VKGET_Prefix, "points");

	
	private SparqlConnector sparql = SparqlConnector.getLocalFusekiConnector();
	
	@Override
	protected SparqlConnector getSparqlConnector() {
		return sparql;
	}
	
	/**
	 * @inheritDoc
	 */
	@Override
	public LineLayout get(Uri uri) {
		String loadBlockQuery = 
				Constants.PREFIX_PART
				+ "SELECT DISTINCT * WHERE { "
				+ " <" + uri + "> ?property ?value . "
				+ "}";
		ResultSet results = sparql.query(loadBlockQuery);
		LineLayout layout = new LineLayout();
		layout.setUri(uri);
		layout.setLabel(new Label());
		while (results.hasNext()) {
			QuerySolution solution = results.next();
			
			Resource resource = solution.get("property").asResource();
			Property property = new Property(Constants.VKGET_Prefix, resource.getLocalName());
			RDFNode node = solution.get("value");
			String value = (node.isLiteral()) ? node.asLiteral().getString() : node.asResource().getURI();
			if (value == "null") {
				continue;
			}

			if (property.equals(FONT_COLOR)) {
				layout.setFontColor(value);
			} else if (property.equals(FONT_SIZE)) {
				layout.setFontSize(Integer.valueOf(value));
			} else if (property.equals(FROM_TYPE)) {
				layout.setFromType(new Type(value));
			} else if (property.equals(PROPERTY)) {
				layout.setProperty(new Property(value));
			} else if (property.equals(TO_TYPE)) {
				layout.setToType(new Type(value));
			} else if (property.equals(LINE_COLOR)) {
				layout.setLineColor(value);
			} else if (property.equals(LINE_TYPE)) {
				layout.setLineType(LineType.fromString(value));
			} else if (property.equals(LINE_THICKNESS)) {
				layout.setLineThickness(Integer.valueOf(value));
			} else if (property.equals(LABEL_SOURCE)) {
				layout.getLabel().setLabelSource(value);
			} else if (property.equals(LABEL_LANG)) {
				layout.getLabel().setLang(value);
			} else if (property.equals(LABEL_TYPE)) {
				layout.getLabel().setType(LabelType.fromString(value));
			} else if (property.equals(POINTS)) {
				layout.setPointsFromString(value);
			}
		}
		return layout;
	}
	
	/**
	 * @inheritDoc
	 */
    @Override
	public void insert(LineLayout layout, RdfEntity parent) {
		String rawUri = parent.getUri().getUri() + "_" + escepeUri(layout.getFromType().getType() + "_" + layout.getToType().getType());
		layout.setUri(getUniqueId(rawUri));
		
		StringBuilder insertQuery = new StringBuilder(Constants.PREFIX_PART);
		insertQuery.append("INSERT DATA { ");
		insertQuery.append("<").append(layout.getUri().getUri()).append("> ");
		
		insertQuery.append(" ").append(Constants.RDF_TYPE).append(" ").append(Constants.LineLayoutType).append("; ");
		insertQuery.append(" ").append(FONT_COLOR).append(" '").append(layout.getFontColor()).append("'; ");
		insertQuery.append(" ").append(FONT_SIZE).append(" '").append(layout.getFontSize()).append("'; ");
		insertQuery.append(" ").append(PROPERTY).append(" '").append(layout.getProperty()).append("'; ");
		insertQuery.append(" ").append(FROM_TYPE).append(" '").append(layout.getFromType().getType()).append("'; ");
		insertQuery.append(" ").append(TO_TYPE).append(" '").append(layout.getToType().getType()).append("'; ");
		insertQuery.append(" ").append(LINE_COLOR).append(" '").append(layout.getLineColor()).append("'; ");
		insertQuery.append(" ").append(LINE_THICKNESS).append(" '").append(layout.getLineThickness()).append("'; ");
		insertQuery.append(" ").append(LINE_TYPE).append(" '").append(layout.getLineType().name()).append("'; ");
		insertQuery.append(" ").append(LABEL_SOURCE).append(" '").append(layout.getLabel().getLabelSource()).append("'; ");
		insertQuery.append(" ").append(LABEL_TYPE).append(" '").append(layout.getLabel().getType()).append("'; ");
		insertQuery.append(" ").append(LABEL_LANG).append(" '").append(layout.getLabel().getLang()).append("'; ");
		insertQuery.append(" ").append(POINTS).append(" '").append(layout.getPointsAsString()).append("'. ");
		
		insertQuery.append(" } ");
		
		sparql.executeQuery(insertQuery.toString());
    }

	/**
	 * @inheritDoc
	 */
    @Override
	public void update(LineLayout layout) {
		StringBuilder updateQuery = new StringBuilder(Constants.PREFIX_PART);
		updateQuery.append("DELETE { <").append(layout.getUri().getUri()).append("> ?p ?o } ");
		updateQuery.append("INSERT { <").append(layout.getUri().getUri()).append("> ");
		
		updateQuery.append(" ").append(Constants.RDF_TYPE).append(" ").append(Constants.LineLayoutType).append("; ");
		updateQuery.append(" ").append(FONT_COLOR).append(" '").append(layout.getFontColor()).append("'; ");
		updateQuery.append(" ").append(FONT_SIZE).append(" '").append(layout.getFontSize()).append("'; ");
		updateQuery.append(" ").append(PROPERTY).append(" '").append(layout.getProperty()).append("'; ");
		updateQuery.append(" ").append(FROM_TYPE).append(" '").append(layout.getFromType().getType()).append("'; ");
		updateQuery.append(" ").append(TO_TYPE).append(" '").append(layout.getToType().getType()).append("'; ");
		updateQuery.append(" ").append(LINE_COLOR).append(" '").append(layout.getLineColor()).append("'; ");
		updateQuery.append(" ").append(LINE_THICKNESS).append(" '").append(layout.getLineThickness()).append("'; ");
		updateQuery.append(" ").append(LINE_TYPE).append(" '").append(layout.getLineType().name()).append("'; ");
		updateQuery.append(" ").append(LABEL_SOURCE).append(" '").append(layout.getLabel().getLabelSource()).append("'; ");
		updateQuery.append(" ").append(LABEL_TYPE).append(" '").append(layout.getLabel().getType()).append("'; ");
		updateQuery.append(" ").append(LABEL_LANG).append(" '").append(layout.getLabel().getLang()).append("'; ");
		updateQuery.append(" ").append(POINTS).append(" '").append(layout.getPointsAsString()).append("'. ");
		updateQuery.append(" } WHERE { <").append(layout.getUri().getUri()).append("> ?p ?o . }");
    	
    	sparql.executeQuery(updateQuery.toString());
    }
	
	/**
	 * @inheritDoc
	 */
	@Override
	public void insertOrUpdate(LineLayout layout, RdfEntity parent) {
		if (exists(layout)) {
			update(layout);
		} else {
			insert(layout, parent);
		}
	}

	@Override
	public List<LineLayout> getAll() {
		// TODO Auto-generated method stub
		return null;
	}
    
}
