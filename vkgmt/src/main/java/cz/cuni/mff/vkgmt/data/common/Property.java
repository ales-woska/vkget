package cz.cuni.mff.vkgmt.data.common;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import cz.cuni.mff.vkgmt.utils.PropertyJsonDeserializer;


/**
 * 
 * @author Ales Woska
 *
 */
@JsonDeserialize(using = PropertyJsonDeserializer.class)
public class Property {
	protected String property;
	protected String prefix;
	protected String name;
	
	public Property() {}
	
	public Property(String property) {
		this.property = property;
		if (property == null || property.isEmpty()) {
			prefix = null;
			name = null;
		} else {
			String[] p = property.split(":");
			if (p.length == 1) {
				prefix = "";
				name = p[0];
			} else {
				prefix = p[0];
				name = p[1];
			}
		}
	}
	
	public Property(String prefix, String name) {
		this.property = prefix + ":" + name;
		this.prefix = prefix;
		this.name = name;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getName() {
		return name;
	}
	
	public String getProperty() {
		return property;
	}

	@Override
	public String toString() {
		if (prefix == null || prefix.isEmpty()) {
			return name;
		} else {
			return prefix + ":" + name;
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Property)) {
			return false;
		} else {
			Property p = (Property) o;
			return p.toString().equals(this.toString());
		}
	}

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
	
	public boolean isUriProperty() {
		return "URI".equals(this.toString());
	}

}
