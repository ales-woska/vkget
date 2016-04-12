package cz.cuni.mff.vkget.layout;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class LineLayout extends AbstractLayout {
	private String fromClass;
	private String toClass;
	private String title;
	private List<Point> points;

	public String getFromClass() {
		return fromClass;
	}

	public void setFromClass(String fromClass) {
		this.fromClass = fromClass;
	}

	public String getToClass() {
		return toClass;
	}

	public void setToClass(String toClass) {
		this.toClass = toClass;
	}

	public String getTitle() {
		return title;
	}

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
