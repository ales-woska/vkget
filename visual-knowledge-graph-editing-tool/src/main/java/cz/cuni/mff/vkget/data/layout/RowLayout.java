package cz.cuni.mff.vkget.data.layout;

import java.util.ArrayList;
import java.util.List;

public class RowLayout extends AbstractLayout {
    private String property;
    private List<AggregateFunction> aggregateFunctions = new ArrayList<AggregateFunction>();
    
    public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public List<AggregateFunction> getAggregateFunctions() {
		return aggregateFunctions;
	}

	public void setAggregateFunctions(List<AggregateFunction> aggregateFunctions) {
		this.aggregateFunctions = aggregateFunctions;
	}

	public String getAggregateFunctionsAsString() {
		StringBuilder sb = new  StringBuilder();
		for (AggregateFunction type: this.aggregateFunctions) {
			sb.append(" ").append(type.getText());
		}
		return sb.toString().trim();
	}
	
	public void setAggregateFunctionsFromString(String aggregateFunctions) {
		if (aggregateFunctions == null) {
			return;
		}
		String[] types = aggregateFunctions.split(" ");
		this.aggregateFunctions = new ArrayList<AggregateFunction>();
		for (String type: types) {
			this.aggregateFunctions.add(AggregateFunction.fromString(type));
		}
	}

}
