package cz.cuni.mff.vkget.data.layout;

import java.util.ArrayList;
import java.util.List;

import cz.cuni.mff.vkget.data.common.Point;
import cz.cuni.mff.vkget.data.common.Property;
import cz.cuni.mff.vkget.sparql.Constants;

/**
 * Layout of property connecting tables
 * @author Ales Woska
 *
 */
public class LineLayout extends AbstractLayout {
	
	private BlockLayout fromType;
	private BlockLayout toType;
	private Property property;
	private List<Point> points;
	
	public LineLayout() {
		this.type = Constants.LineLayoutType;
	}

	public BlockLayout getFromType() {
		return fromType;
	}


	public void setFromType(BlockLayout fromType) {
		this.fromType = fromType;
	}


	public BlockLayout getToType() {
		return toType;
	}


	public void setToType(BlockLayout toType) {
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

}
