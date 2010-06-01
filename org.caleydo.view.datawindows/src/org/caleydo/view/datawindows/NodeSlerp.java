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
	private float precision = 0.01f;
	private int numberOfIterations = 1000;
	private float[] tempVector;
	private float oldDistanceToTarget;
	private float[] calcuatedPosition;

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
		tempVector = new float[2];
		calcuatedPosition = new float[2];
	}

	public boolean doASlerp(float[] position) {

		actualPoint = position.clone();
		slerpFactor = speed * (float) time.deltaT();

		// the distance to the target:
		tempVector[0] = targetPoint[0] - actualPoint[0];
		tempVector[1] = targetPoint[1] - actualPoint[1];

		distanceToTarget = (float) Math.sqrt(tempVector[0] * tempVector[0]
				+ tempVector[1] * tempVector[1]);

		// if another slerp is longer than the distance to the target, the
		// lenght of the current slerp will be approximated
		if (distanceToTarget <= slerpFactor) {
			slerpFactor = 0;
			if ((targetPoint[0] == 0) && (targetPoint[1] == 0)) {
				returnPoint[0] = position[0] * -1;
				returnPoint[1] = position[1] * -1;
				return false;
			}

			oldDistanceToTarget = distanceToTarget;

			// the last slerp action should match the target exactly
			tempVector[0] = targetPoint[0] - actualPoint[0];
			tempVector[1] = targetPoint[1] - actualPoint[1];

			oldDistanceToTarget = (float) Math.sqrt(tempVector[0]
					* tempVector[0] + tempVector[1] * tempVector[1]);

			// approximating the correct slerp vector
			for (int i = 0; i < numberOfIterations; i++) {
				actualPoint = position.clone();

				tempVector[0] = targetPoint[0] - calcuatedPosition[0];
				tempVector[1] = targetPoint[1] - calcuatedPosition[1];
				oldDistanceToTarget = distanceToTarget;
				distanceToTarget = (float) Math.sqrt(tempVector[0]
						* tempVector[0] + tempVector[1] * tempVector[1]);

				// decide, if the approximation is precise enough
				if (distanceToTarget < precision) {
					break;
				}
				// decide, if the failure of the approximation starts growing
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

		//calculating the differential slerp vector
		returnPoint[0] = directionVector[0] * slerpFactor;
		returnPoint[1] = directionVector[1] * slerpFactor;
		time.update();

		return true;

	}
}
