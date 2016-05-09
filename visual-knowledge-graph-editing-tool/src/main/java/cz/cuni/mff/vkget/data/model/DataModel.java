package cz.cuni.mff.vkget.data.model;

import java.io.Serializable;
import java.util.List;

/**
 * Objects holding all @see RdfTable objects.
 * @author Ales Woska
 *
 */
public class DataModel implements Serializable {
	
	/**
	 * Collection of @see @inheritDoc
	 */
    private List<RdfTable> tables;

	public List<RdfTable> getTables() {
		return tables;
	}

	public void setTables(List<RdfTable> tables) {
		this.tables = tables;
	}
    
}
