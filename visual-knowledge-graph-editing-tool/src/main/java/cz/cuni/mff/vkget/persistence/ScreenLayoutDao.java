package cz.cuni.mff.vkget.persistence;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cz.cuni.mff.vkget.connect.SparqlConnector;
import cz.cuni.mff.vkget.data.common.Property;
import cz.cuni.mff.vkget.data.common.Uri;
import cz.cuni.mff.vkget.data.layout.BlockLayout;
import cz.cuni.mff.vkget.data.layout.LineLayout;
import cz.cuni.mff.vkget.data.layout.ScreenLayout;
import cz.cuni.mff.vkget.sparql.Constants;

/**
 * ScreenLayout dao object
 * @author Ales Woska
 *
 */
@Repository
public class ScreenLayoutDao extends AbstractDao<ScreenLayout> {
	private static final Property NAMESPACES = new Property(Constants.VKGET_Prefix, "namespaces");
	
	private SparqlConnector sparql = SparqlConnector.getLocalFusekiConnector();
	
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
	public void insert(ScreenLayout layout) {

		StringBuilder insertQuery = new StringBuilder(Constants.PREFIX_PART);
		insertQuery.append("INSERT DATA { <").append(layout.getUri()).append("> ");
		
		insertQuery.append(" ").append(Constants.RDF_TYPE).append(" ").append(Constants.ScreenLayoutType).append("; ");
		insertQuery.append(" ").append(Constants.RDFS_LABEL).append(" '").append(layout.getName()).append("'; ");
		insertQuery.append(" ").append(NAMESPACES).append(" '").append(layout.getNamespacesAsString()).append("'. ");
		
		for (BlockLayout bl: layout.getBlockLayouts()) {
			insertQuery.append(" <").append(layout.getUri()).append("> ").append(Constants.BlockLayoutProperty).append(" <").append(bl.getUri()).append("> . ");
		}
		for (LineLayout ll: layout.getLineLayouts()) {
			insertQuery.append(" <").append(layout.getUri()).append("> ").append(Constants.LineLayoutProperty).append(" <").append(ll.getUri()).append("> . ");
		}
		
		insertQuery.append(" }");
    	
    	sparql.executeQuery(insertQuery.toString());
    	
        for (BlockLayout block: layout.getBlockLayouts()) {
            blockDao.insertOrUpdate(block);
        }
        for (LineLayout line: layout.getLineLayouts()) {
            lineDao.insertOrUpdate(line);
        }
    }
	
	/**
	 * @inheritDoc
	 */
	@Override
	public void insertOrUpdate(ScreenLayout layout) {
		if (exists(layout)) {
			update(layout);
		} else {
			insert(layout);
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
		updateQuery.append(NAMESPACES).append(" '").append(layout.getNamespacesAsString()).append("' . ");
		
		for (BlockLayout bl: layout.getBlockLayouts()) {
			updateQuery.append(" <").append(layout.getUri().getUri()).append("> ").append(Constants.BlockLayoutProperty).append(" <").append(bl.getUri().getUri()).append("> . ");
		}
		for (LineLayout ll: layout.getLineLayouts()) {
			updateQuery.append(" <").append(layout.getUri().getUri()).append("> ").append(Constants.LineLayoutProperty).append(" <").append(ll.getUri().getUri()).append("> . ");
		}
		
		updateQuery.append(" } WHERE { <").append(layout.getUri().getUri()).append("> ?p ?o . }");
    	
    	sparql.executeQuery(updateQuery.toString());
    	
        for (BlockLayout block: layout.getBlockLayouts()) {
            blockDao.insertOrUpdate(block);
        }
        for (LineLayout line: layout.getLineLayouts()) {
            lineDao.insertOrUpdate(line);
        }
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
    	
    	for (BlockLayout block: layout.getBlockLayouts()) {
    		blockDao.delete(block);
    	}
    	for (LineLayout line: layout.getLineLayouts()) {
    		lineDao.delete(line);
    	}
    }
    
    /**
     * @inheritDoc
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
		for (ScreenLayout layout: layouts) {
			layout.setBlockLayouts(this.loadBlockLayouts(layout.getUri()));
			layout.setLineLayouts(this.loadLineLayouts(layout.getUri()));
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
			}
		}
		layout.setBlockLayouts(this.loadBlockLayouts(layout.getUri()));
		layout.setLineLayouts(this.loadLineLayouts(layout.getUri()));
		fillBlocksToLines(layout);
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
    
    private void fillBlocksToLines(ScreenLayout layout) {
    	for (LineLayout lineLayout: layout.getLineLayouts()) {
    		for (BlockLayout blockLayout: layout.getBlockLayouts()) {
    			if (lineLayout.getFromType().getUri().equals(blockLayout.getUri())) {
    				lineLayout.setFromType(blockLayout);
    			}
    			if (lineLayout.getToType().getUri().equals(blockLayout.getUri())) {
    				lineLayout.setToType(blockLayout);
    			}
    		}
    	}
    }
    
}
