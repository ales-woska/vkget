package cz.cuni.mff.vkget.data.layout;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import cz.cuni.mff.vkget.sparql.Constants;

public class LineLayout extends AbstractLayout {
	private String fromType;
	private String toType;
	private List<Point> points;
	
	public LineLayout() {
		this.type = Constants.LineLayoutType;
	}

	public String getFromType() {
		return fromType;
	}

	public void setFromType(String fromType) {
		this.fromType = fromType;
	}

	public String getToType() {
		return toType;
	}

	public void setToType(String toType) {
		this.toType = toType;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	public List<Point> getPoints() {
		return points;
	}

	public String getPointsAsString() {
		StringBuilder sb = new StringBuilder();
		for (Point p: this.points) {
			sb.append(" ").append(p.x).append(" ").append(p.y);
		}
		return sb.toString().trim();
	}

	public void setPoints(List<Point> points) {
		this.points = points;
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
