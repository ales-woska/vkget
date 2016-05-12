package cz.cuni.mff.vkget.data.common;


/**
 * 
 * @author Ales Woska
 *
 */
public class Property {
	private String prefix;
	private String name;
	
	public Property(String property) {
		if (property == null || property.isEmpty()) {
			prefix = null;
			name = null;
		} else {
			String[] p = property.split(":");
			prefix = p[0];
			name = p[1];
		}
	}
	
	public Property(String prefix, String name) {
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

}
