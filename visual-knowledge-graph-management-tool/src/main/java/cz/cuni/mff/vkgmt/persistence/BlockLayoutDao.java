package cz.cuni.mff.vkgmt.persistence;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import cz.cuni.mff.vkgmt.connect.SparqlConnector;
import cz.cuni.mff.vkgmt.data.common.Property;
import cz.cuni.mff.vkgmt.data.common.RdfEntity;
import cz.cuni.mff.vkgmt.data.common.Type;
import cz.cuni.mff.vkgmt.data.common.Uri;
import cz.cuni.mff.vkgmt.data.layout.BlockLayout;
import cz.cuni.mff.vkgmt.data.layout.ColumnLayout;
import cz.cuni.mff.vkgmt.data.layout.Label;
import cz.cuni.mff.vkgmt.data.layout.LabelType;
import cz.cuni.mff.vkgmt.data.layout.LineType;
import cz.cuni.mff.vkgmt.sparql.Constants;

/**
 * BlockLayout DAO
 * @author Ales Woska
 *
 */
@Repository
public class BlockLayoutDao extends AbstractDao<BlockLayout> {
	private final Property FONT_COLOR = new Property(Constants.VKGMT_Prefix, "fontColor");
	private final Property FONT_SIZE = new Property(Constants.VKGMT_Prefix, "fontSize");
	private final Property LINE_COLOR = new Property(Constants.VKGMT_Prefix, "lineColor");
	private final Property LINE_TYPE = new Property(Constants.VKGMT_Prefix, "lineType");
	private final Property LINE_THICKNESS = new Property(Constants.VKGMT_Prefix, "lineThickness");
	private final Property LABEL_SOURCE = new Property(Constants.VKGMT_Prefix, "labelSource");
	private final Property LABEL_TYPE = new Property(Constants.VKGMT_Prefix, "labelType");
	private final Property LABEL_LANG = new Property(Constants.VKGMT_Prefix, "labelLang");
	private final Property FOR_TYPE = new Property(Constants.VKGMT_Prefix, "forType");
	private final Property BACKGROUND = new Property(Constants.VKGMT_Prefix, "background");
	private final Property HEIGHT = new Property(Constants.VKGMT_Prefix, "height");
	private final Property WIDTH = new Property(Constants.VKGMT_Prefix, "width");
	private final Property LEFT = new Property(Constants.VKGMT_Prefix, "left");
	private final Property TOP = new Property(Constants.VKGMT_Prefix, "top");

	@Autowired
	@Qualifier("settingsConnector")
	private SparqlConnector sparql;
	
	@Override
	protected SparqlConnector getSparqlConnector() {
		return sparql;
	}

	@Autowired
    private ColumnLayoutDao columnLayoutDao;
	
	/**
	 * @inheritDoc
	 */
	@Override
	public BlockLayout get(Uri uri) {
		String loadBlockQuery = 
				Constants.PREFIX_PART
				+ "SELECT DISTINCT * WHERE { "
				+ " <" + uri.getUri() + "> ?property ?value . "
				+ "}";
		ResultSet results = sparql.query(loadBlockQuery);
		BlockLayout layout = new BlockLayout();
		layout.setUri(uri);
		layout.setLabel(new Label());
		while (results.hasNext()) {
			QuerySolution solution = results.next();
			
			Resource resource = solution.get("property").asResource();
			Property property = new Property(Constants.VKGMT_Prefix, resource.getLocalName());
			
			RDFNode node = solution.get("value");
			String value = (node.isLiteral()) ? node.asLiteral().getString() : node.asResource().getURI();
			if (value == "null") {
				continue;
			}

			if (property.equals(FONT_COLOR)) {
				layout.setFontColor(value);
			} else if (property.equals(FONT_SIZE)) {
				layout.setFontSize(Integer.valueOf(value));
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
			} else if (property.equals(FOR_TYPE)) {
				layout.setForType(new Type(value));
			} else if (property.equals(BACKGROUND)) {
				layout.setBackground(value);
			} else if (property.equals(HEIGHT)) {
				layout.setHeight(Integer.valueOf(value));
			} else if (property.equals(WIDTH)) {
				layout.setWidth(Integer.valueOf(value));
			} else if (property.equals(LEFT)) {
				layout.setLeft(Integer.valueOf(value));
			} else if (property.equals(TOP)) {
				layout.setTop(Integer.valueOf(value));
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
			ColumnLayout bl = columnLayoutDao.get(rlUri);
			layouts.add(bl);
		}
		return layouts;
    }
	
	/**
	 * @inheritDoc
	 */
    @Override
	public void insert(BlockLayout layout, RdfEntity parent) {
    	String rawUri = parent.getUri().getUri() + "_" + escepeUri(layout.getForType().getType());
    	layout.setUri(getUniqueId(rawUri));
    	
		StringBuilder insertQuery = new StringBuilder(Constants.PREFIX_PART);
		insertQuery.append("INSERT DATA { ");
		insertQuery.append("<").append(layout.getUri().getUri()).append("> ");
		
		insertQuery.append(" ").append(Constants.RDF_TYPE).append(" ").append(Constants.BlockLayoutType).append("; ");
		insertQuery.append(" ").append(FONT_COLOR).append(" '").append(layout.getFontColor()).append("'; ");
		insertQuery.append(" ").append(FONT_SIZE).append(" '").append(layout.getFontSize()).append("'; ");
		insertQuery.append(" ").append(LINE_COLOR).append(" '").append(layout.getLineColor()).append("'; ");
		insertQuery.append(" ").append(LINE_THICKNESS).append(" '").append(layout.getLineThickness()).append("'; ");
		insertQuery.append(" ").append(LINE_TYPE).append(" '").append(layout.getLineType().name()).append("'; ");
		insertQuery.append(" ").append(LABEL_SOURCE).append(" '").append(layout.getLabel().getLabelSource()).append("'; ");
		insertQuery.append(" ").append(LABEL_TYPE).append(" '").append(layout.getLabel().getType()).append("'; ");
		insertQuery.append(" ").append(LABEL_LANG).append(" '").append(layout.getLabel().getLang()).append("'; ");
		insertQuery.append(" ").append(FOR_TYPE).append(" '").append(layout.getForType()).append("'; ");
		insertQuery.append(" ").append(BACKGROUND).append(" '").append(layout.getBackground()).append("'; ");
		insertQuery.append(" ").append(HEIGHT).append(" '").append(layout.getHeight()).append("'; ");
		insertQuery.append(" ").append(WIDTH).append(" '").append(layout.getWidth()).append("'; ");
		insertQuery.append(" ").append(LEFT).append(" '").append(layout.getLeft()).append("'; ");
		insertQuery.append(" ").append(TOP).append(" '").append(layout.getTop()).append("'. ");
		
		for (ColumnLayout columnLayout: layout.getProperties()) {
			columnLayoutDao.insertOrUpdate(columnLayout, layout);
		}

		for (ColumnLayout cl: layout.getProperties()) {
			insertQuery.append(" <").append(layout.getUri().getUri()).append("> ").append(Constants.ColumnLayoutProperty).append(" <").append(cl.getUri().getUri()).append("> . ");
		}
		
		insertQuery.append(" }");
		
		sparql.executeQuery(insertQuery.toString());
		
    }
    
    /**
     * @inheritDoc
     */
    @Override
    public void update(BlockLayout layout) {
    	StringBuilder updateQuery = new StringBuilder(Constants.PREFIX_PART);
		updateQuery.append("DELETE { <").append(layout.getUri().getUri()).append("> ?p ?o } ");
		updateQuery.append("INSERT { <").append(layout.getUri().getUri()).append("> ");

		updateQuery.append(" ").append(Constants.RDF_TYPE).append(" ").append(Constants.BlockLayoutType).append("; ");
		updateQuery.append(" ").append(FONT_COLOR).append(" '").append(layout.getFontColor()).append("'; ");
		updateQuery.append(" ").append(FONT_SIZE).append(" '").append(layout.getFontSize()).append("'; ");
		updateQuery.append(" ").append(LINE_COLOR).append(" '").append(layout.getLineColor()).append("'; ");
		updateQuery.append(" ").append(LINE_THICKNESS).append(" '").append(layout.getLineThickness()).append("'; ");
		updateQuery.append(" ").append(LINE_TYPE).append(" '").append(layout.getLineType().name()).append("'; ");
		updateQuery.append(" ").append(LABEL_SOURCE).append(" '").append(layout.getLabel().getLabelSource()).append("'; ");
		updateQuery.append(" ").append(LABEL_TYPE).append(" '").append(layout.getLabel().getType()).append("'; ");
		updateQuery.append(" ").append(LABEL_LANG).append(" '").append(layout.getLabel().getLang()).append("'; ");
		updateQuery.append(" ").append(FOR_TYPE).append(" '").append(layout.getForType()).append("'; ");
		updateQuery.append(" ").append(BACKGROUND).append(" '").append(layout.getBackground()).append("'; ");
		updateQuery.append(" ").append(HEIGHT).append(" '").append(layout.getHeight()).append("'; ");
		updateQuery.append(" ").append(WIDTH).append(" '").append(layout.getWidth()).append("'; ");
		updateQuery.append(" ").append(LEFT).append(" '").append(layout.getLeft()).append("'; ");
		updateQuery.append(" ").append(TOP).append(" '").append(layout.getTop()).append("'. ");
		
		for (ColumnLayout columnLayout: layout.getProperties()) {
			columnLayoutDao.insertOrUpdate(columnLayout, layout);
		}

		for (ColumnLayout cl: layout.getProperties()) {
			updateQuery.append(" <").append(layout.getUri().getUri()).append("> ").append(Constants.ColumnLayoutProperty).append(" <").append(cl.getUri().getUri()).append("> . ");
		}
		
		updateQuery.append(" } WHERE { <").append(layout.getUri().getUri()).append("> ?p ?o . }");
		
		sparql.executeQuery(updateQuery.toString());
    }
	
	/**
	 * @inheritDoc
	 */
	@Override
	public void insertOrUpdate(BlockLayout layout, RdfEntity parent) {
		if (exists(layout)) {
			update(layout);
		} else {
			insert(layout, parent);
		}
	}

	@Override
	public List<BlockLayout> getAll() {
		// TODO Auto-generated method stub
		return null;
	}
}
