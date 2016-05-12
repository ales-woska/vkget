package cz.cuni.mff.vkget.persistence;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cz.cuni.mff.vkget.connect.SparqlConnector;
import cz.cuni.mff.vkget.data.common.Type;
import cz.cuni.mff.vkget.data.common.Uri;
import cz.cuni.mff.vkget.data.layout.BlockLayout;
import cz.cuni.mff.vkget.data.layout.ColumnLayout;
import cz.cuni.mff.vkget.data.layout.Label;
import cz.cuni.mff.vkget.data.layout.LabelType;
import cz.cuni.mff.vkget.data.layout.LineType;
import cz.cuni.mff.vkget.sparql.Constants;

/**
 * BlockLayout DAO
 * @author Ales Woska
 *
 */
@Repository
public class BlockLayoutDao implements SparqlDao<BlockLayout> {
	private final String FONT_COLOR = Constants.VKGET_Prefix + ":" + "fontColor";
	private final String FONT_SIZE = Constants.VKGET_Prefix + ":" + "fontSize";
	private final String LINE_COLOR = Constants.VKGET_Prefix + ":" + "lineColor";
	private final String LINE_TYPE = Constants.VKGET_Prefix + ":" + "lineType";
	private final String LINE_THICKNESS = Constants.VKGET_Prefix + ":" + "lineThickness";
	private final String LABEL_SOURCE = Constants.VKGET_Prefix + ":" + "labelSource";
	private final String LABEL_TYPE = Constants.VKGET_Prefix + ":" + "labelType";
	private final String LABEL_LANG = Constants.VKGET_Prefix + ":" + "labelLang";
	private final String FOR_TYPE = Constants.VKGET_Prefix + ":" + "forType";
	private final String BACKGROUND = Constants.VKGET_Prefix + ":" + "background";
	private final String HEIGHT = Constants.VKGET_Prefix + ":" + "height";
	private final String WIDTH = Constants.VKGET_Prefix + ":" + "width";
	private final String LEFT = Constants.VKGET_Prefix + ":" + "left";
	private final String TOP = Constants.VKGET_Prefix + ":" + "top";

	private SparqlConnector sparql = SparqlConnector.getLocalFusekiConnector();
	
	@Autowired
	private ColumnLayoutDao columnLayoutDao;
	
	/**
	 * @inheritDoc
	 */
	@Override
	public BlockLayout load(Uri uri) {
		String loadBlockQuery = 
				Constants.PREFIX_PART
				+ "SELECT DISTINCT * WHERE { "
				+ " <" + uri + "> ?property ?value . "
				+ "}";
		ResultSet results = sparql.query(loadBlockQuery);
		BlockLayout layout = new BlockLayout();
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
				case FONT_COLOR: layout.setFontColor(value); break;
				case FONT_SIZE: layout.setFontSize(Integer.valueOf(value)); break;
				case LINE_COLOR: layout.setLineColor(value); break;
				case LINE_TYPE: layout.setLineType(LineType.fromString(value)); break;
				case LINE_THICKNESS: layout.setLineThickness(Integer.valueOf(value)); break;
				case LABEL_SOURCE: layout.getLabel().setLabelSource(value); break;
				case LABEL_LANG: layout.getLabel().setLang(value); break;
				case LABEL_TYPE: layout.getLabel().setType(LabelType.fromString(value)); break;
				case FOR_TYPE: layout.setForType(new Type(value)); break;
				case BACKGROUND: layout.setBackground(value); break;
				case HEIGHT: layout.setHeight(Integer.valueOf(value)); break;
				case WIDTH: layout.setWidth(Integer.valueOf(value)); break;
				case LEFT: layout.setLeft(Integer.valueOf(value)); break;
				case TOP: layout.setTop(Integer.valueOf(value)); break;
			}
		}
		layout.setProperties(this.loadColumnLayouts(uri));
		return layout;
	}
    
    private List<ColumnLayout> loadColumnLayouts(Uri uri) {
    	String loadBlocksForScreen = 
				Constants.PREFIX_PART
				+ "SELECT DISTINCT * WHERE { "
				+ " <" + uri + "> " + Constants.ColumnLayoutProperty + " ?uri . "
				+ "}";
		ResultSet results = sparql.query(loadBlocksForScreen);
		List<ColumnLayout> layouts = new ArrayList<ColumnLayout>();
		while (results.hasNext()) {
			QuerySolution solution = results.next();
			Uri rlUri = new Uri(solution.get("uri").asResource().getURI());
			ColumnLayout bl = columnLayoutDao.load(rlUri);
			layouts.add(bl);
		}
		return layouts;
    }
	
	/**
	 * @inheritDoc
	 */
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
		insertQuery.append(" ").append(LINE_TYPE).append(" \"").append(layout.getLineType().name()).append("\"; ");
		insertQuery.append(" ").append(LABEL_SOURCE).append(" \"").append(layout.getLabel().getLabelSource()).append("\"; ");
		insertQuery.append(" ").append(LABEL_TYPE).append(" \"").append(layout.getLabel().getType()).append("\"; ");
		insertQuery.append(" ").append(LABEL_LANG).append(" \"").append(layout.getLabel().getLang()).append("\"; ");
		insertQuery.append(" ").append(FOR_TYPE).append(" \"").append(layout.getForType()).append("\"; ");
		insertQuery.append(" ").append(BACKGROUND).append(" \"").append(layout.getBackground()).append("\"; ");
		insertQuery.append(" ").append(HEIGHT).append(" \"").append(layout.getHeight()).append("\"; ");
		insertQuery.append(" ").append(WIDTH).append(" \"").append(layout.getWidth()).append("\"; ");
		insertQuery.append(" ").append(LEFT).append(" \"").append(layout.getLeft()).append("\"; ");
		insertQuery.append(" ").append(TOP).append(" \"").append(layout.getTop()).append("\". ");
		
		insertQuery.append(" }");
		
		sparql.insertQuery(insertQuery.toString());
				
		for (ColumnLayout columnLayout: layout.getProperties()) {
			columnLayoutDao.insert(columnLayout);
		}
		
    }
}
