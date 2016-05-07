package cz.cuni.mff.vkget.connect;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.springframework.stereotype.Service;

import cz.cuni.mff.vkget.data.layout.BlockLayout;
import cz.cuni.mff.vkget.data.layout.LineLayout;
import cz.cuni.mff.vkget.data.layout.RowLayout;
import cz.cuni.mff.vkget.data.layout.ScreenLayout;
import cz.cuni.mff.vkget.data.model.Graph;
import cz.cuni.mff.vkget.data.model.RdfTriple;

@Service
public class CommonDataConnector implements DataConnector {
	
	private SparqlConnector connector;
	
	public CommonDataConnector() {}
	
	public CommonDataConnector(String endpoint) {
		connector = new SparqlConnector(endpoint);
	}

	@Override
	public Graph loadGraph(ScreenLayout screenLayout) {
		Graph graph = new Graph();
		
		String query = this.constructQuery(screenLayout);
		ResultSet results = this.connector.query(query);
		while (results.hasNext()) {
			QuerySolution solution = results.next();

			Map<String, String> varMap = new LinkedHashMap<String, String>();
			int i = 0;
			for (BlockLayout blockLayout: screenLayout.getBlockLayouts()) {
				String var = "x" + i;
				i++;
				varMap.put(blockLayout.getForType(), var);
			}
			
			for (BlockLayout blockLayout: screenLayout.getBlockLayouts()) {
				String var = varMap.get(blockLayout.getForType());

				RdfTriple rdfTriple = new RdfTriple();
				rdfTriple.setUri(solution.get(var).asResource().getURI());
				rdfTriple.setType(blockLayout.getForType());
				rdfTriple.setProperties(new HashMap<String, Object>());
				
				int j = 0;
				for (RowLayout rowLayout: blockLayout.getProperties()) {
					String property = var + "y" + j;
					j++;
					
					RDFNode rdfNode = solution.get(property);
					Object value = null;
					if (rdfNode.isResource()) {
						value = rdfNode.asResource().getURI();
					} else if (rdfNode.isLiteral()) {
						value = rdfNode.asLiteral().getValue();
					}
					
					if (value instanceof Number) {
						value = (Number) value;
					}

					rdfTriple.getProperties().put(rowLayout.getProperty(), value);
				}
				
				for (LineLayout lineLayout: screenLayout.getLineLayouts()) {
					if (lineLayout.getFromType().equals(blockLayout.getForType())) {
						String property = varMap.get(lineLayout.getToType());
						
						RDFNode rdfNode = solution.get(property);
						String uri = rdfNode.asResource().getURI();
						RdfTriple value = new RdfTriple();
						value.setUri(uri);

						rdfTriple.getProperties().put(lineLayout.getProperty(), value);
						
					} else {
						continue;
					}
				}
				
				graph.addTripleIfNotExists(rdfTriple);
			}

		}
		return graph;
	}
	
	private String constructQuery(ScreenLayout screenLayout) {
		StringBuilder sb = new StringBuilder();
		for (String prefix: screenLayout.getNamespaces().keySet()) {
			String namespace = screenLayout.getNamespaces().get(prefix);
			sb.append("PREFIX ").append(prefix).append(": <").append(namespace).append("> ");
		}
		sb.append("SELECT DISTINCT * WHERE { ");
		
		Map<String, String> varMap = new LinkedHashMap<String, String>();
		
		int i = 0;
		for (BlockLayout blockLayout: screenLayout.getBlockLayouts()) {
			String var = "x" + i;
			i++;
			varMap.put(blockLayout.getForType(), var);
			sb.append(" ?").append(var).append(" rdf:type ").append(blockLayout.getForType()).append(" . ");
			int j = 0;
			for (RowLayout rowLayout: blockLayout.getProperties()) {
				String property = var + "y" + j;
				j++;
				sb.append(" ?").append(var).append(" ").append(rowLayout.getProperty()).append(" ?").append(property).append(" . ");
			}
		}
		for (LineLayout lineLayout: screenLayout.getLineLayouts()) {
			String varFrom = varMap.get(lineLayout.getFromType());
			String varTo = varMap.get(lineLayout.getToType());
			sb.append(" ?").append(varFrom).append(" ").append(lineLayout.getProperty()).append(" ?").append(varTo).append(" . ");
		}
		
		sb.append("} ");
		return sb.toString();
	}
	
}
