package cz.cuni.mff.vkget.data.layout;

import cz.cuni.mff.vkget.data.common.Property;
import cz.cuni.mff.vkget.sparql.Constants;

/**
 * Definition of table column.
 * @author Ales Woska
 *
 */
public class ColumnLayout extends AbstractLayout {
	/**
	 * Which property is shown in column
	 */
    private Property property;
    
    /**
     * What aggregate function to use in case of multiple values.
     */
    private AggregateFunction aggregateFunctions = AggregateFunction.NOTHING;
    
    public ColumnLayout() {
    	this.type = Constants.ColumnLayoutType;
    }
    
    public Property getProperty() {
		return property;
	}

	public void setProperty(Property property) {
		this.property = property;
	}

	public AggregateFunction getAggregateFunction() {
		return aggregateFunctions;
	}

	public void setAggregateFunction(AggregateFunction aggregateFunctions) {
		this.aggregateFunctions = aggregateFunctions;
	}

}
