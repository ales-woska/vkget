package cz.cuni.mff.vkget.connect;

import java.util.Map;

import org.springframework.stereotype.Service;

import cz.cuni.mff.vkget.data.common.Property;

/**
 * Implementatinon of @see DataConnector for Virtuoso endpoints.
 * @author Ales Woska
 *
 */
@Service
public class VirtuosoDataConnector extends CommonDataConnector {
	
	public VirtuosoDataConnector() {}
	
	public VirtuosoDataConnector(ConnectionInfo connectionInfo) {
		super(connectionInfo);
	}

	
	/**
	 * Uses Virtuoso specific functions.
	 */
	@Override
	protected void appendContainsFunction(StringBuilder sb, Map<Property, String> propertyVarMap, Property property, String filterValue) {
		sb.append(" && (?").append(propertyVarMap.get(property)).append(" bif:contains '").append(filterValue).append("') ");
	}	
}
