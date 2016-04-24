package cz.cuni.mff.vkget.persistence;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.springframework.stereotype.Repository;

import cz.cuni.mff.vkget.connect.SparqlConnector;
import cz.cuni.mff.vkget.data.layout.LineLayout;
import cz.cuni.mff.vkget.sparql.Constants;

@Repository
public class LineLayoutDao implements SparqlDao<LineLayout> {
	private final String FONT_COLOR = Constants.VKGET_Prefix + ":" + "fontColor";
	private final String FONT_SIZE = Constants.VKGET_Prefix + ":" + "fontSize";
	private final String FROM_TYPE = Constants.VKGET_Prefix + ":" + "fromType";
	private final String PROPERTY = Constants.VKGET_Prefix + ":" + "property";
	private final String TO_TYPE = Constants.VKGET_Prefix + ":" + "toType";
	private final String LINE_COLOR = Constants.VKGET_Prefix + ":" + "lineColor";
	private final String LINE_TYPE = Constants.VKGET_Prefix + ":" + "lineType";
	private final String LINE_THICKNESS = Constants.VKGET_Prefix + ":" + "lineThickness";
	private final String TITLE = Constants.VKGET_Prefix + ":" + "title";
	private final String TITLE_TYPES = Constants.VKGET_Prefix + ":" + "titleTypes";
	private final String POINTS = Constants.VKGET_Prefix + ":" + "points";

	private SparqlConnector sparql = SparqlConnector.getLocalFusekiConnector();
	
	@Override
	public LineLayout load(String uri) {
		String loadBlockQuery = 
				Constants.PREFIX_PART
				+ "SELECT DISTINCT * WHERE { "
				+ " <" + uri + "> ?property ?value . "
				+ "}";
		ResultSet results = sparql.query(loadBlockQuery);
		LineLayout layout = new LineLayout();
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
				case FONT_COLOR: layout.setFontColor(value); break;
				case FONT_SIZE: layout.setFontSize(Integer.valueOf(value)); break;
				case FROM_TYPE: layout.setFromType(value); break;
				case PROPERTY: layout.setProperty(value); break;
				case TO_TYPE: layout.setToType(value); break;
				case LINE_COLOR: layout.setLineColor(value); break;
				case LINE_TYPE: layout.setLineTypeFromString(value); break;
				case LINE_THICKNESS: layout.setLineThickness(Integer.valueOf(value)); break;
				case TITLE: layout.setTitle(value); break;
				case TITLE_TYPES: layout.setTitleTypesFromString(value); break;
				case POINTS: layout.setPointsFromString(value); break;
			}
		}
		return layout;
	}
	
	
    @Override
	public void insert(LineLayout layout) {
		
		StringBuilder insertQuery = new StringBuilder(Constants.PREFIX_PART);
		insertQuery.append("INSERT DATA { ");
		insertQuery.append("<").append(layout.getUri()).append("> ");
		
		insertQuery.append(" ").append(Constants.RDF_TYPE).append(" ").append(Constants.LineLayoutType).append("; ");
		insertQuery.append(" ").append(FONT_COLOR).append(" \"").append(layout.getFontColor()).append("\"; ");
		insertQuery.append(" ").append(FONT_SIZE).append(" \"").append(layout.getFontSize()).append("\"; ");
		insertQuery.append(" ").append(PROPERTY).append(" \"").append(layout.getProperty()).append("\"; ");
		insertQuery.append(" ").append(FROM_TYPE).append(" \"").append(layout.getFromType()).append("\"; ");
		insertQuery.append(" ").append(TO_TYPE).append(" \"").append(layout.getToType()).append("\"; ");
		insertQuery.append(" ").append(LINE_COLOR).append(" \"").append(layout.getLineColor()).append("\"; ");
		insertQuery.append(" ").append(LINE_THICKNESS).append(" \"").append(layout.getLineThickness()).append("\"; ");
		insertQuery.append(" ").append(LINE_TYPE).append(" \"").append(layout.getLineTypeAsString()).append("\"; ");
		insertQuery.append(" ").append(TITLE).append(" \"").append(layout.getTitle()).append("\"; ");
		insertQuery.append(" ").append(TITLE_TYPES).append(" \"").append(layout.getTitleTypesAsString()).append("\"; ");
		insertQuery.append(" ").append(POINTS).append(" \"").append(layout.getPointsAsString()).append("\". ");
		
		insertQuery.append(" } ");
		
		sparql.insertQuery(insertQuery.toString());
		
		
    }
    
}
