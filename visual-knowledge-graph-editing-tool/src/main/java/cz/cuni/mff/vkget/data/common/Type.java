package cz.cuni.mff.vkget.data.common;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import cz.cuni.mff.vkget.utils.TypeJsonDeserializer;

/**
 * 
 * @author Ales Woska
 *
 */

@JsonDeserialize(using = TypeJsonDeserializer.class)
public class Type {
	private String type;
	private String prefix;
	private String name;
	
	public Type() {}
	
	public Type(String type) {
		this.type = type;
		if (type == null || type.isEmpty()) {
			prefix = null;
			name = null;
		} else {
			String[] p = type.split(":");
			if (p.length == 1) {
				prefix = "";
				name = p[0];
			} else {
				prefix = p[0];
				name = p[1];
			}
		}
	}
	
	public Type(String prefix, String name) {
		this.type = prefix + ":" + name;
		this.prefix = prefix;
		this.name = name;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
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
		if (!(o instanceof Type)) {
			return false;
		} else {
			Type t = (Type) o;
			return t.toString().equals(this.toString());
		}
	}

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
