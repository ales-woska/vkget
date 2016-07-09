package cz.cuni.mff.vkget.data.model;

import java.io.Serializable;
import java.util.List;

import cz.cuni.mff.vkget.data.common.Property;
import cz.cuni.mff.vkget.data.common.Type;

/**
 * RdfTable hold collection of instances with same type.
 * @author Ales Woska
 *
 */
public class RdfTable implements Serializable {
	
	/**
	 * rdf:type for the table
	 */
	private Type type;
	
	private String label;
	
	/**
	 * table instances - rows
	 */
	private List<RdfInstance> instances;
	
	/**
	 * URIs of table columns
	 */
	private List<Property> columns;
	
	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public List<RdfInstance> getInstances() {
		return instances;
	}

	public void setInstances(List<RdfInstance> instances) {
		this.instances = instances;
	}

	public List<Property> getColumns() {
		return columns;
	}

	public void setColumns(List<Property> columns) {
		this.columns = columns;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
