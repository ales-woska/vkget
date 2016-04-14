package cz.cuni.mff.vkget.sparql;

public class Constants {
	public static final String VKGET_Namespace = "http://mff.cuni.cz/vkget#";
	public static final String VKGET_Prefix = "vkget";
	public static final String ScreenLayoutType = VKGET_Prefix + ":ScreenLayout";
	public static final String BlockLayoutType = VKGET_Prefix + ":BlockLayout";
	public static final String LineLayoutType = VKGET_Prefix + ":LineLayout";
	public static final String ScreenLayoutProperty = VKGET_Prefix + ":screenLayout";
	public static final String BlockLayoutProperty = VKGET_Prefix + ":blockLayout";
	public static final String LineLayoutProperty = VKGET_Prefix + ":lineLayout";
	public static final String RDF_TYPE = "rdf:type";
	public static final String RDFS_TITLE = "rdfs:title";
	
	public static final String PREFIX_PART = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
			+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
			+ "PREFIX "+VKGET_Prefix+": <"+VKGET_Namespace+"> ";

}
