package cz.cuni.mff.vkget.data.model;

import java.io.Serializable;
import java.util.List;

/**
 * RdfTable hold collection of instances with same type.
 * @author Ales Woska
 *
 */
public class RdfTable implements Serializable {
	
	/**
	 * rdf:type for the table
	 */
	private String typeUri;
	
	/**
	 * table instances - rows
	 */
	private List<RdfInstance> instances;
	
	/**
	 * URIs of table columns
	 */
	private List<String> columnsURIs;

	public String getTypeUri() {
		return typeUri;
	}

	public void setTypeUri(String classUri) {
		this.typeUri = classUri;
	}

	public List<RdfInstance> getInstances() {
		return instances;
	}

	public void setInstances(List<RdfInstance> instances) {
		this.instances = instances;
	}

	public List<String> getColumnsURIs() {
		return columnsURIs;
	}

	public void setColumnsURIs(List<String> columnsURIs) {
		this.columnsURIs = columnsURIs;
	}

}
