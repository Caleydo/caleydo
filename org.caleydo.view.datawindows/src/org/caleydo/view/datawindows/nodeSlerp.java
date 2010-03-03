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
	public double distanceToTarget;
	public double speed;
	public Point2D.Double returnPoint;
	private Point2D.Double actualPoint;
	private double slerpFactor = 0;
	private Time time;
	private int readyFlag=0;
	private double acceleration=0;
	private double normedStatus=0;

	public nodeSlerp(double v, Point2D.Double startingPoint,
			Point2D.Double targettingPoint) {
		startPoint = startingPoint;
		targetPoint = targettingPoint;
		speed = v;
		System.out.println("starting a slerp");
		time = new SystemTime();
		((SystemTime) time).rebase();
		Point2D.Double tempVector;
		tempVector = new Point2D.Double(targetPoint.getX() - startPoint.getX(),
				targetPoint.getY() - startPoint.getY());
		length = Math.sqrt(tempVector.getX() * tempVector.getX()
				+ tempVector.getY() * tempVector.getY());
		System.out.println("slerp erstellt länge="+length);
		
		directionVector = new Point2D.Double(tempVector.getX() / length,
				tempVector.getY() / length);
		actualPoint = new Point2D.Double(0, 0);
		returnPoint = new Point2D.Double(0, 0);
		actualPoint.setLocation(startPoint);
		actualLength = 0;
		time.update();
		
	}

	public boolean doASlerp(Point2D.Double position) {
		Point2D.Double tempVector;

		actualPoint.setLocation(position);
		
		
		System.out.println("distanceToTarget:"+distanceToTarget);
		System.out.println("slerpfactor:"+slerpFactor);
		System.out.println("length:"+length);
		
		normedStatus=distanceToTarget/length;
		acceleration=-1*((normedStatus-1)*(normedStatus-1))+1;
		
		System.out.println("normedStatus:"+normedStatus);
		System.out.println("beschleunigung:"+acceleration);
		slerpFactor = speed * time.deltaT()*acceleration;
		
		tempVector = new Point2D.Double(actualPoint.getX() - startPoint.getX(),
				actualPoint.getY() - startPoint.getY());
		actualLength = Math.sqrt(tempVector.getX() * tempVector.getX()
				+ tempVector.getY() * tempVector.getY());

		
		
		//the distance to the target:
		tempVector = new Point2D.Double(targetPoint.getX()-actualPoint.getX(),
					targetPoint.getY()-actualPoint.getY());
	distanceToTarget = Math.sqrt(tempVector.getX() * tempVector.getX()
				+ tempVector.getY() * tempVector.getY());
	
	
		
		//if (actualLength >= length) {
			//return false;
	//	}

		
	if(readyFlag==1){
		readyFlag=0;
		return false;
		
	}
	
	if (distanceToTarget<=slerpFactor){
    	readyFlag = 1;
    	
    }

		
		tempVector = new Point2D.Double(targetPoint.getX()-actualPoint.getX(),
				targetPoint.getY()-actualPoint.getY());
		double dlength = Math.sqrt(tempVector.getX() * tempVector.getX()
				+ tempVector.getY() * tempVector.getY());
		
		
		directionVector.setLocation(tempVector.getX() /dlength,
				tempVector.getY() /dlength);
		
	
	
		//actualPoint.setLocation(actualPoint.getX() + directionVector.getX()
			//	* slerpFactor, actualPoint.getY() + directionVector.getY()
				//* slerpFactor);
		
		
		
		
		returnPoint.setLocation(directionVector.getX() * slerpFactor,
				directionVector.getY() * slerpFactor);
		time.update();
		
	
		return true;

	}

}
