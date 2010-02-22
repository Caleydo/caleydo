package org.caleydo.view.datawindows;

import java.awt.geom.Point2D;

public class PoincareDisk {

	@SuppressWarnings("unused")
	private double radius;
	
	
	
	public PoincareDisk(double diskRadius) {
		radius = diskRadius;
	}
	
	public void loadTree(){
		
		
	}
	
	public void centerNode(){
		
	}
	
	public void translateTree(){
		
	}
	
	public void scaleTree(){
		
	}
	
	public Point2D.Double projectPoint(Point2D.Double coordinate) {

		double coordinateLength = coordinate.getX() * coordinate.getX()
				+ coordinate.getY() * coordinate.getY();
		coordinateLength = Math.sqrt(coordinateLength);
		double radiussquare = radius * radius;
		double projectionFactor = (2 * radiussquare)
				/ (radiussquare + coordinateLength);
		coordinate.setLocation(coordinate.getX() * projectionFactor / 7 * 2,
				coordinate.getY() * projectionFactor / 7 * 2);
		return coordinate;
	}
	
	
	public void projectTree(){
		
	}
	
	public void calculateLinePoints(){
		
	}
	
	
}
