package cz.cuni.mff.vkget.persistence;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cz.cuni.mff.vkget.connect.SparqlConnector;
import cz.cuni.mff.vkget.layout.BlockLayout;
import cz.cuni.mff.vkget.layout.LineLayout;
import cz.cuni.mff.vkget.layout.ScreenLayout;
import cz.cuni.mff.vkget.sparql.Constants;

/**
  * Created by Ales Woska on 31.1.2016.
  */
@Repository
public class ScreenLayoutDao {
	private SparqlConnector sparql = SparqlConnector.getLocalFusekiConnector();
	
	private static final String loadScreenLayoutQuery = 
			Constants.PREFIX_PART
			+ "SELECT DISTINCT * WHERE { "
			+ "  ?layout " + Constants.RDF_TYPE + " " + Constants.ScreenLayoutType + " . "
			+ "  ?layout " + Constants.RDFS_TITLE + " ?name . "
			+ "}";
	
	@Autowired
    private BlockLayoutDao blockDao;
	
	@Autowired
    private LineLayoutDao lineDao;

    public void insert(ScreenLayout layout) {

		StringBuilder insertQuery = new StringBuilder(Constants.PREFIX_PART);
		insertQuery.append("INSERT DATA { <").append(layout.getUri()).append("> ");
		
		insertQuery.append(" ").append(Constants.RDF_TYPE).append(" ").append(Constants.ScreenLayoutType).append("; ");
		insertQuery.append(" ").append(Constants.RDFS_TITLE).append(" \"").append(layout.getName()).append("\"; ");
		
		for (BlockLayout bl: layout.getBlockLayouts()) {
			insertQuery.append(" ").append(Constants.BlockLayoutProperty).append(" <").append(bl.getUri()).append("> ; ");
		}
		for (LineLayout ll: layout.getLineLayouts()) {
			insertQuery.append(" ").append(Constants.LineLayoutProperty).append(" <").append(ll.getUri()).append("> ; ");
		}
		
		insertQuery.append(" }");
    	
    	sparql.insertQuery(insertQuery.toString());
    	
        for (BlockLayout block: layout.getBlockLayouts()) {
            blockDao.insert(block);
        }
        for (LineLayout line: layout.getLineLayouts()) {
            lineDao.insert(line);
        }
    }
    
    public List<ScreenLayout> loadScreenLayouts() {
    	List<ScreenLayout> layouts = new ArrayList<ScreenLayout>();
    	ResultSet results = sparql.query(loadScreenLayoutQuery);
    	
		while (results.hasNext()) {
			QuerySolution solution = results.next();

			ScreenLayout layout = new ScreenLayout();
			String uri = solution.get("layout").asResource().getURI();
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
    
    public List<BlockLayout> loadBlockLayouts(String uri) {
    	String loadBlocksForScreen = 
				Constants.PREFIX_PART
				+ "SELECT DISTINCT * WHERE { "
				+ " <" + uri + "> " + Constants.BlockLayoutProperty + " ?uri . "
				+ "}";
		ResultSet results = sparql.query(loadBlocksForScreen);
		List<BlockLayout> layouts = new ArrayList<BlockLayout>();
		while (results.hasNext()) {
			QuerySolution solution = results.next();
			String blUri = solution.get("uri").asResource().getURI();
			BlockLayout bl = blockDao.load(blUri);
			layouts.add(bl);
		}
		return layouts;
    }
    
    public List<LineLayout> loadLineLayouts(String uri) {
    	String loadBlocksForScreen = 
				Constants.PREFIX_PART
				+ "SELECT DISTINCT * WHERE { "
				+ " <" + uri + "> " + Constants.LineLayoutProperty + " ?uri . "
				+ "}";
		ResultSet results = sparql.query(loadBlocksForScreen);
		List<LineLayout> layouts = new ArrayList<LineLayout>();
		while (results.hasNext()) {
			QuerySolution solution = results.next();
			String lluri = solution.get("uri").asResource().getURI();
			LineLayout ll = lineDao.load(lluri);
			layouts.add(ll);
		}
		return layouts;
    }
    
    
}
