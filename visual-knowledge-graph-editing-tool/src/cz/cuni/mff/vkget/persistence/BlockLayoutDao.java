package cz.cuni.mff.vkget.persistence;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.springframework.stereotype.Repository;

import cz.cuni.mff.vkget.connect.SparqlConnector;
import cz.cuni.mff.vkget.data.layout.BlockLayout;
import cz.cuni.mff.vkget.sparql.Constants;

@Repository
public class BlockLayoutDao implements SparqlDao<BlockLayout> {
	private final String FONT_COLOR = Constants.VKGET_Prefix + ":" + "fontColor";
	private final String FONT_SIZE = Constants.VKGET_Prefix + ":" + "fontSize";
	private final String LINE_COLOR = Constants.VKGET_Prefix + ":" + "lineColor";
	private final String LINE_TYPE = Constants.VKGET_Prefix + ":" + "lineType";
	private final String LINE_THICKNESS = Constants.VKGET_Prefix + ":" + "lineThickness";
	private final String TITLE = Constants.VKGET_Prefix + ":" + "title";
	private final String TITLE_TYPES = Constants.VKGET_Prefix + ":" + "titleTypes";
	private final String FOR_TYPE = Constants.VKGET_Prefix + ":" + "forType";
	private final String BACKGROUND = Constants.VKGET_Prefix + ":" + "background";
	private final String HEIGHT = Constants.VKGET_Prefix + ":" + "height";
	private final String WIDTH = Constants.VKGET_Prefix + ":" + "width";
	private final String LEFT = Constants.VKGET_Prefix + ":" + "left";
	private final String TOP = Constants.VKGET_Prefix + ":" + "top";

	private SparqlConnector sparql = SparqlConnector.getLocalFusekiConnector();
	
	@Override
	public BlockLayout load(String uri) {
		String loadBlockQuery = 
				Constants.PREFIX_PART
				+ "SELECT DISTINCT * WHERE { "
				+ " <" + uri + "> ?property ?value . "
				+ "}";
		ResultSet results = sparql.query(loadBlockQuery);
		BlockLayout layout = new BlockLayout();
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
				case LINE_COLOR: layout.setLineColor(value); break;
				case LINE_TYPE: layout.setLineTypeFromString(value); break;
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
	
	
    @Override
	public void insert(BlockLayout layout) {
		
		StringBuilder insertQuery = new StringBuilder(Constants.PREFIX_PART);
		insertQuery.append("INSERT DATA { ");
		insertQuery.append("<").append(layout.getUri()).append("> ");
		
		insertQuery.append(" ").append(Constants.RDF_TYPE).append(" \"").append(Constants.BlockLayoutType).append("\"; ");
		insertQuery.append(" ").append(FONT_COLOR).append(" \"").append(layout.getFontColor()).append("\"; ");
		insertQuery.append(" ").append(FONT_SIZE).append(" \"").append(layout.getFontSize()).append("\"; ");
		insertQuery.append(" ").append(LINE_COLOR).append(" \"").append(layout.getLineColor()).append("\"; ");
		insertQuery.append(" ").append(LINE_THICKNESS).append(" \"").append(layout.getLineThickness()).append("\"; ");
		insertQuery.append(" ").append(LINE_TYPE).append(" \"").append(layout.getLineTypeAsString()).append("\"; ");
		insertQuery.append(" ").append(TITLE).append(" \"").append(layout.getTitle()).append("\"; ");
		insertQuery.append(" ").append(TITLE_TYPES).append(" \"").append(layout.getTitleTypesAsString()).append("\"; ");
		insertQuery.append(" ").append(FOR_TYPE).append(" \"").append(layout.getForType()).append("\"; ");
		insertQuery.append(" ").append(BACKGROUND).append(" \"").append(layout.getBackground()).append("\"; ");
		insertQuery.append(" ").append(HEIGHT).append(" \"").append(layout.getHeight()).append("\"; ");
		insertQuery.append(" ").append(WIDTH).append(" \"").append(layout.getWidth()).append("\"; ");
		insertQuery.append(" ").append(LEFT).append(" \"").append(layout.getLeft()).append("\"; ");
		insertQuery.append(" ").append(TOP).append(" \"").append(layout.getTop()).append("\". ");
//		insertQuery.append(" ").append().append(" \"").append(layout.getProperties()).append("\"; ");
		
		insertQuery.append(" }");
		
		sparql.insertQuery(insertQuery.toString());
		
		
    }
}
