package cz.cuni.mff.vkget.persistence;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Resource;

import cz.cuni.mff.vkget.layout.BlockLayout;
import cz.cuni.mff.vkget.rdf.SparqlConnector;

public class BlockLayoutDao {
	private final String GRAPH = "http://mff.cuni.cz/vkgetGraph";
	private final String NAMESPACE = "http://mff.cuni.cz/vkget#";
	private final String PREFIX = "vkget";
	private final String TYPE = PREFIX + ":blockLayout";
	private final String RDF_TYPE = "rdf:type";
	
	private final String FONT_COLOR = PREFIX + ":" + "fontColor";
	private final String FONT_SIZE = PREFIX + ":" + "fontSize";
	private final String LINE_COLOR = PREFIX + ":" + "lineColor";
	private final String LINE_TYPE = PREFIX + ":" + "lineType";
	private final String LINE_THICKNESS = PREFIX + ":" + "lineThickness";
	private final String TITLE = PREFIX + ":" + "title";
	private final String TITLE_TYPES = PREFIX + ":" + "titleTypes";
	
	private final String FOR_TYPE = PREFIX + ":" + "forType";
	private final String BACKGROUND = PREFIX + ":" + "background";
	private final String HEIGHT = PREFIX + ":" + "height";
	private final String WIDTH = PREFIX + ":" + "width";
	private final String LEFT = PREFIX + ":" + "left";
	private final String TOP = PREFIX + ":" + "top";

	private SparqlConnector sparql = new SparqlConnector("http://localhost:8890/sparql");
	
	public BlockLayout load(String uri) {
		String loadBlockQuery = 
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "PREFIX "+PREFIX+": <"+NAMESPACE+"> "
				+ "SELECT DISTINCT * WHERE { "
				+ " <" + uri + "> ?property ?value . "
				+ "}";
		ResultSet results = sparql.query(loadBlockQuery);
		BlockLayout layout = new BlockLayout();
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
				case LINE_COLOR: layout.setLineColor(value); break;
				case LINE_TYPE: layout.setLineType(value); break;
				case LINE_THICKNESS: layout.setLineThickness(Integer.valueOf(value)); break;
				case TITLE: layout.setTitle(value); break;
				case TITLE_TYPES: layout.setTitleTypesFromString(value); break;
				case FOR_TYPE: layout.setForType(value); break;
				case BACKGROUND: layout.setBackground(value); break;
				case HEIGHT: layout.setHeight(Integer.valueOf(value)); break;
				case WIDTH: layout.setWidth(Integer.valueOf(value)); break;
				case LEFT: layout.setLeft(Integer.valueOf(value)); break;
				case TOP: layout.setTop(Integer.valueOf(value)); break;
			}
		}
		return layout;
	}
	
	
    public void insert(BlockLayout layout) {
		
		StringBuilder insertQuery = new StringBuilder("PREFIX ").append(PREFIX).append(": <").append(NAMESPACE).append("> ");
		insertQuery.append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> ");
		insertQuery.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ");
		insertQuery.append("INSERT DATA { GRAPH <").append(GRAPH).append("> { ");
		insertQuery.append("<").append(layout.getUri()).append("> ");
		
		insertQuery.append(" ").append(RDF_TYPE).append(" \"").append(TYPE).append("\"; ");
		insertQuery.append(" ").append(FONT_COLOR).append(" \"").append(layout.getFontColor()).append("\"; ");
		insertQuery.append(" ").append(FONT_SIZE).append(" \"").append(layout.getFontSize()).append("\"; ");
		insertQuery.append(" ").append(LINE_COLOR).append(" \"").append(layout.getLineColor()).append("\"; ");
		insertQuery.append(" ").append(LINE_THICKNESS).append(" \"").append(layout.getLineThickness()).append("\"; ");
		insertQuery.append(" ").append(LINE_TYPE).append(" \"").append(layout.getLineType()).append("\"; ");
		insertQuery.append(" ").append(TITLE).append(" \"").append(layout.getTitle()).append("\"; ");
		insertQuery.append(" ").append(TITLE_TYPES).append(" \"").append(layout.getTitleTypesAsString()).append("\"; ");
		insertQuery.append(" ").append(FOR_TYPE).append(" \"").append(layout.getForType()).append("\"; ");
		insertQuery.append(" ").append(BACKGROUND).append(" \"").append(layout.getBackground()).append("\"; ");
		insertQuery.append(" ").append(HEIGHT).append(" \"").append(layout.getHeight()).append("\"; ");
		insertQuery.append(" ").append(WIDTH).append(" \"").append(layout.getWidth()).append("\"; ");
		insertQuery.append(" ").append(LEFT).append(" \"").append(layout.getLeft()).append("\"; ");
		insertQuery.append(" ").append(TOP).append(" \"").append(layout.getTop()).append("\". ");
//		insertQuery.append(" ").append().append(" \"").append(layout.getProperties()).append("\"; ");
		
		insertQuery.append("} }");
		
		sparql.insertQuery(insertQuery.toString());
		
		
    }
}
