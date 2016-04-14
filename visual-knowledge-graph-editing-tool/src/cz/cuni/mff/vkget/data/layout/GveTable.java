package cz.cuni.mff.vkget.data.layout;

import java.util.List;

import cz.cuni.mff.vkget.data.model.RdfObject;

/**
 * GveTable is table which contains subjects as lines. It's columns are
 * RdfProperties from within subjects.
 *
 * @param classURI
 *            : Uri of rdf class of this table (each table has exactly one rdf
 *            cass)
 * @param instances
 *            : class instances
 * @param columnsURIs
 *            : list of columns identified by their URIs
 */
public class GveTable {
	private String classUri;
	private List<RdfObject> instances;
	private List<String> columnsURIs;

	public String getClassUri() {
		return classUri;
	}

	public void setClassUri(String classUri) {
		this.classUri = classUri;
	}

	public List<RdfObject> getInstances() {
		return instances;
	}

	public void setInstances(List<RdfObject> instances) {
		this.instances = instances;
	}

	public List<String> getColumnsURIs() {
		return columnsURIs;
	}

	public void setColumnsURIs(List<String> columnsURIs) {
		this.columnsURIs = columnsURIs;
	}

}
