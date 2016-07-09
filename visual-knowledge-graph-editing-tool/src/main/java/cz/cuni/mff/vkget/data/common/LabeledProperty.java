package cz.cuni.mff.vkget.data.common;



/**
 * 
 * @author Ales Woska
 *
 */
public class LabeledProperty extends Property {
	private String label;
	
	public LabeledProperty() {}
	
	public LabeledProperty(Property property, String label) {
		super(property.getProperty());
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
}
