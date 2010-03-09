package org.caleydo.view.datawindows;

import org.caleydo.core.util.system.SystemTime;
import org.caleydo.core.util.system.Time;

import java.awt.geom.Point2D;

public class nodeSlerp {
	public Point2D.Double startPoint;
	public Point2D.Double targetPoint;
	private Point2D.Double directionVector;
	private double length;
	public double distanceToTarget;
	public double speed;
	public Point2D.Double returnPoint;
	private Point2D.Double actualPoint;
	private double slerpFactor = 0;
	private Time time;
	private double acceleration = 0;
	private double normedStatus = 0;
	private double dLength = 0;


	public nodeSlerp(double v, Point2D.Double startingPoint,
			Point2D.Double targettingPoint) {
		startPoint = startingPoint;
		targetPoint = targettingPoint;
		
		//System.out.println("slerp target:"+targetPoint.getX()+"|"+targetPoint.getY());
		speed = v;
		time = new SystemTime();
		((SystemTime) time).rebase();
		//Point2D.Double tempVector;
		// calculate the direction of the slerp
		Point2D.Double tempVector;
		tempVector = new Point2D.Double(targetPoint.getX() - startPoint.getX(),
				targetPoint.getY() - startPoint.getY());
		length = Math.sqrt(tempVector.getX() * tempVector.getX()
				+ tempVector.getY() * tempVector.getY());
		directionVector = new Point2D.Double(tempVector.getX() / length,
				tempVector.getY() / length);
		actualPoint = new Point2D.Double(0, 0);
		returnPoint = new Point2D.Double(0, 0);
		actualPoint.setLocation(startPoint);
		time.update();
	}

	public boolean doASlerp(Point2D.Double position) {

		actualPoint.setLocation(position);

		normedStatus = distanceToTarget / length;
		// do an accelerated movement, because of a lack of precision caused
		// by the moebius transformation
		//acceleration = -1 * ((normedStatus - 1) * (normedStatus - 1)) + 1;
		slerpFactor = speed * time.deltaT();//* acceleration;
		
		
		Point2D.Double tempVector = new Point2D.Double(0,0);
		
		tempVector.setLocation(actualPoint.getX() - startPoint.getX(),
				actualPoint.getY() - startPoint.getY());

		// the distance to the target:
		tempVector.setLocation(targetPoint.getX() - actualPoint.getX(),
				targetPoint.getY() - actualPoint.getY());
		distanceToTarget = Math.sqrt(tempVector.getX() * tempVector.getX()
				+ tempVector.getY() * tempVector.getY());

		System.out.println("distance slerp:"+distanceToTarget);
		System.out.println("target:"+targetPoint.getX()+"|"+targetPoint.getY());
		System.out.println("slerpfactor:"+slerpFactor);
		
		
		if (distanceToTarget <= slerpFactor) {
			
			
			
			return false;
		}

		tempVector.setLocation(targetPoint.getX() - actualPoint.getX(),
				targetPoint.getY() - actualPoint.getY());
		dLength = Math.sqrt(tempVector.getX() * tempVector.getX()
				+ tempVector.getY() * tempVector.getY());

		directionVector.setLocation(tempVector.getX() / dLength, tempVector
				.getY()
				/ dLength);

		returnPoint.setLocation(directionVector.getX() * slerpFactor,
				directionVector.getY() * slerpFactor);
		time.update();

		return true;

	}

}
