package org.caleydo.view.datawindows;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import org.caleydo.core.data.graph.tree.Tree;

public class PoincareDisk {

	public double radius;
	private Tree<PoincareNode> tree;
	// private boolean dirtyTree = true;
	private int treeNodeCounter;
	protected double nodeSize;
	protected double lineWidth;
	protected Point2D.Double absolutePosition;
	private PoincareNode centeredNode;
	private double layoutLenseFactor = 1.45;

	public PoincareDisk() {
		radius = 1;
		nodeSize = 0.4;
		lineWidth = 0.2;
		absolutePosition = new Point2D.Double(0, 0);
	}

	public Tree<PoincareNode> getTree() {
		return tree;

	}

	public void loadTree() {
		// creating a tree for testing
		tree = new Tree<PoincareNode>();

		PoincareNode node = new PoincareNode(tree, "Root", 1);
		centeredNode = node;

		tree.setRootNode(node);
		tree.addChild(node, new PoincareNode(tree, "Child1 l1", 3));
		tree.addChild(node, new PoincareNode(tree, "Child2 l1", 3));
		tree.addChild(node, new PoincareNode(tree, "Child1 l1", 3));
		tree.addChild(node, new PoincareNode(tree, "Child2 l1", 3));
		tree.addChild(node, new PoincareNode(tree, "Child1 l1", 3));

		int iCount = 3344;
		for (PoincareNode tempNode : tree.getChildren(node)) {

			PoincareNode tempNode22 = new PoincareNode(tree, "Child6 l1",
					iCount--);
			tree.addChild(tempNode, tempNode22);

			tree.addChild(tempNode22, new PoincareNode(tree, "Child7 l1",
					iCount--));
			tree.addChild(tempNode22, new PoincareNode(tree, "Child7 l1",
					iCount--));

			PoincareNode tempNode433 = new PoincareNode(tree, "Child6 l1",
					iCount--);
			tree.addChild(tempNode22, tempNode433);
			tree.addChild(tempNode433, new PoincareNode(tree, "Child7 l1",
					iCount--));
			tree.addChild(tempNode433, new PoincareNode(tree, "Child7 l1",
					iCount--));

			PoincareNode tempNode33 = new PoincareNode(tree, "Child6 l1",
					iCount--);
			tree.addChild(tempNode22, tempNode33);
			tree.addChild(tempNode33, new PoincareNode(tree, "Child7 l1",
					iCount--));
			tree.addChild(tempNode33, new PoincareNode(tree, "Child7 l1",
					iCount--));

			tree.addChild(tempNode, new PoincareNode(tree, "Child3 l1",
					iCount--));
			tree.addChild(tempNode, new PoincareNode(tree, "Child4 l1",
					iCount--));
			tree.addChild(tempNode, new PoincareNode(tree, "Child3 l1",
					iCount--));
			tree.addChild(tempNode, new PoincareNode(tree, "Child4 l1",
					iCount--));
			tree.addChild(tempNode, new PoincareNode(tree, "Child3 l1",
					iCount--));
			tree.addChild(tempNode, new PoincareNode(tree, "Child4 l1",
					iCount--));
			tree.addChild(tempNode, new PoincareNode(tree, "Child3 l1",
					iCount--));

			PoincareNode tempNode2 = new PoincareNode(tree, "Child6 l1",
					iCount--);
			tree.addChild(tempNode, tempNode2);

			tree.addChild(tempNode2, new PoincareNode(tree, "Child7 l1",
					iCount--));
			tree.addChild(tempNode2, new PoincareNode(tree, "Child7 l1",
					iCount--));
			tree.addChild(tempNode2, new PoincareNode(tree, "Child7 l1",
					iCount--));
			PoincareNode tempNode3 = new PoincareNode(tree, "Child6 l1",
					iCount--);
			tree.addChild(tempNode2, tempNode3);
			tree.addChild(tempNode3, new PoincareNode(tree, "Child7 l1",
					iCount--));
			tree.addChild(tempNode3, new PoincareNode(tree, "Child7 l1",
					iCount--));
			tree.addChild(tempNode3, new PoincareNode(tree, "Child7 l1",
					iCount--));
			tree.addChild(tempNode3, new PoincareNode(tree, "Child7 l1",
					iCount--));
			tree.addChild(tempNode3, new PoincareNode(tree, "Child7 l1",
					iCount--));
			tree.addChild(tempNode3, new PoincareNode(tree, "Child7 l1",
					iCount--));
			PoincareNode tempNode34 = new PoincareNode(tree, "Child6 l1",
					iCount--);
			tree.addChild(tempNode3, tempNode34);
			tree.addChild(tempNode34, new PoincareNode(tree, "Child7 l1",
					iCount--));
			tree.addChild(tempNode34, new PoincareNode(tree, "Child7 l1",
					iCount--));
			PoincareNode tempNode344 = new PoincareNode(tree, "Child6 l1",
					iCount--);
			tree.addChild(tempNode34, tempNode344);
			tree.addChild(tempNode344, new PoincareNode(tree, "Child7 l1",
					iCount--));
			tree.addChild(tempNode344, new PoincareNode(tree, "Child7 l1",
					iCount--));
			PoincareNode tempNode3444 = new PoincareNode(tree, "Child6 l1",
					iCount--);
			tree.addChild(tempNode344, tempNode3444);
			tree.addChild(tempNode3444, new PoincareNode(tree, "Child7 l1",
					iCount--));
			tree.addChild(tempNode3444, new PoincareNode(tree, "Child7 l1",
					iCount--));
			PoincareNode tempNode34444 = new PoincareNode(tree, "Child6 l1",
					iCount--);
			tree.addChild(tempNode3444, tempNode34444);
			tree.addChild(tempNode34444, new PoincareNode(tree, "Child7 l1",
					iCount--));
			tree.addChild(tempNode34444, new PoincareNode(tree, "Child7 l1",
					iCount--));
			PoincareNode tempNode344444 = new PoincareNode(tree, "Child6 l1",
					iCount--);
			tree.addChild(tempNode34444, tempNode344444);
			tree.addChild(tempNode344444, new PoincareNode(tree, "Child7 l1",
					iCount--));
			tree.addChild(tempNode344444, new PoincareNode(tree, "Child7 l1",
					iCount--));
		}

		// layoutTree();
		moebiusLayoutTree();
		// scaleTree(treeScaleFactor, 1);
		// projectTree();

	}

	// public void centerNode(PoincareNode node) {
	// translateTree(new Point2D.Double(node.getPosition().getX() * -1, node
	// .getPosition().getY()
	// * -1));
	//
	// }

	public void centerNodeMoebius(PoincareNode node) {
		translateTreeMoebius(new Point2D.Double(node.getPosition().getX() * -1,
				node.getPosition().getY() * -1));

	}

	public void translateTreeMoebius(Point2D.Double translationVector) {

		PoincareNode root = tree.getRoot();

		double distance = this.distanceFromOrigin(translationVector);
		distance = (double) Math.round(distance + 0.5);

		Point2D.Double directionVector = new Point2D.Double(translationVector
				.getX()
				/ distance, translationVector.getY() / distance);
		// make more steps, because the moebius transformation can only be
		// applied for
		// distance < 1
		for (int i = 0; i < (int) distance; i++) {
			// start the recursive algorithm
			translateNodeMoebius(root, directionVector);
		}
		// projectTree();
	}

	private boolean translateNodeMoebius(PoincareNode node,
			Point2D.Double translationVector) {
		// do the transformation:
		ComplexNumber tempVector = moebiusTransformation(new ComplexNumber(node
				.getPosition().getX(), node.getPosition().getY()),
				new ComplexNumber(translationVector.getX(), translationVector
						.getY()));
		Point2D.Double newPoint = new Point2D.Double(tempVector.getRealPart(),
				tempVector.getImaginaryPart());
		node.setPosition(newPoint);
		node.setDistanceFromOrigin(this.distanceFromOrigin(newPoint));

		if (tree.getChildren(node) == null) {
			return false;
		}
		ArrayList<PoincareNode> children = tree.getChildren(node);
		int numberOfChildren = children.size();

		for (int i = 0; i < numberOfChildren; i++) {
			// recursion step
			translateNodeMoebius(children.get(i), translationVector);
		}
		return true;
	}

	// public void translateTree(Point2D.Double translationVector) {
	// PoincareNode root = tree.getRoot();
	// translateNode(root, translationVector);
	// projectTree();
	// }

	// private boolean translateNode(PoincareNode node,
	// Point2D.Double translationVector) {
	// absolutePosition.setLocation(absolutePosition.getX()
	// + translationVector.getX(), absolutePosition.getY()
	// + translationVector.getY());
	//
	// node.setPosition(new Point2D.Double(node.getPosition().getX()
	// + translationVector.getX(), node.getPosition().getY()
	// + translationVector.getY()));
	//
	// if (tree.getChildren(node) == null) {
	// return false;
	// }
	// ArrayList<PoincareNode> children = tree.getChildren(node);
	// int numberOfChildren = children.size();
	//
	// for (int i = 0; i < numberOfChildren; i++) {
	//
	// translateNode(children.get(i), translationVector);
	// }
	// return true;
	// }

	// public void scaleTree(double factor, int mode) {
	// absoluteScalation = absoluteScalation * factor;
	// if (mode == 1) {
	// nodeSize = nodeSize * factor;
	// }
	//
	// PoincareNode root = tree.getRoot();
	// root.setPosition(scalePoint(root.getPosition(), factor));
	// scaleNode(root, factor);
	// projectTree();
	// }
	//
	// private boolean scaleNode(PoincareNode node, double factor) {
	// if (tree.getChildren(node) == null) {
	// return false;
	// }
	// ArrayList<PoincareNode> children = tree.getChildren(node);
	// int numberOfChildren = children.size();
	// for (int i = 0; i < numberOfChildren; i++) {
	// scalePoint(children.get(i).getPosition(), factor);
	// scaleNode(children.get(i), factor);
	// }
	// return true;
	// }

	// private Point2D.Double scalePoint(Point2D.Double point, double factor) {
	// Point2D.Double newPoint = new Point2D.Double(point.getX() * factor,
	// point.getY() * factor);
	// return newPoint;
	// }

	// public Point2D.Double projectPoint(Point2D.Double point) {
	// Point2D.Double coordinate = new Point2D.Double();
	// coordinate.setLocation(point);
	// double coordinateLength = coordinate.getX() * coordinate.getX()
	// + coordinate.getY() * coordinate.getY();
	//		
	//		
	// // coordinateLength = Math.sqrt(coordinateLength);
	//	
	//		
	//		
	// //for speedup, the radius of the disk is everytime 1
	// //double radiussquare = radius * radius;
	// //double projectionFactor =1;
	// // double projectionFactor =2-coordinateLength;// 2 / (1 +
	// coordinateLength);
	//
	// double projectionFactor = 2 / (1 + coordinateLength);
	// //double projectionFactor =coordinateLength*coordinateLength;
	//		
	// //System.out.println(coordinateLength);
	// //projectionFactor = projectionFactor + (1-coordinateLength);
	//		
	// //todo find the real factor
	// coordinate.setLocation(coordinate.getX() * projectionFactor ,
	// coordinate.getY() * projectionFactor );
	//		
	//		
	//		
	// return coordinate;
	// }
	//
	// protected boolean projectNode(PoincareNode parentNode) {
	// double distance = distanceFromOrigin(parentNode.getPosition());
	// parentNode.setDistanceFromOrigin(distance);
	// if (tree.getChildren(parentNode) == null) {
	// return false;
	// }
	//
	// ArrayList<PoincareNode> children = tree.getChildren(parentNode);
	// int numberOfChildren = children.size();
	// for (int i = 0; i < numberOfChildren; i++) {
	// children.get(i).setProjectedPosition(
	// projectPoint(children.get(i).getPosition()));
	// // recursion step
	// projectNode(children.get(i));
	// }
	// return true;
	//
	// }
	//
	// public void projectTree() {
	// if (dirtyTree) {
	// PoincareNode root = tree.getRoot();
	// Point2D.Double projectedPoint = new Point2D.Double(0, 0);
	// //start recursive algorithm
	// projectedPoint = projectPoint(root.getPosition());
	// root.setProjectedPosition(projectedPoint);
	// projectNode(root);
	// }
	// }

	public void calculateLinePoints() {

	}

	// public void layoutTree() {
	// System.out.println("layoutTree Called");
	// PoincareNode root = tree.getRoot();
	// root.setPosition(new Point2D.Double(0, 0));
	// root.setDistanceFromOrigin(0);
	// treeNodeCounter = 1;
	// root.iComparableValue = treeNodeCounter;
	// //start recursive algorithm:
	// layoutNode(root, 0, 2 * Math.PI);
	// }

	// The angleOffset is the starting angle of the available area
	// angle is the length of the area
	// All angles are in radiant
	// private boolean layoutNode(PoincareNode parentNode, double angleOffset,
	// double angle) {;
	// if (tree.getChildren(parentNode) == null) {
	// return false;
	// }
	//
	// ArrayList<PoincareNode> children = tree.getChildren(parentNode);
	// int numberOfChildren = children.size();
	// double splitAngle = angle / (double) (numberOfChildren +2);
	//
	// //if the node is root, the node are note competing each other
	// if (parentNode.iComparableValue == 1) {
	// splitAngle = angle / (double) (numberOfChildren);
	// }
	//
	// double absoluteAngle = angleOffset - angle / 2;
	// double length = 0.3 + Math.log((double) (children.size()));
	// if (children.size() >= 2) {
	// length = (nodeSize * 3) / (Math.sin(splitAngle / 2));
	// }
	//
	// Point2D.Double relativePoint = new Point2D.Double(0, 0);
	// System.out.println("number of children: " + numberOfChildren);
	// for (int i = 0; i < numberOfChildren; i++) {
	// absoluteAngle = absoluteAngle + splitAngle;
	//
	// Point2D.Double newPoint = new Point2D.Double(parentNode
	// .getPosition().getX(), parentNode.getPosition().getY());
	//
	// relativePoint = angleToCoordinate(absoluteAngle);
	// newPoint.setLocation(newPoint.getX() + relativePoint.getX()
	// * length, newPoint.getY() + relativePoint.getY() * length);
	//
	// children.get(i).setPosition(
	// new Point2D.Double(newPoint.getX(), newPoint.getY()));
	// System.out.println("Angle: " + absoluteAngle * 180 / Math.PI);
	// System.out.println("SplitAngle: " + splitAngle * 180 / Math.PI);
	// System.out.println("Node " + children.get(i).nodeName
	// + " is set to: " + children.get(i).getPosition().getX()
	// + "|" + children.get(i).getPosition().getY());
	// // recursion step:
	// treeNodeCounter++;
	// children.get(i).iComparableValue = treeNodeCounter;
	//
	// layoutNode(children.get(i), absoluteAngle, splitAngle);
	//
	// }
	// return true;
	// }

	private Point2D.Double angleToCoordinate(double angle) {
		Point2D.Double coordinate = new Point2D.Double(0, 0);
		coordinate.setLocation(Math.sin(angle), Math.cos(angle));
		return coordinate;
	}

	public double distanceFromOrigin(Point2D.Double coordinate) {
		double coordinateLength = coordinate.getX() * coordinate.getX()
				+ coordinate.getY() * coordinate.getY();
		coordinateLength = Math.sqrt(coordinateLength);
		return coordinateLength;
	}

	public double distancePoints(Point2D.Double point1, Point2D.Double point2) {
		Point2D.Double relativePosition = new Point2D.Double();
		relativePosition.setLocation(point1.getX() - point2.getX(), point1
				.getY()
				- point2.getY());
		return distanceFromOrigin(relativePosition);
	}

	public PoincareNode getNodeByCompareableValue(int value) {
		PoincareNode root = tree.getRoot();

		PoincareNode result = compareValueNode(root, value);
		return result;
	}

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

			tempNode = compareValueNode(node.getChildren().get(i), value);
			if (tempNode != null) {
				return tempNode;
			}

		}
		return null;
	}

	public void clearHighlightedNodes() {
		PoincareNode tempNode;
		System.out.println("call clear" + tree.getNumberOfNodes());
		for (int i = 1; i <= tree.getNumberOfNodes(); i++) {
			System.out.println("untersuche: " + i);
			tempNode = getNodeByCompareableValue(i);
			if (tempNode != null) {
				getNodeByCompareableValue(i).highLighted = false;
				System.out.println("knoten: " + tempNode.nodeName);
			}

		}
	}

	public void setCenteredNode(PoincareNode centeredNode) {
		this.centeredNode = centeredNode;
	}

	public PoincareNode getCenteredNode() {
		return centeredNode;

	}

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

	public void moebiusLayoutTree() {

		PoincareNode root = tree.getRoot();
		root.setPosition(new Point2D.Double(0, 0));
		root.setDistanceFromOrigin(0);
		treeNodeCounter = 1;
		root.iComparableValue = treeNodeCounter;
		moebiusNodeLayout(root, 0, 2 * Math.PI);
	}

	public void moebiusNodeLayout(PoincareNode parentNode, double angleOffset,
			double angle) {

		if (tree.getChildren(parentNode) == null) {
			return;
		}

		ArrayList<PoincareNode> children = tree.getChildren(parentNode);
		int numberOfChildren = children.size();
		double splitAngle = angle / (double) (numberOfChildren + 2);

		// if the node is root, the node are note competing each other
		if (parentNode.iComparableValue == 1) {
			splitAngle = angle / (double) (numberOfChildren);
		}

		double absoluteAngle = angleOffset - angle / 2;
		for (int i = 0; i < numberOfChildren; i++) {
			absoluteAngle = absoluteAngle + splitAngle;

			Point2D.Double newPoint = new Point2D.Double(parentNode
					.getPosition().getX(), parentNode.getPosition().getY());

			Point2D.Double relativePoint = new Point2D.Double(0, 0);
			relativePoint.setLocation(angleToCoordinate(absoluteAngle));

			// double length = 1 + Math.log((double) (children.size()));
			//			
			// if (children.size() > 2) {
			// //length = (nodeSize * 3) / (Math.sin(splitAngle / 2));
			// layoutLenseFactor = 0.3 + Math.log((double) children.size());
			//				
			// }
			//			

			// complex numbers for the moebius transformation
			ComplexNumber relativeTargetPoint = new ComplexNumber(relativePoint
					.getX()
					/ layoutLenseFactor, relativePoint.getY()
					/ layoutLenseFactor);

			ComplexNumber startingPoint = new ComplexNumber(newPoint.getX(),
					newPoint.getY());

			ComplexNumber targetPoint = new ComplexNumber(0, 0);

			targetPoint.setValue(moebiusTransformation(startingPoint,
					relativeTargetPoint));
			// alternative version:

			// targetPoint=targetPoint.subtract(startingPoint);
			//			
			// System.out.println("before multiply"+targetPoint.getImaginaryPart()+"|"+targetPoint.getRealPart());
			//			
			// targetPoint=targetPoint.multiply(new ComplexNumber(0.4,0));
			//			
			// System.out.println("after multiply"+targetPoint.getImaginaryPart()+"|"+targetPoint.getRealPart());
			//
			// targetPoint=targetPoint.add(startingPoint);
			//			

			// translation of the complex number position into a coordinate
			newPoint = new Point2D.Double(targetPoint.getRealPart(),
					targetPoint.getImaginaryPart());

			children.get(i).setPosition(newPoint);
			children.get(i).setDistanceFromOrigin(
					this.distanceFromOrigin(newPoint));

			treeNodeCounter++;
			children.get(i).iComparableValue = treeNodeCounter;
			// recursion step:
			moebiusNodeLayout(children.get(i), absoluteAngle, splitAngle);
		}
		return;

	}

	public Point2D.Double getEV(Point2D.Double vector) {
		if ((vector.getX() == 0) && (vector.getY() == 0)) {
			return new Point2D.Double(1, 0);
		}

		Point2D.Double eVector = new Point2D.Double();
		eVector.setLocation(vector);
		double length = distanceFromOrigin(vector);
		eVector.setLocation(vector.getX() / length, vector.getY() / length);

		return eVector;

	}

	// this method converts a real distance into a projected distance
	public double getMetric(Point2D.Double position, double length) {

		// double modLength=distanceFromOrigin(position);
		// normalizing the distance:
		// double x=modLength/1.414213562;

		// Point2D.Double newPoint = new Point2D.Double(x,x);

		// Point2D.Double relativePoint = new Point2D.Double(length,length);

		ComplexNumber targetPoint = new ComplexNumber(0, 0);

		Point2D.Double eVectorPos = getEV(position);

		// System.out.println("position:"+position.getX()+"|"+position.getY());
		// System.out.println("ev:"+eVectorPos.getX()+"|"+eVectorPos.getY());
		// System.out.println("length:"+length);
		// System.out.println("ev*length:"+eVectorPos.getX()*length+"|"+eVectorPos.getY()*length);

		ComplexNumber relativeTargetPoint = new ComplexNumber(eVectorPos.getX()
				* length, eVectorPos.getY() * length);

		// ComplexNumber relativeTargetPoint = new ComplexNumber(length,
		// length);

		ComplexNumber startingPoint = new ComplexNumber(position.getX(),
				position.getY());

		targetPoint.setValue(moebiusTransformation(startingPoint,
				relativeTargetPoint));

		// System.out.println(distancePoints(newPoint,new
		// Point2D.Double(targetPoint.getRealPart(),targetPoint.getImaginaryPart())));

		// pointA=this.projectPoint(pointA);
		// pointB=this.projectPoint(pointB);

		return distancePoints(position, new Point2D.Double(targetPoint
				.getRealPart(), targetPoint.getImaginaryPart()));

	}

	// this method is used to select a node without a picking manager
	// argument area specifies the distance from a node which will be tolerated
	public PoincareNode findNodeByCoordinate(Point2D.Double coordinate,
			double area) {

		int counter = 1;
		double distance;
		double bestDistance = 10;
		PoincareNode bestNode = null;

		if (area != 0) {
			while (this.getNodeByCompareableValue(counter) != null) {
				
				// calculating the distance to the coordinate:
				distance = distancePoints(getNodeByCompareableValue(counter)
						.getPosition(), coordinate);
		
				if (distance <= area) {
					if (distance <= bestDistance) {
				
						bestDistance = distance;
						bestNode = getNodeByCompareableValue(counter);
					}
				}
				counter++;
			}
		} else {
			double nodeRadius;
			while (this.getNodeByCompareableValue(counter) != null) {
				
				
				
				distance = distancePoints(getNodeByCompareableValue(counter)
						.getPosition(), coordinate);
				
				nodeRadius = getMetric(getNodeByCompareableValue(counter)
						.getPosition(), nodeSize);
		
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

}
