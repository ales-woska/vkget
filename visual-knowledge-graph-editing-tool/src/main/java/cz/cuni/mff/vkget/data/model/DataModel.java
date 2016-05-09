package cz.cuni.mff.vkget.data.model;

import java.io.Serializable;
import java.util.List;

/**
  * Class contains all GveTables (whole data)
 *
  * @param tables list of all gve tables
  */
public class DataModel implements Serializable {
    private List<RdfTable> tables;

	public List<RdfTable> getTables() {
		return tables;
	}

	public void setTables(List<RdfTable> tables) {
		this.tables = tables;
	}
	
	public RdfTable getTableByType(String type) {
		for (RdfTable table: tables) {
			if (table.getTypeUri().equals(type)) {
				return table;
			}
		}
		return null;
	}
    
}
