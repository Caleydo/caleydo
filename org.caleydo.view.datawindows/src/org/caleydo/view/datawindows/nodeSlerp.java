package org.caleydo.view.datawindows;

import org.caleydo.core.data.graph.tree.Tree;
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
	private DataWindowsDisk dummyDisk;
	private PoincareNode dummyNode;
	

	public nodeSlerp(double v, Point2D.Double startingPoint,
			Point2D.Double targettingPoint) {
		startPoint = startingPoint;
		targetPoint = targettingPoint;

		// System.out.println("slerp target:"+targetPoint.getX()+"|"+targetPoint.getY());
		speed = v;
		time = new SystemTime();
		((SystemTime) time).rebase();
		// Point2D.Double tempVector;
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
		dummyDisk = new DataWindowsDisk();
		Tree<PoincareNode> dummyTree = new Tree<PoincareNode>();
		dummyNode = new PoincareNode(dummyTree, "dummyNode", 1);
	}

	public boolean doASlerp(Point2D.Double position) {

		actualPoint.setLocation(position);

		normedStatus = distanceToTarget / length;
		// do an accelerated movement, because of a lack of precision caused
		// by the moebius transformation
		// acceleration = -1 * ((normedStatus - 1) * (normedStatus - 1)) + 1;
		slerpFactor = speed * time.deltaT();// * acceleration;

		Point2D.Double tempVector = new Point2D.Double(0, 0);

		tempVector.setLocation(actualPoint.getX() - startPoint.getX(),
				actualPoint.getY() - startPoint.getY());

		// the distance to the target:
		tempVector.setLocation(targetPoint.getX() - actualPoint.getX(),
				targetPoint.getY() - actualPoint.getY());
		distanceToTarget = Math.sqrt(tempVector.getX() * tempVector.getX()
				+ tempVector.getY() * tempVector.getY());

		System.out.println("distance to target :" + distanceToTarget);
		System.out.println("target:" + targetPoint.getX() + "|"
				+ targetPoint.getY());
		System.out.println("slerpfactor:" + slerpFactor);

		
		
		if (distanceToTarget <= slerpFactor) {

			slerpFactor = 0;

			if ((targetPoint.getX() == 0) && (targetPoint.getY() == 0)) {
				returnPoint.setLocation((position.getX()) * -1, (position
						.getY())
						* -1);
				return false;
			}
			
			
			
			double oldDistanceToTarget = distanceToTarget;
			Point2D.Double calcuatedPosition = new Point2D.Double();
			// if the last slerp action should match the target exactly
			System.out.println("new iteration");
			
			double precision=0.01;
			int numberOfIterations = 1000;

			tempVector.setLocation(targetPoint.getX() - actualPoint.getX(),
					targetPoint.getY() - actualPoint.getY());
			oldDistanceToTarget = Math
					.sqrt(tempVector.getX() * tempVector.getX()
							+ tempVector.getY() * tempVector.getY());

			for (int i = 0; i < numberOfIterations; i++) {
				System.out.println("----");
				System.out.println("distance to target :" + distanceToTarget);
				System.out.println("target:" + targetPoint.getX() + "|"
						+ targetPoint.getY());
				System.out.println("actual Point:" + actualPoint.getX() + "|"
						+ actualPoint.getY());
				System.out.println("slerpfactor:" + slerpFactor);

				System.out.println("return Point:" + returnPoint.getX() + "|"
						+ returnPoint.getY());
			
				actualPoint.setLocation(position);

				tempVector.setLocation(targetPoint.getX()
						- calcuatedPosition.getX(), targetPoint.getY()
						- calcuatedPosition.getY());
				oldDistanceToTarget = distanceToTarget;
				distanceToTarget = Math.sqrt(tempVector.getX()
						* tempVector.getX() + tempVector.getY()
						* tempVector.getY());

				// decide, if the last transformation was to much:
		

				System.out.println("new distance to target :"
						+ distanceToTarget);
				System.out.println("new distance to target :"
						+ distanceToTarget);
				System.out.println("calculated Point :" +calcuatedPosition.getX()+"|"+calcuatedPosition.getY());

				//		
				if (distanceToTarget < precision) {
					break;
				}
				if ((distanceToTarget > oldDistanceToTarget) && (i > 1)) {
					break;
				}

				slerpFactor = slerpFactor +precision;

				// dummyNode.setPosition(position);
				ComplexNumber complexPoint = new ComplexNumber();
				complexPoint.setValue(actualPoint.getX(), actualPoint.getY());

				complexPoint = dummyDisk.moebiusTransformation(complexPoint,
						new ComplexNumber(directionVector.getX() * slerpFactor,
								directionVector.getY() * slerpFactor));

				calcuatedPosition.setLocation(complexPoint.getRealPart(),
						complexPoint.getImaginaryPart());
				
				

			}
			System.out.println("----");
			System.out.println("distance to target :" + distanceToTarget);
			System.out.println("target:" + targetPoint.getX() + "|"
					+ targetPoint.getY());
			System.out.println("actual Point:" + actualPoint.getX() + "|"
					+ actualPoint.getY());
			System.out.println("slerpfactor:" + slerpFactor);

			System.out.println("probe:");
	
			
			
			

			returnPoint.setLocation(directionVector.getX() * slerpFactor,
					directionVector.getY() * slerpFactor);
			
			
			
			System.out.println("return Point:" + returnPoint.getX() + "|"
					+ returnPoint.getY());
			
			ComplexNumber complexPoint = new ComplexNumber();
			complexPoint.setValue(position.getX(), position.getY());

			complexPoint = dummyDisk.moebiusTransformation(complexPoint,
					new ComplexNumber(returnPoint.getX(),returnPoint.getY()));

			calcuatedPosition.setLocation(complexPoint.getRealPart(),
					complexPoint.getImaginaryPart());
			System.out.println("calculated Point:" + calcuatedPosition.getX() + "|"
					+ calcuatedPosition.getY());
			
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
