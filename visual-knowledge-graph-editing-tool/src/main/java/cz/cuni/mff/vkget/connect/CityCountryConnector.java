package cz.cuni.mff.vkget.connect;

import java.util.HashMap;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.springframework.stereotype.Service;

import cz.cuni.mff.vkget.data.model.Graph;
import cz.cuni.mff.vkget.data.model.RdfTriple;

@Service
public class CityCountryConnector {
	
	private SparqlConnector connector;
	
	public CityCountryConnector() {
		connector = SparqlConnector.getDbpediaConnector();
	}
	
	private static final String query = 
			"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
			+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
			+ "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> "
			+ "SELECT DISTINCT * WHERE { "
			+ "  ?city rdf:type dbpedia-owl:City . "
			+ "  ?city rdfs:label ?cityLabel . "
			+ "  ?city dbpedia-owl:country ?country . "
			+ "  ?city dbpedia-owl:populationTotal ?populationTotalCity . "
			+ "  ?city dbpedia-owl:postalCode ?postalCode . "
			+ "  ?country dbpedia-owl:currency ?currency . "
			+ "  ?country dbpedia-owl:areaTotal ?areaTotal . "
			+ "  ?country dbpedia-owl:populationTotal ?populationTotalCountry . "
			+ "  ?country rdfs:label ?countryLabel . "
			+ "  FILTER (lang(?cityLabel) = 'en' && lang(?countryLabel) = 'en' && ?populationTotalCity > 1000000) "
			+ "}";
	

	public Graph loadCitiesWithCountries() {
		Graph graph = new Graph();
		
		ResultSet results = this.connector.query(query);
		while (results.hasNext()) {
			QuerySolution solution = results.next();

			String cityUri = solution.get("city").asResource().getURI();
			String cityLabel = solution.get("cityLabel").asLiteral().getLexicalForm();
			Long populationCity = solution.get("populationTotalCity").asLiteral().getLong();
			String postalCode = solution.get("postalCode").asLiteral().getLexicalForm();

			String countryUri = solution.get("country").asResource().getURI();
			Long populationCountry = solution.get("populationTotalCountry").asLiteral().getLong();
			String currency = solution.get("currency").asResource().getURI();
			Float areaTotal = solution.get("areaTotal").asLiteral().getFloat();
			String countryLabel = solution.get("countryLabel").asLiteral().getLexicalForm();
			
			RdfTriple city = new RdfTriple();
			city.setUri(cityUri);
			city.setType("dbpedia-owl:City");
			city.setProperties(new HashMap<String, Object>());
			city.getProperties().put("rdfs:label", cityLabel);
			city.getProperties().put("dbpedia-owl:populationTotal", populationCity);
			city.getProperties().put("dbpedia-owl:postalCode", postalCode);
			graph.addTripleIfNotExists(city);
			
			RdfTriple country = new RdfTriple();
			country.setUri(countryUri);
			country.setType("dbpedia-owl:Country");
			country.setProperties(new HashMap<String, Object>());
			country.getProperties().put("dbpedia-owl:populationTotal", populationCountry);
			country.getProperties().put("dbpedia-owl:currency", currency);
			country.getProperties().put("dbpedia-owl:areaTotal", areaTotal);
			country.getProperties().put("rdfs:label", countryLabel);
			graph.addTripleIfNotExists(country);
		}
		return graph;
	}
	
}
