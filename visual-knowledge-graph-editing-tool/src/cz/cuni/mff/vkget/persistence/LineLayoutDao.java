package cz.cuni.mff.vkget.persistence;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Resource;

import cz.cuni.mff.vkget.layout.LineLayout;
import cz.cuni.mff.vkget.rdf.SparqlConnector;

public class LineLayoutDao {
	private final String GRAPH = "http://mff.cuni.cz/vkgetGraph";
	private final String NAMESPACE = "http://mff.cuni.cz/vkget#";
	private final String PREFIX = "vkget";
	private final String TYPE = PREFIX + ":lineLayout";
	private final String RDF_TYPE = "rdf:type";
	
	private final String FONT_COLOR = PREFIX + ":" + "fontColor";
	private final String FONT_SIZE = PREFIX + ":" + "fontSize";
	private final String FROM_CLASS = PREFIX + ":" + "fromClass";
	private final String TO_CLASS = PREFIX + ":" + "toClass";
	private final String LINE_COLOR = PREFIX + ":" + "lineColor";
	private final String LINE_TYPE = PREFIX + ":" + "lineType";
	private final String LINE_THICKNESS = PREFIX + ":" + "lineThickness";
	private final String TITLE = PREFIX + ":" + "title";
	private final String TITLE_TYPES = PREFIX + ":" + "titleTypes";
	private final String POINTS = PREFIX + ":" + "points";

	private SparqlConnector sparql = new SparqlConnector("http://localhost:8890/sparql");
	
	

	
	public LineLayout load(String uri) {
		String loadBlockQuery = 
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "PREFIX vkget: <http://mff.cuni.cz/vkget#> "
				+ "SELECT DISTINCT * WHERE { "
				+ " <" + uri + "> ?property ?value . "
				+ "}";
		ResultSet results = sparql.query(loadBlockQuery);
		LineLayout layout = new LineLayout();
		layout.setUri(uri);
		while (results.hasNext()) {
			QuerySolution solution = results.next();
			
			Resource resource = solution.get("property").asResource();
			String property = PREFIX + ":" + resource.getLocalName();
			String value = solution.get("value").asLiteral().getString();
			if (value == "null") {
				continue;
			}
			
			switch (property) {
				case FONT_COLOR: layout.setFontColor(value); break;
				case FONT_SIZE: layout.setFontSize(Integer.valueOf(value)); break;
				case FROM_CLASS: layout.setFromClass(value); break;
				case TO_CLASS: layout.setToClass(value); break;
				case LINE_COLOR: layout.setLineColor(value); break;
				case LINE_TYPE: layout.setLineType(value); break;
				case LINE_THICKNESS: layout.setLineThickness(Integer.valueOf(value)); break;
				case TITLE: layout.setTitle(value); break;
				case TITLE_TYPES: layout.setTitleTypesFromString(value); break;
				case POINTS: layout.setPointsFromString(value); break;
			}
		}
		return layout;
	}
	
	
    public void insert(LineLayout layout) {
		
		StringBuilder insertQuery = new StringBuilder("PREFIX ").append(PREFIX).append(": <").append(NAMESPACE).append("> ");
		insertQuery.append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> ");
		insertQuery.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ");
		insertQuery.append("INSERT DATA { GRAPH <").append(GRAPH).append("> { ");
		insertQuery.append("<").append(layout.getUri()).append("> ");
		
		insertQuery.append(" ").append(RDF_TYPE).append(" \"").append(TYPE).append("\"; ");
		insertQuery.append(" ").append(FONT_COLOR).append(" \"").append(layout.getFontColor()).append("\"; ");
		insertQuery.append(" ").append(FONT_SIZE).append(" \"").append(layout.getFontSize()).append("\"; ");
		insertQuery.append(" ").append(FROM_CLASS).append(" \"").append(layout.getFromClass()).append("\"; ");
		insertQuery.append(" ").append(TO_CLASS).append(" \"").append(layout.getToClass()).append("\"; ");
		insertQuery.append(" ").append(LINE_COLOR).append(" \"").append(layout.getLineColor()).append("\"; ");
		insertQuery.append(" ").append(LINE_THICKNESS).append(" \"").append(layout.getLineThickness()).append("\"; ");
		insertQuery.append(" ").append(LINE_TYPE).append(" \"").append(layout.getLineType()).append("\"; ");
		insertQuery.append(" ").append(TITLE).append(" \"").append(layout.getTitle()).append("\"; ");
		insertQuery.append(" ").append(TITLE_TYPES).append(" \"").append(layout.getTitleTypesAsString()).append("\"; ");
		insertQuery.append(" ").append(POINTS).append(" \"").append(layout.getPointsAsString()).append("\". ");
		
		insertQuery.append("} }");
		
		sparql.insertQuery(insertQuery.toString());
		
		
    }
    
}
