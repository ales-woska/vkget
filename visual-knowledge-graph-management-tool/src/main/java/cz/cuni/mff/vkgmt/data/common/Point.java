package cz.cuni.mff.vkgmt.data.common;

import java.io.Serializable;

/**
 * 2D point
 * @author Ales Woska
 */
public class Point implements Serializable {
	public int x;
	public int y;
	
	public Point() {
	}

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
}
