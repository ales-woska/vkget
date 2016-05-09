package cz.cuni.mff.vkget.sparql;

import java.util.Map;

import org.apache.jena.ext.com.google.common.collect.ImmutableMap;

/**
 * Constants used in application.
 * @author Ales Woska
 *
 */
public class Constants {
	/*
	 * Whole URI of application namespace
	 */
	public static final String VKGET_Namespace = "http://mff.cuni.cz/vkget#";
	
	/**
	 * Prefix of application namespace.
	 */
	public static final String VKGET_Prefix = "vkget";
	
	/**
	 * RDF type of ScreenLayout
	 */
	public static final String ScreenLayoutType = VKGET_Prefix + ":ScreenLayout";
	
	/**
	 * RDF type of BlockLayout
	 */
	public static final String BlockLayoutType = VKGET_Prefix + ":BlockLayout";
	
	/**
	 * RDF type of LineLayout
	 */
	public static final String LineLayoutType = VKGET_Prefix + ":LineLayout";
	
	/**
	 * RDF type of ColumnLayout
	 */
	public static final String ColumnLayoutType = VKGET_Prefix + ":ColumnLayout";
	
	/**
	 * RDF property 
	 */
	public static final String ScreenLayoutProperty = VKGET_Prefix + ":screenLayout";
	
	/**
	 * Contain BlockLayout property
	 */
	public static final String BlockLayoutProperty = VKGET_Prefix + ":blockLayout";
	
	/**
	 * Contain LineLayout property
	 */
	public static final String LineLayoutProperty = VKGET_Prefix + ":lineLayout";
	
	/**
	 * Contain ColumnLayout property
	 */
	public static final String ColumnLayoutProperty = VKGET_Prefix + ":columnLayout";
	
	/**
	 * RDF Type property
	 */
	public static final String RDF_TYPE = "rdf:type";
	
	/**
	 * RDF Title property
	 */
	public static final String RDFS_TITLE = "rdfs:title";
	
	/**
	 * RDF Label property
	 */
	public static final String RDFS_LABEL = "rdfs:label";
	
	/**
	 * Common namespace definition for SPARQL query
	 */
	public static final String PREFIX_PART = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
			+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
			+ "PREFIX "+VKGET_Prefix+": <"+VKGET_Namespace+"> ";
	
	/**
	 * NS prefix mappint to namespaces as pair prefix:namespace
	 */
	public static final Map<String, String> nsPrefixMapping = ImmutableMap.<String, String>builder()
		    .put("rdfs", "http://www.w3.org/2000/01/rdf-schema#")
		    .put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#")
		    .put(VKGET_Prefix, VKGET_Namespace)
		    .build();

}
