package cz.cuni.mff.vkget.data.layout;

import cz.cuni.mff.vkget.data.common.Uri;

/**
 * 
 * @author Ales Woska
 *
 */
public class Label {
	private Uri forUri;
	private LabelType type;
	private String labelSource;
	private String lang;
	
	public Label() {}
	
	public Label(Uri uri, LabelType type, String source, String lang) {
		this.forUri = uri;
		this.type = type;
		this.labelSource = source;
		this.lang = lang;
	}

	public Uri getForUri() {
		return forUri;
	}

	public void setForUri(Uri forUri) {
		this.forUri = forUri;
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
