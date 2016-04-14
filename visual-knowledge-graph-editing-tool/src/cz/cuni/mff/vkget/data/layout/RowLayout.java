package cz.cuni.mff.vkget.data.layout;

import java.util.List;

public class RowLayout {
    private String propertyName;
    private List<TitleType> titleTypes;
    private String titleResource;
    private List<AggregateFunction> aggregateFunctions;
    
    public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public List<TitleType> getTitleTypes() {
		return titleTypes;
	}

	public void setTitleTypes(List<TitleType> titleTypes) {
		this.titleTypes = titleTypes;
	}

	public String getTitleResource() {
		return titleResource;
	}

	public void setTitleResource(String titleResource) {
		this.titleResource = titleResource;
	}

	public List<AggregateFunction> getAggregateFunctions() {
		return aggregateFunctions;
	}

	public void setAggregateFunctions(List<AggregateFunction> aggregateFunctions) {
		this.aggregateFunctions = aggregateFunctions;
	}

	public String getTitle() {
        String returnTitle = "";
        for (TitleType titleType: titleTypes) {
            if (returnTitle.isEmpty()) {
            	switch (titleType) {
                    case URL: return propertyName;
                    case PROPERTY: return propertyName;
                    case CONSTANT: return titleResource;
                    default: return "";
                }
            } else {
            	break;
            }
        }
        return returnTitle;
    }

    public String getValue(List<String> properties) {
        if (properties.size() == 1) {
            properties.get(0);
        } else {
            for (AggregateFunction aggregateFunction: aggregateFunctions) {
                switch (aggregateFunction) {
                    case MAX: return max(properties);
                    case MIN: return min(properties);
                    case AVG: return avg(properties);
                    case NOTHING: return flatten(properties);
                    default: return null;
                }
            }
        }
        return "";
    }

    private String max(List<String> strings) {
        long max = 0;
        for (String s: strings) {
            long n = Long.valueOf(s);
            if (n > max) {
            	max = n;
            }
        }
        return String.valueOf(max);
    }

    private String min(List<String> strings) {
        long min = Long.MAX_VALUE;
        for (String s: strings) {
            long n = Long.valueOf(s);
            if (n < min) {
            	min = n;
            }
        }
        return String.valueOf(min);
    }

    private String avg(List<String> strings) {
        long sum = 0;
        for (String s: strings) {
            sum += Long.parseLong(s);       
        }
        return String.valueOf(sum / strings.size());
    }

    private String flatten(List<String> strings) {
        String value = "";
        for (String s: strings) {
        	value += " " + s;
        }
        return value;
    }
}
