package cz.cuni.mff.vkgmt.persistence;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

import cz.cuni.mff.vkgmt.connect.SparqlConnector;
import cz.cuni.mff.vkgmt.data.common.Property;
import cz.cuni.mff.vkgmt.data.common.RdfEntity;
import cz.cuni.mff.vkgmt.data.common.Uri;
import cz.cuni.mff.vkgmt.data.layout.BlockLayout;
import cz.cuni.mff.vkgmt.data.layout.LineLayout;
import cz.cuni.mff.vkgmt.data.layout.PropagationType;
import cz.cuni.mff.vkgmt.data.layout.ScreenLayout;
import cz.cuni.mff.vkgmt.sparql.Constants;

/**
 * ScreenLayout dao object
 * @author Ales Woska
 *
 */
@Repository
public class ScreenLayoutDao extends AbstractDao<ScreenLayout> {
	private static final Property NAMESPACES = new Property(Constants.VKGMT_Prefix, "namespaces");
	private static final Property FILTER_PROPAGATION = new Property(Constants.VKGMT_Prefix, "filterPropagation");
	private static final Property HEIGHT = new Property(Constants.VKGMT_Prefix, "height");
	private static final Property WIDTH = new Property(Constants.VKGMT_Prefix, "width");

	@Autowired
	@Qualifier("settingsConnector")
	private SparqlConnector sparql;
	
	@Override
	protected SparqlConnector getSparqlConnector() {
		return sparql;
	}
	
	private static final String loadScreenLayoutQuery = 
			Constants.PREFIX_PART
			+ "SELECT DISTINCT * WHERE { "
			+ "  ?layout " + Constants.RDF_TYPE + " " + Constants.ScreenLayoutType + " . "
			+ "  ?layout " + Constants.RDFS_LABEL + " ?name . "
			+ "}";
	
	@Autowired
    private BlockLayoutDao blockDao;
	
	@Autowired
    private LineLayoutDao lineDao;
	
	@Override
	public boolean exists(ScreenLayout layout) {
		StringBuilder existsQuery = new StringBuilder(Constants.PREFIX_PART);
		existsQuery.append("SELECT DISTINCT (count(?type) as ?typeCount)  WHERE { <");
		existsQuery.append(layout.getUri().getUri());
		existsQuery.append("> rdf:type ?type . } GROUP BY ?type");
		ResultSet results = sparql.query(existsQuery.toString());
		while (results.hasNext()) {
			QuerySolution solution = results.next();
			int resultCount = solution.getLiteral("typeCount").asLiteral().getInt();
			if (resultCount == 0) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	/**
	 * @inheritDoc
	 */
    @Override
	public void insert(ScreenLayout layout, RdfEntity parent) {
    	String rawUri = Constants.VKGMT_Namespace + escepeUri(layout.getName());
    	layout.setUri(getUniqueId(rawUri));
		StringBuilder insertQuery = new StringBuilder(Constants.PREFIX_PART);
		insertQuery.append("INSERT DATA { <").append(layout.getUri()).append(">\n");
		
		insertQuery.append(" ").append(Constants.RDF_TYPE).append(" ").append(Constants.ScreenLayoutType).append(";\n");
		insertQuery.append(" ").append(Constants.RDFS_LABEL).append(" '").append(layout.getName()).append("';\n");
		insertQuery.append(" ").append(NAMESPACES).append(" '").append(layout.getNamespacesAsString()).append("';\n");
		insertQuery.append(" ").append(FILTER_PROPAGATION).append(" '").append(layout.getFilterPropagation()).append("';\n");
		insertQuery.append(" ").append(HEIGHT).append(" '").append(layout.getHeight()).append("';\n");
		insertQuery.append(" ").append(WIDTH).append(" '").append(layout.getWidth()).append("'.\n");
    	
        for (BlockLayout block: layout.getBlockLayouts()) {
            blockDao.insertOrUpdate(block, layout);
        }
        
        for (LineLayout line: layout.getLineLayouts()) {
            lineDao.insertOrUpdate(line, layout);
        }
		
		for (BlockLayout bl: layout.getBlockLayouts()) {
			insertQuery.append(" <").append(layout.getUri()).append("> ").append(Constants.BlockLayoutProperty).append(" <").append(bl.getUri()).append("> . ");
		}
		for (LineLayout ll: layout.getLineLayouts()) {
			insertQuery.append(" <").append(layout.getUri()).append("> ").append(Constants.LineLayoutProperty).append(" <").append(ll.getUri()).append("> . ");
		}
		
		insertQuery.append("\n}");
    	
    	sparql.executeQuery(insertQuery.toString());
    }
	
	/**
	 * @inheritDoc
	 */
	@Override
	public void insertOrUpdate(ScreenLayout layout, RdfEntity parent) {
		if (exists(layout)) {
			update(layout);
		} else {
			insert(layout, null);
		}
	}

	/**
	 * @inheritDoc
	 */
    @Override
	public void update(ScreenLayout layout) {
		StringBuilder updateQuery = new StringBuilder(Constants.PREFIX_PART);
		updateQuery.append("DELETE { <").append(layout.getUri().getUri()).append("> ?p ?o } ");
		updateQuery.append("INSERT { <").append(layout.getUri().getUri()).append("> ");
		
		updateQuery.append(" ").append(Constants.RDF_TYPE).append(" ").append(Constants.ScreenLayoutType).append(" ; ");
		updateQuery.append(" ").append(Constants.RDFS_LABEL).append(" '").append(layout.getName()).append("' ; ");
		updateQuery.append(NAMESPACES).append(" '").append(layout.getNamespacesAsString()).append("' ; ");
		updateQuery.append(FILTER_PROPAGATION).append(" '").append(layout.getFilterPropagation().name()).append("' ; ");
		updateQuery.append(WIDTH).append(" '").append(layout.getWidth()).append("' ; ");
		updateQuery.append(HEIGHT).append(" '").append(layout.getHeight()).append("' . ");
    	
        for (BlockLayout block: layout.getBlockLayouts()) {
            blockDao.insertOrUpdate(block, layout);
        }
        for (LineLayout line: layout.getLineLayouts()) {
            lineDao.insertOrUpdate(line, layout);
        }
		
		for (BlockLayout bl: layout.getBlockLayouts()) {
			updateQuery.append(" <").append(layout.getUri().getUri()).append("> ").append(Constants.BlockLayoutProperty).append(" <").append(bl.getUri().getUri()).append("> . ");
		}
		for (LineLayout ll: layout.getLineLayouts()) {
			updateQuery.append(" <").append(layout.getUri().getUri()).append("> ").append(Constants.LineLayoutProperty).append(" <").append(ll.getUri().getUri()).append("> . ");
		}
		
		updateQuery.append(" } WHERE { <").append(layout.getUri().getUri()).append("> ?p ?o . }");
    	
    	sparql.executeQuery(updateQuery.toString());
    }
    
    /**
     * @inheritDoc
     */
    @Override
    public void delete(ScreenLayout layout) {
    	StringBuilder deleteQuery = new StringBuilder(Constants.PREFIX_PART);
    	deleteQuery.append("DELETE { <").append(layout.getUri()).append("> ?p ?o");
    	deleteQuery.append(" } WHERE { <").append(layout.getUri()).append("> ?p ?o . }");
    	
    	sparql.executeQuery(deleteQuery.toString());

    	if (layout.getBlockLayouts() != null) {
	    	for (BlockLayout block: layout.getBlockLayouts()) {
	    		blockDao.delete(block);
	    	}
    	}
    	
    	if (layout.getLineLayouts() != null) {
	    	for (LineLayout line: layout.getLineLayouts()) {
	    		lineDao.delete(line);
	    	}
    	}
    }
    
    /**
     * WARNING no inner sublayouts are loaded!!!
     */
    @Override
    public List<ScreenLayout> getAll() {
    	List<ScreenLayout> layouts = new ArrayList<ScreenLayout>();
    	ResultSet results = sparql.query(loadScreenLayoutQuery);
    	
		while (results.hasNext()) {
			QuerySolution solution = results.next();

			ScreenLayout layout = new ScreenLayout();
			Uri uri = new Uri(solution.get("layout").asResource().getURI());
			layout.setUri(uri);
			layout.setName(solution.get("name").asLiteral().getLexicalForm());
			
			layouts.add(layout);
			
		}
		return layouts;
    }

    /**
     * @inheritDoc
     */
	@Override
	public ScreenLayout get(Uri uri) {
		String loadBlockQuery = 
				Constants.PREFIX_PART
				+ "SELECT DISTINCT * WHERE { "
				+ " <" + uri + "> ?property ?value . "
				+ "}";
		ResultSet results = sparql.query(loadBlockQuery);
		ScreenLayout layout = new ScreenLayout();
		layout.setUri(uri);
		while (results.hasNext()) {
			QuerySolution solution = results.next();
			
			RDFNode node = solution.get("property");
			Model model = node.getModel();
			model.setNsPrefixes(Constants.nsPrefixMapping);
			Resource resource = node.asResource();
			String namespace = resource.getNameSpace();
			String nsPrefix = model.getNsURIPrefix(namespace);
			Property property = new Property(nsPrefix, resource.getLocalName());
			
			node = solution.get("value");
			String value = (node.isLiteral()) ? node.asLiteral().getString() : node.asResource().getURI();
			if (value == "null") {
				continue;
			}

			if (property.equals(Constants.RDFS_LABEL)) {
				layout.setName(value);
			} else if (property.equals(NAMESPACES)) {
				layout.setNamespacesFromString(value);
			} else if (property.equals(FILTER_PROPAGATION)) {
				if (value != null && !value.equals("null")) {
					layout.setFilterPropagation(PropagationType.valueOf(value));
				}
			} else if (property.equals(WIDTH)) {
				layout.setWidth(Integer.valueOf(value));
			} else if (property.equals(HEIGHT)) {
				layout.setHeight(Integer.valueOf(value));
			}
		}
		layout.setBlockLayouts(this.loadBlockLayouts(layout.getUri()));
		layout.setLineLayouts(this.loadLineLayouts(layout.getUri()));
		return layout;
	}
    
    private List<BlockLayout> loadBlockLayouts(Uri blockLayoutUri) {
    	String loadBlocksForScreen = 
				Constants.PREFIX_PART
				+ "SELECT DISTINCT * WHERE { "
				+ " <" + blockLayoutUri.getUri() + "> " + Constants.BlockLayoutProperty + " ?uri . "
				+ "}";
		ResultSet results = sparql.query(loadBlocksForScreen);
		List<BlockLayout> layouts = new ArrayList<BlockLayout>();
		while (results.hasNext()) {
			QuerySolution solution = results.next();
			Uri blUri = new Uri(solution.get("uri").asResource().getURI());
			BlockLayout bl = blockDao.get(blUri);
			layouts.add(bl);
		}
		return layouts;
    }
    
    private List<LineLayout> loadLineLayouts(Uri lineLayoutUri) {
    	String loadBlocksForScreen = 
				Constants.PREFIX_PART
				+ "SELECT DISTINCT * WHERE { "
				+ " <" + lineLayoutUri.getUri() + "> " + Constants.LineLayoutProperty + " ?uri . "
				+ "}";
		ResultSet results = sparql.query(loadBlocksForScreen);
		List<LineLayout> layouts = new ArrayList<LineLayout>();
		while (results.hasNext()) {
			QuerySolution solution = results.next();
			Uri lluri = new Uri(solution.get("uri").asResource().getURI());
			LineLayout ll = lineDao.get(lluri);
			layouts.add(ll);
		}
		return layouts;
    }
    
}
