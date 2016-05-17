package cz.cuni.mff.vkget.data.layout;


/**
 * 
 * @author Ales Woska
 *
 */
public class Label {
	private LabelType type;
	private String labelSource;
	private String lang;
	
	public Label() {}
	
	public Label(LabelType type, String source, String lang) {
		this.type = type;
		this.labelSource = source;
		this.lang = lang;
	}

	public LabelType getType() {
		return type;
	}

	public void setType(LabelType type) {
		this.type = type;
	}

	public String getLabelSource() {
		return labelSource;
	}

	public void setLabelSource(String labelSource) {
		this.labelSource = labelSource;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

}
