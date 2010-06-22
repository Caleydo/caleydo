package org.caleydo.view.datawindows;

import java.util.ArrayList;

import org.caleydo.core.data.graph.tree.Tree;

public class PoincareDisk {

	public float radius;
	private Tree<PoincareNode> tree;
	private int treeNodeCounter;
	protected float nodeSize;
	protected float lineWidth;
	protected float[] absolutePosition;
	private PoincareNode centeredNode;
	public float centeredNodeSize = 1;
	private float layoutLenseFactor = 1.45f;
	private boolean calculateRootNodeAngleSwitch = false;

	public PoincareDisk() {
		radius = 1f;
		nodeSize = 0.1f;
		lineWidth = 2;
		absolutePosition = new float[2];
	}

	public Tree<PoincareNode> getTree() {
		return tree;
	}

	public void loadTree(Tree<PoincareNode> tree) {

		this.tree = tree;
		centeredNode = tree.getRoot();
		// applies the tree layout
		moebiusLayoutTree(2);

	}

	// moves a node into the middle of the screen
	public void centerNodeMoebius(PoincareNode node) {

		float[] translation = new float[2];
		translation[0] = node.getPosition()[0] * -1;
		translation[1] = node.getPosition()[1] * -1;

		translateTreeMoebius(translation);

	}

	// translates the tree by a given vector
	public void translateTreeMoebius(float[] translationVector) {

		PoincareNode root = tree.getRoot();
		translateNodeMoebius(root, translationVector);
	}

	// applies a moebiustransformation on an node calls this method for the
	// children
	private boolean translateNodeMoebius(PoincareNode node,
			float[] translationVector) {
		// do the transformation:
		ComplexNumber tempVector = moebiusTransformation(new ComplexNumber(node
				.getPosition()[0], node.getPosition()[1]), new ComplexNumber(
				translationVector[0], translationVector[1]));

		// converting the complex numbers into coordinates
		float[] newPoint = new float[2];
		newPoint[0] = (float) tempVector.getRealPart();
		newPoint[1] = (float) tempVector.getImaginaryPart();

		node.setPosition(newPoint);
		node.setDistanceFromOrigin(this.distanceFromOrigin(newPoint));

		if (tree.getChildren(node) == null) {
			return false;
		}
		ArrayList<PoincareNode> children = tree.getChildren(node);
		int numberOfChildren = children.size();
		// iterating the children of the node
		for (int i = 0; i < numberOfChildren; i++) {
			// recursion step
			translateNodeMoebius(children.get(i), translationVector);
		}
		return true;
	}

	public float[] zoomPoint(float[] point, float intensity) {

		float[] coordinate = new float[2];
		intensity = (1 + intensity) - intensity * distanceFromOrigin(point);

		coordinate[0] = point[0] * intensity;
		coordinate[1] = point[1] * intensity;
		return coordinate;
	}

	// zooms a node by an given intensity and calls this method for the nodes
	// children
	protected boolean zoomNode(PoincareNode parentNode, float intensity) {
		float distance = distanceFromOrigin(parentNode.getPosition());
		parentNode.setDistanceFromOrigin(distance);
		if (tree.getChildren(parentNode) == null) {
			return false;
		}

		ArrayList<PoincareNode> children = tree.getChildren(parentNode);
		int numberOfChildren = children.size();
		// iterating the children
		for (int i = 0; i < numberOfChildren; i++) {
			children.get(i).setZoomedPosition(
					zoomPoint(children.get(i).getPosition(), intensity));
			// recursion step
			zoomNode(children.get(i), intensity);
		}
		return true;
	}

	public void zoomTree(float intensity) {
		PoincareNode root = tree.getRoot();
		float[] projectedPoint = new float[2];
		// start recursive algorithm
		projectedPoint = zoomPoint(root.getPosition(), intensity).clone();
		root.setZoomedPosition(projectedPoint);
		zoomNode(root, intensity);
	}

	// converts an angle to an identity vector
	private float[] angleToCoordinate(float angle) {
		float[] coordinate = new float[2];
		coordinate[0] = (float) Math.sin(angle);
		coordinate[1] = (float) Math.cos(angle);
		return coordinate;
	}

	// returns the distance of a point from the origin
	public float distanceFromOrigin(float[] coordinate) {
		float coordinateLength = coordinate[0] * coordinate[0] + coordinate[1]
				* coordinate[1];
		coordinateLength = (float) Math.sqrt((double) coordinateLength);
		return coordinateLength;
	}

	// returns the distance of two points
	public float distancePoints(float[] point1, float[] point2) {
		float[] relativePosition = new float[2];
		relativePosition[0] = point1[0] - point2[0];
		relativePosition[1] = point1[1] - point2[1];
		return distanceFromOrigin(relativePosition);
	}

	// finds a node in the tree by the nodes comparable value
	public PoincareNode getNodeByCompareableValue(int value) {
		PoincareNode root = tree.getRoot();
		PoincareNode result = compareValueNode(root, value);
		return result;
	}

	// compares the targeted comparable value with a node and calls this method
	// for the nodes children
	private PoincareNode compareValueNode(PoincareNode node, int value) {
		if (node.iComparableValue == value) {
			return node;
		}

		if (tree.getChildren(node) == null) {
			return null;
		}

		ArrayList<PoincareNode> children = tree.getChildren(node);
		int numberOfChildren = children.size();
		PoincareNode tempNode;
		for (int i = 0; i < numberOfChildren; i++) {
			// recursion step
			tempNode = compareValueNode(node.getChildren().get(i), value);
			if (tempNode != null) {
				return tempNode;
			}
		}
		return null;
	}

	public void clearHighlightedNodes() {
		PoincareNode tempNode;
		for (int i = 1; i <= tree.getNumberOfNodes(); i++) {
			tempNode = getNodeByCompareableValue(i);
			if (tempNode != null) {
				getNodeByCompareableValue(i).highLighted = false;
			}

		}
	}

	public void setCenteredNode(PoincareNode centeredNode) {
		this.centeredNode = centeredNode;
	}

	public PoincareNode getCenteredNode() {
		return centeredNode;

	}

	// calculates the moebiustransformation
	public ComplexNumber moebiusTransformation(ComplexNumber point,
			ComplexNumber translation) {

		ComplexNumber num = new ComplexNumber();
		num.setValue(point);
		num = num.add(translation);

		ComplexNumber num2 = new ComplexNumber();
		num2.setValue(translation);
		num2.setValue(num2.getRealPart(), num2.getImaginaryPart() * -1);
		num2 = num2.multiply(point);
		num2 = num2.add(new ComplexNumber(1, 0));
		return num.divide(num2);
	}

	public void moebiusLayoutTree(int mode) {
		// mode 1 = symmetric layout without angle correction marker
		// mode 2 = root connection is on the left side

		PoincareNode root = tree.getRoot();
		float[] nullPosition = new float[2];
		nullPosition[0] = 0;
		nullPosition[1] = 0;
		root.setPosition(nullPosition);
		root.setDistanceFromOrigin(0);
		treeNodeCounter = 1;
		root.iComparableValue = treeNodeCounter;
		root.setChildrenAngleOffset(0);
		if (mode == 2)
			moebiusNodeLayout(root, (float) (Math.PI / 2), (float) Math.PI,
					mode);
		if (mode == 1)
			moebiusNodeLayout(root, 0, 0, mode);
	}

	public void moebiusNodeLayout(PoincareNode parentNode, float angleOffset,
			float angle, int mode) {

		if (tree.getChildren(parentNode) == null) {
			return;
		}

		ArrayList<PoincareNode> children = tree.getChildren(parentNode);
		int numberOfChildren = children.size();
		float splitAngle = angle / (float) (numberOfChildren + 2);
		float absoluteAngle = 0;
		// if the node is root, the node are note competing each other

		if (parentNode.iComparableValue == 1) {
			splitAngle = angle / (float) (numberOfChildren);
			if (numberOfChildren == 1) {
				splitAngle = angle / 2;
			}
			absoluteAngle = angleOffset - angle / 2;
			absoluteAngle = absoluteAngle + splitAngle / 2;
		}
		if (parentNode.iComparableValue != 1) {
			absoluteAngle = angleOffset - angle / 2;
			absoluteAngle = absoluteAngle + splitAngle;
		}

		for (int i = 0; i < numberOfChildren; i++) {

			float[] newPoint = new float[2];
			newPoint[0] = parentNode.getPosition()[0];
			newPoint[1] = parentNode.getPosition()[1];

			float[] relativePoint = new float[2];
			relativePoint = angleToCoordinate(absoluteAngle).clone();
			// converting to complex numbers for the Moebius transformation
			ComplexNumber relativeTargetPoint = new ComplexNumber(
					(double) relativePoint[0] / layoutLenseFactor,
					(double) relativePoint[1] / layoutLenseFactor);

			ComplexNumber startingPoint = new ComplexNumber(newPoint[0],
					newPoint[1]);

			ComplexNumber targetPoint = new ComplexNumber(0, 0);
			targetPoint.setValue(moebiusTransformation(startingPoint,
					relativeTargetPoint));

			// translation of the complex number position into a coordinate
			newPoint[0] = (float) targetPoint.getRealPart();
			newPoint[1] = (float) targetPoint.getImaginaryPart();

			children.get(i).setPosition(newPoint);
			children.get(i).setDistanceFromOrigin(
					this.distanceFromOrigin(newPoint));

			treeNodeCounter++;
			children.get(i).iComparableValue = treeNodeCounter;
			// recursion step:
			moebiusNodeLayout(children.get(i), absoluteAngle, splitAngle, mode);
			absoluteAngle = absoluteAngle + splitAngle;
		}
		return;
	}

	// conversion of a vector into its normed form
	public float[] getEV(float[] vector) {
		if ((vector[0] == 0) && (vector[1] == 0)) {
			float[] returnValue = new float[2];
			returnValue[0] = 1;
			returnValue[1] = 0;
			return returnValue;
		}

		float[] eVector = new float[2];
		eVector = vector.clone();
		float length = distanceFromOrigin(vector);
		eVector[0] = vector[0] / length;
		eVector[1] = vector[1] / length;
		return eVector;

	}

	// converts a real distance into a distance on the disk
	public float[] getMetric(float[] position, float length) {

		ComplexNumber targetPoint = new ComplexNumber(0, 0);

		float[] eVectorPos = getEV(position);
		ComplexNumber relativeTargetPoint = new ComplexNumber(eVectorPos[0]
				* -length, eVectorPos[1] * -length);
		ComplexNumber startingPoint = new ComplexNumber(position[0],
				position[1]);
		targetPoint.setValue(moebiusTransformation(startingPoint,
				relativeTargetPoint));

		float[] returnValue = new float[2];
		returnValue[0] = (float) targetPoint.getRealPart();
		returnValue[1] = (float) targetPoint.getImaginaryPart();

		float distance = distancePoints(position, returnValue);
		returnValue[0] = distance;
		returnValue[1] = distance;
		return returnValue;
	}

	// can used to select a node without a picking manager
	// argument area specifies the distance from a node which will be tolerated
	public PoincareNode findNodeByCoordinate(float[] coordinate, float area) {

		int counter = 1;
		float distance;
		float bestDistance = 10;
		PoincareNode bestNode = null;

		if (area != 0) {
			while (this.getNodeByCompareableValue(counter) != null) {
				// calculating the distance to the coordinate:
				distance = distancePoints(getNodeByCompareableValue(counter)
						.getZoomedPosition(), coordinate);
				if (distance <= area) {
					if (distance <= bestDistance) {
						bestDistance = distance;
						bestNode = getNodeByCompareableValue(counter);
					}
				}
				counter++;
			}
		} else {
			float nodeRadius;
			while (this.getNodeByCompareableValue(counter) != null) {

				distance = distancePoints(getNodeByCompareableValue(counter)
						.getZoomedPosition(), coordinate);

				float[] size = getMetric(getNodeByCompareableValue(counter)
						.getPosition(), nodeSize);

				nodeRadius = this.distanceFromOrigin(size);

				if (distance <= nodeRadius) {
					bestDistance = distance;
					bestNode = getNodeByCompareableValue(counter);
					break;
				}
				counter++;
			}
		}
		return bestNode;
	}

	// the return value is normed on the unit disk
	// the unit of the intend is procent of the resulting size
	public float[] findOptimalCenterNodeSize(PoincareNode node, float intend) {
		float currentDistance = 1;
		PoincareNode closestNode = null;
		float[] returnValue = new float[2];
		// iterating all nodes by their comparable number
		for (int i = 1;; i++) {
			if (this.getNodeByCompareableValue(i) == null) {
				break;
			}

			if (this.distanceFromOrigin(this.getNodeByCompareableValue(i)
					.getZoomedPosition()) < currentDistance) {
				if (node != this.getNodeByCompareableValue(i)) {
					currentDistance = distanceFromOrigin(this
							.getNodeByCompareableValue(i).getZoomedPosition());

					closestNode = getNodeByCompareableValue(i);
					// System.out.println("distance: "+currentDistance);

				}
			}
		}
		// System.out.println("entgültig "+closestNode.getPosition()[0]+" "+closestNode.getPosition()[1]);
		currentDistance = currentDistance
				- this.getMetric(closestNode.getPosition(),
						this.nodeSize * 1.4142f)[0] - intend;

		returnValue[0] = currentDistance * 0.7071f;
		returnValue[1] = currentDistance * 0.7071f;

		return returnValue;
	}

	// start the recursive disk rotation algrithm
	public void rotateDisk(float angle) {
		PoincareNode root = tree.getRoot();
		rotateNode(root, angle);
	}

	// rotates a node by a given angle around the origin, and recursively calls
	// it self for the children of the node
	public void rotateNode(PoincareNode parentNode, float angle) {

		float[] oldPosition = new float[2];
		oldPosition = parentNode.getPosition();
		float length = this.distanceFromOrigin(oldPosition);
		float oldAngle;
		float[] oldPositionEV = this.getEV(oldPosition);
		oldAngle = (float) Math.atan2(oldPositionEV[1], oldPositionEV[0]);
		float newAngle = oldAngle + angle;

		float[] newPosition = new float[2];
		newPosition[0] = length * (float) Math.cos(newAngle);
		newPosition[1] = length * (float) Math.sin(newAngle);

		parentNode.setPosition(newPosition);

		if (tree.getChildren(parentNode) == null) {
			return;
		}

		ArrayList<PoincareNode> children = tree.getChildren(parentNode);
		int numberOfChildren = children.size();
		for (int i = 0; i < numberOfChildren; i++) {
			rotateNode(children.get(i), angle);
		}
	}

	// the goal of this method is to calculate an angle, which rotates the disk
	// so that the root node is on the left side of the next centered node
	// this method can be called before and after a node translation
	public float calculateCorrectDiskRotation(PoincareNode currentNode) {

		if (currentNode != null) {
			if (currentNode.iComparableValue != 1) {

				calculateRootNodeAngleSwitch = true;

				float[] currentPosition = new float[2];
				currentPosition = currentNode.getPosition().clone();
				float[] parentPosition;
				parentPosition = currentNode.getParent().getPosition().clone();

				// the vector from the next current node to the origin
				float[] relativePosition = new float[2];
				relativePosition[0] = -currentPosition[0];
				relativePosition[1] = -currentPosition[1];

				currentPosition[0] = 0;
				currentPosition[1] = 0;

				// performing a moebiustransformation to get the correct
				// position of the next root node
				ComplexNumber moebiusTransformation = new ComplexNumber();
				ComplexNumber complexParentPosition = new ComplexNumber();
				moebiusTransformation.setValue((double) relativePosition[0],
						(double) relativePosition[1]);
				complexParentPosition.setValue((double) parentPosition[0],
						(double) parentPosition[1]);
				complexParentPosition = this.moebiusTransformation(
						complexParentPosition, moebiusTransformation);

				// calculating the new vector from the current node to its root
				relativePosition[0] = (float) complexParentPosition
						.getRealPart();
				relativePosition[1] = (float) complexParentPosition
						.getImaginaryPart();

				float[] eV = getEV(relativePosition);
				float angle = (float) Math.atan2(eV[1], eV[0]);

				if (Math.abs(-angle + Math.PI) < Math.PI) {
					return -angle + (float) Math.PI;
				} else {
					return -angle - (float) Math.PI;
				}

			} else {
				float[] currentPosition = new float[2];
				currentPosition = currentNode.getPosition().clone();
				float[] childPosition;
				// the first child of the root
				if (this.calculateRootNodeAngleSwitch == true) {
					if (currentNode.getChildren() != null) {

						childPosition = currentNode.getChildren().get(0)
								.getPosition();

						float[] relativePosition = new float[2];
						relativePosition[0] = -currentPosition[0];
						relativePosition[1] = -currentPosition[1];

						currentPosition[0] = 0;
						currentPosition[1] = 0;

						ComplexNumber moebiusTransformation = new ComplexNumber();
						ComplexNumber complexParentPosition = new ComplexNumber();

						moebiusTransformation.setValue(
								(double) relativePosition[0],
								(double) relativePosition[1]);
						complexParentPosition.setValue(
								(double) childPosition[0],
								(double) childPosition[1]);

						complexParentPosition = this.moebiusTransformation(
								complexParentPosition, moebiusTransformation);

						relativePosition[0] = (float) complexParentPosition
								.getRealPart();
						relativePosition[1] = (float) complexParentPosition
								.getImaginaryPart();

						float[] eV = getEV(relativePosition);
						float angle = (float) Math.atan2(eV[1], eV[0]);

						float angleOffset = ((float) Math.PI / (float) currentNode
								.getChildren().size()) / 2;
						angle = angle + angleOffset + (float) Math.PI / 2;
						if (Math.abs(-angle + Math.PI) < Math.PI) {
							return -angle + (float) Math.PI;
						} else {
							return -angle - (float) Math.PI;
						}
					}
				}
			}
		}
		return 0;
	}
}