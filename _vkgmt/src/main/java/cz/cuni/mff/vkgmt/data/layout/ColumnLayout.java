package cz.cuni.mff.vkgmt.data.layout;

import cz.cuni.mff.vkgmt.data.common.Property;
import cz.cuni.mff.vkgmt.data.common.RdfEntity;
import cz.cuni.mff.vkgmt.sparql.Constants;

/**
 * Definition of table column.
 * @author Ales Woska
 *
 */
public class ColumnLayout extends RdfEntity {
	/**
	 * Which property is shown in column
	 */
	protected Property property;

	protected Label label;
    
    /**
     * What aggregate function to use in case of multiple values.
     */
	protected AggregateFunction aggregateFunction = AggregateFunction.NOTHING;
    
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
		return aggregateFunction;
	}

	public void setAggregateFunction(AggregateFunction aggregateFunctions) {
		this.aggregateFunction = aggregateFunctions;
	}

	public Label getLabel() {
		return label;
	}

	public void setLabel(Label label) {
		this.label = label;
	}
	
	public boolean isUriColumn() {
		if (property == null) {
			return false;
		}
		if (property.getPrefix() != null && !property.getPrefix().isEmpty()) {
			return false;
		}
		if (property.getName() == null) {
			return false;
		}
		if (property.getName().equals("URI")) {
			return true;
		}
		return false;
	}

}
