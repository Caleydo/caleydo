package org.caleydo.core.manager.view;

import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.swt.graphics.Point;

public class SelectionPoint2D {
	
	protected int viewID;
	
	protected String deskoXID;

	protected int x;
	
	protected int y;
	
	public SelectionPoint2D() {
		deskoXID = null;
		viewID = -1;
		x = 0;
		y = 0;
	}
	
	public SelectionPoint2D(String deskoXID, int viewID, Point point) {
		this.deskoXID = deskoXID;
		this.viewID = viewID;
		this.x = point.x;
		this.y = point.y;
	}

	public String getDeskoXID() {
		return deskoXID;
	}

	public void setDeskoXID(String deskoXID) {
		this.deskoXID = deskoXID;
	}

	public int getViewID() {
		return viewID;
	}

	public void setViewID(int viewID) {
		this.viewID = viewID;
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

	@XmlTransient
	public Point getPoint() {
		return new Point(x, y);
	}
	
	public void setPoint(Point point) {
		this.x = point.x;
		this.y = point.y;
	}
	
	public String toString() {
		String s = "{'" + deskoXID + "', " +
			viewID + ", " +
			"(" + x + ", " + y + ") }";
		return s;
	}
}
