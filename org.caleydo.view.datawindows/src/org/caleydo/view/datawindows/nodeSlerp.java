package org.caleydo.view.datawindows;

import org.caleydo.core.util.system.SystemTime;
import org.caleydo.core.util.system.Time;

import java.awt.geom.Point2D;

public class nodeSlerp {
	public Point2D.Double startPoint;
	public Point2D.Double targetPoint;
	private Point2D.Double directionVector;
	private double length;
	private double actualLength;
	public double speed;
	public Point2D.Double returnPoint;
	private Point2D.Double actualPoint;
	private double slerpFactor = 0;
	private Time time;

	public nodeSlerp(double v, Point2D.Double startingPoint,
			Point2D.Double targettingPoint) {
		startPoint=startingPoint;
		targetPoint=targettingPoint;
		speed=v;
		System.out.println("starting a slerp");
		time = new SystemTime();
		((SystemTime) time).rebase();
		Point2D.Double tempVector;
		tempVector = new Point2D.Double(targetPoint.getX() - startPoint.getX(),
				targetPoint.getY() - startPoint.getY());
		length = Math.sqrt(tempVector.getX() * tempVector.getX()
				+ tempVector.getY() * tempVector.getY());
		directionVector = new Point2D.Double(tempVector.getX() / length,
				tempVector.getY() / length);
		actualPoint = new Point2D.Double(0,0);
		returnPoint = new Point2D.Double(0,0);
		actualPoint.setLocation(startPoint);
		actualLength = 0;
		
	}

	public boolean doASlerp() {
		Point2D.Double tempVector;

		time.update();
		
		tempVector = new Point2D.Double(actualPoint.getX() - startPoint.getX(),
				actualPoint.getY() - startPoint.getY());
		
	
		actualLength = Math.sqrt(tempVector.getX() * tempVector.getX()
				+ tempVector.getY() * tempVector.getY());
		

		if( actualLength>=length){
			return false;
		}
		//speed=100;
		System.out.println("actualLength: "+ actualLength);
		System.out.println("length: "+ length);
		System.out.println("startpoint:"+startPoint.getX()+"|"+startPoint.getY());
		
		
		slerpFactor = speed *  time.deltaT();
		
		System.out.println("slerpfac: "+ slerpFactor);
			actualPoint.setLocation(actualPoint.getX()+directionVector.getX()*slerpFactor, 
					actualPoint.getY()+directionVector.getY()*slerpFactor);
		returnPoint.setLocation(directionVector.getX()*slerpFactor, 
				directionVector.getY()*slerpFactor);
		System.out.println("returnPoint:"+returnPoint.getX()+"|"+returnPoint.getY());
		return true;
		
		
		
	}

}
