package cz.cuni.mff.vkget.persistence;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

import cz.cuni.mff.vkget.layout.BlockLayout;
import cz.cuni.mff.vkget.layout.LineLayout;
import cz.cuni.mff.vkget.layout.ScreenLayout;
import cz.cuni.mff.vkget.rdf.SparqlConnector;

/**
  * Created by Ales Woska on 31.1.2016.
  */
public class ScreenLayoutDao {
	private static final String NAMESPACE = "http://mff.cuni.cz/vkget#";
	private static final String PREFIX = "vkget";
	private static final String TYPE = PREFIX + ":screenLayout";
	private static final String RDF_TYPE = "rdf:type";
	
	private SparqlConnector sparql = new SparqlConnector("http://localhost:8890/sparql");
	private static final String loadScreenLayoutQuery = 
			"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
			+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
			+ "PREFIX "+PREFIX+": <"+NAMESPACE+"> "
			+ "SELECT DISTINCT * WHERE { "
			+ "  ?layout " + RDF_TYPE + " " + TYPE + " . "
			+ "  ?layout rdfs:title ?name . "
			+ "}";
	
    private final BlockLayoutDao blockDao = new BlockLayoutDao();
    private final LineLayoutDao lineDao = new LineLayoutDao();

    public void insert(ScreenLayout layout) {
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
				"PREFIX vkget: <http://mff.cuni.cz/vkget#> "
				+ "SELECT DISTINCT * WHERE { "
				+ " <" + uri + "> vkget:blockLayout ?uri . "
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
				"PREFIX vkget: <http://mff.cuni.cz/vkget#> "
				+ "SELECT DISTINCT * WHERE { "
				+ " <" + uri + "> vkget:lineLayout ?uri . "
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
