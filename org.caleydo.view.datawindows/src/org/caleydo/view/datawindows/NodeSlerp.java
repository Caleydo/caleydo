package org.caleydo.view.datawindows;

import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.util.system.SystemTime;
import org.caleydo.core.util.system.Time;

public class NodeSlerp {
	public float[] startPoint;
	public float[] targetPoint;
	private float[] directionVector;
	private float length;
	public float distanceToTarget;
	public float speed;
	public float[] returnPoint;
	private float[] actualPoint;
	private float slerpFactor = 0;
	private Time time;
	private float dLength = 0;
	private DataWindowsDisk dummyDisk;
	float precision = 0.01f;
	int numberOfIterations = 1000;

	public NodeSlerp(float speed, float[] startPoint, float[] targetPoint) {
		this.startPoint = startPoint;
		this.targetPoint = targetPoint;
		this.speed = speed;
		time = new SystemTime();
		((SystemTime) time).rebase();

		// calculate the direction of the slerp
		float[] tempVector = new float[2];
		tempVector[0] = targetPoint[0] - startPoint[0];
		tempVector[1] = targetPoint[1] - startPoint[1];

		length = (float) Math.sqrt(tempVector[0] * tempVector[0]
				+ tempVector[1] * tempVector[1]);
		directionVector = new float[2];
		directionVector[0] = tempVector[0] / length;
		directionVector[1] = tempVector[1] / length;
		actualPoint = new float[2];
		returnPoint = new float[2];
		actualPoint = startPoint.clone();
		time.update();
		dummyDisk = new DataWindowsDisk(null);
		Tree<PoincareNode> dummyTree = new Tree<PoincareNode>();
		new PoincareNode(dummyTree, "dummyNode", 1);
	}

	public boolean doASlerp(float[] position) {

		actualPoint = position.clone();

		// do an accelerated movement, because of a lack of precision caused
		// by the moebius transformation
		// acceleration = -1 * ((normedStatus - 1) * (normedStatus - 1)) + 1;
		slerpFactor = speed * (float) time.deltaT();// * acceleration;

		float[] tempVector = new float[2];

		tempVector[0] = actualPoint[0] - startPoint[0];
		tempVector[1] = actualPoint[1] - startPoint[1];

		// the distance to the target:
		tempVector[0] = targetPoint[0] - actualPoint[0];
		tempVector[1] = targetPoint[1] - actualPoint[1];
		distanceToTarget = (float) Math.sqrt(tempVector[0] * tempVector[0]
				+ tempVector[1] * tempVector[1]);

		if (distanceToTarget <= slerpFactor) {
			slerpFactor = 0;
			if ((targetPoint[0] == 0) && (targetPoint[1] == 0)) {
				returnPoint[0] = (position[0]) * -1;
				returnPoint[1] = (position[1]) * -1;
				return false;
			}

			float oldDistanceToTarget = distanceToTarget;
			float[] calcuatedPosition = new float[2];
			// the last slerp action should match the target exactly

			tempVector[0] = targetPoint[0] - actualPoint[0];
			tempVector[1] = targetPoint[1] - actualPoint[1];
			
			oldDistanceToTarget = (float) Math.sqrt(tempVector[0]
					* tempVector[0] + tempVector[1] * tempVector[1]);

			for (int i = 0; i < numberOfIterations; i++) {
				actualPoint = position.clone();

				tempVector[0] = targetPoint[0] - calcuatedPosition[0];
				tempVector[1] = targetPoint[1] - calcuatedPosition[1];
				oldDistanceToTarget = distanceToTarget;
				distanceToTarget = (float) Math.sqrt(tempVector[0]
						* tempVector[0] + tempVector[1] * tempVector[1]);

				// decide, if the last transformation was to much:

				if (distanceToTarget < precision) {
					break;
				}
				if ((distanceToTarget > oldDistanceToTarget) && (i > 1)) {
					break;
				}

				slerpFactor = slerpFactor + precision;
				ComplexNumber complexPoint = new ComplexNumber();
				complexPoint.setValue(actualPoint[0], actualPoint[1]);

				complexPoint = dummyDisk.moebiusTransformation(complexPoint,
						new ComplexNumber(directionVector[0] * slerpFactor,
								directionVector[1] * slerpFactor));
				calcuatedPosition[0] = (float) complexPoint.getRealPart();
				calcuatedPosition[1] = (float) complexPoint.getImaginaryPart();

			}

			returnPoint[0] = directionVector[0] * slerpFactor;
			returnPoint[1] = directionVector[1] * slerpFactor;

			ComplexNumber complexPoint = new ComplexNumber();
			complexPoint.setValue(position[0], position[1]);
			complexPoint = dummyDisk.moebiusTransformation(complexPoint,
					new ComplexNumber(returnPoint[0], returnPoint[1]));
			calcuatedPosition[0] = (float) complexPoint.getRealPart();
			calcuatedPosition[1] = (float) complexPoint.getImaginaryPart();
			return false;
		}

		tempVector[0] = targetPoint[0] - actualPoint[0];
		tempVector[1] = targetPoint[1] - actualPoint[1];
		dLength = (float) Math.sqrt(tempVector[0] * tempVector[0]
				+ tempVector[1] * tempVector[1]);

		directionVector[0] = tempVector[0] / dLength;
		directionVector[1] = tempVector[1] / dLength;

		returnPoint[0] = directionVector[0] * slerpFactor;
		returnPoint[1] = directionVector[1] * slerpFactor;
		time.update();

		return true;

	}
}
