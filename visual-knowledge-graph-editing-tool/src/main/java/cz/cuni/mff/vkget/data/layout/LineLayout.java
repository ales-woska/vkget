package cz.cuni.mff.vkget.data.layout;

import java.util.ArrayList;
import java.util.List;

import cz.cuni.mff.vkget.data.common.Point;
import cz.cuni.mff.vkget.data.common.Property;
import cz.cuni.mff.vkget.data.common.Type;
import cz.cuni.mff.vkget.sparql.Constants;

/**
 * Layout of property connecting tables
 * @author Ales Woska
 *
 */
public class LineLayout extends AbstractLayout {
	
	private Type fromType;
	private Type toType;
	private Property property;
	private List<Point> points;
	
	public LineLayout() {
		this.type = Constants.LineLayoutType;
	}

	public Type getFromType() {
		return fromType;
	}


	public void setFromType(Type fromType) {
		this.fromType = fromType;
	}


	public Type getToType() {
		return toType;
	}


	public void setToType(Type toType) {
		this.toType = toType;
	}


	public List<Point> getPoints() {
		return points;
	}


	public void setProperty(Property property) {
		this.property = property;
	}


	public String getPointsAsString() {
		StringBuilder sb = new StringBuilder();
		if (points != null) {
			for (Point p: this.points) {
				sb.append(" ").append(p.x).append(" ").append(p.y);
			}
		}
		return sb.toString().trim();
	}

	public void setPoints(List<Point> points) {
		this.points = points;
	}

	public Property getProperty() {
		return property;
	}

	public void setPointsFromString(String pointsString) {
		String[] points = pointsString.split(" ");
		this.points = new ArrayList<Point>();
		for (int i = 0; i < points.length; i += 2) {
			String x = points[i];
			String y = points[i + 1];
			this.points.add(new Point(Integer.valueOf(x), Integer.valueOf(y)));
		}
	}
	
	public int getLeft() {
		int middle = getPoints().size() / 2 - 1;
		Point point1 = getPoints().get(middle);
		Point point2 = getPoints().get(middle + 1);
		return (point1.x + point2.x) / 2;
	}
	
	public int getTop() {
		int middle = getPoints().size() / 2 - 1;
		Point point1 = getPoints().get(middle);
		Point point2 = getPoints().get(middle + 1);
		return (point1.y + point2.y) / 2;
	}


}
