package cz.cuni.mff.vkgmt.connect;

import java.util.Map;

import org.springframework.stereotype.Service;

import cz.cuni.mff.vkgmt.data.common.Property;

/**
 * Implementatinon of @see DataConnector for Virtuoso endpoints.
 * @author Ales Woska
 *
 */
@Service
public class VirtuosoDataConnector extends DefaultDataConnector {
	
	public VirtuosoDataConnector() {}
	
	public VirtuosoDataConnector(ConnectionInfo connectionInfo) {
		super(connectionInfo);
	}

	
	/**
	 * Uses Virtuoso specific functions.
	 */
	@Override
	protected void appendContainsFunction(StringBuilder sb, Map<Property, String> propertyVarMap, Property property, String filterValue) {
		String varName = propertyVarMap.get(property);
		sb.append(" && (contains(lcase(?").append(varName).append("), '").append(filterValue.toLowerCase()).append("')) ");
	}	
}
