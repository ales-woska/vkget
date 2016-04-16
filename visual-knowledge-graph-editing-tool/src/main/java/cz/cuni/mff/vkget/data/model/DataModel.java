package cz.cuni.mff.vkget.data.model;

import java.io.Serializable;
import java.util.List;

import cz.cuni.mff.vkget.data.layout.GveTable;

/**
  * Class contains all GveTables (whole data)
 *
  * @param tables list of all gve tables
  */
public class DataModel implements Serializable {
    private List<GveTable> tables;

	public List<GveTable> getTables() {
		return tables;
	}

	public void setTables(List<GveTable> tables) {
		this.tables = tables;
	}
    
}
