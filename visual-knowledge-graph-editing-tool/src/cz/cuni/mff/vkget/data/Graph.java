package cz.cuni.mff.vkget.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Graph {
	private List<RdfTriple> triples = new ArrayList<RdfTriple>();

	public List<RdfTriple> getTriples() {
		return triples;
	}

	public void setTriples(List<RdfTriple> triples) {
		this.triples = triples;
	}
	
	public void addTripleIfNotExists(RdfTriple triple) {
		if (!containsTriple(triple)) {
			addTriple(triple);
		}
	}
	
	public void addTriple(RdfTriple triple) {
		triples.add(triple);
	}
	
	public boolean containsTriple(RdfTriple triple) {
		for (RdfTriple t: triples) {
			if (triple.getUri().equals(t.getUri()) && triple.getType().equals(t.getType())) {
				return true;
			}
		}
		return false;
	}
	
	public List<String> getTypes() {
		Set<String> types = new HashSet<String>();
		for (RdfTriple triple: triples) {
			types.add(triple.getType());
		}
		return new ArrayList<String>(types);
	}
	
	public List<String> getAllClassProperties(String type) {
		Set<String> properties = new HashSet<String>();
		for (RdfTriple triple: triples) {
			if (triple.getType().equals(type)) {
				properties.addAll(triple.getProperties().keySet());
			}
		}
		return new ArrayList<String>(properties);
	}
	
	public List<RdfTriple> getRdfTriplesByType(String type) {
		List<RdfTriple> triples = new ArrayList<>();
		for (RdfTriple triple: this.triples) {
			if (triple.getType().equals(type)) {
				triples.add(triple);
			}
		}
		return triples;
	}
	
	
}
