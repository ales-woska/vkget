package cz.cuni.mff.vkget.data;

import java.util.List;

import cz.cuni.mff.vkget.layout.GveTable;

/**
  * Class contains all GveTables (whole data)
 *
  * @param tables list of all gve tables
  */
public class DataModel {
    private List<GveTable> tables;

	public List<GveTable> getTables() {
		return tables;
	}

	public void setTables(List<GveTable> tables) {
		this.tables = tables;
	}
    
}
