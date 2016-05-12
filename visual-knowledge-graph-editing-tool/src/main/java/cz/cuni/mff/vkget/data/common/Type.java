package cz.cuni.mff.vkget.data.common;

/**
 * 
 * @author Ales Woska
 *
 */
public class Type {
	private String prefix;
	private String name;
	
	public Type(String type) {
		if (type == null || type.isEmpty()) {
			prefix = null;
			name = null;
		} else {
			String[] p = type.split(":");
			prefix = p[0];
			name = p[1];
		}
	}
	
	public Type(String prefix, String name) {
		this.prefix = prefix;
		this.name = name;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getName() {
		return name;
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
