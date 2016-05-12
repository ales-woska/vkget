package cz.cuni.mff.vkget.sparql;

import java.util.Map;

import org.apache.jena.ext.com.google.common.collect.ImmutableMap;

import cz.cuni.mff.vkget.data.common.Property;
import cz.cuni.mff.vkget.data.common.Type;

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
	public static final Type ScreenLayoutType = new Type(VKGET_Prefix, "ScreenLayout");
	
	/**
	 * RDF type of BlockLayout
	 */
	public static final Type BlockLayoutType = new Type(VKGET_Prefix, "BlockLayout");
	
	/**
	 * RDF type of LineLayout
	 */
	public static final Type LineLayoutType = new Type(VKGET_Prefix, "LineLayout");
	
	/**
	 * RDF type of ColumnLayout
	 */
	public static final Type ColumnLayoutType = new Type(VKGET_Prefix, "ColumnLayout");
	
	/**
	 * RDF property 
	 */
	public static final Property ScreenLayoutProperty = new Property(VKGET_Prefix, "screenLayout");
	
	/**
	 * Contain BlockLayout property
	 */
	public static final Property BlockLayoutProperty = new Property(VKGET_Prefix, "blockLayout");
	
	/**
	 * Contain LineLayout property
	 */
	public static final Property LineLayoutProperty = new Property(VKGET_Prefix, "lineLayout");
	
	/**
	 * Contain ColumnLayout property
	 */
	public static final Property ColumnLayoutProperty = new Property(VKGET_Prefix, "columnLayout");
	
	/**
	 * RDF Type property
	 */
	public static final Property RDF_TYPE = new Property("rdf", "type");
	
	/**
	 * RDF Label property
	 */
	public static final Property RDFS_LABEL = new Property("rdfs", "label");
	
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
