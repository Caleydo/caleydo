package org.caleydo.view.datawindows;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import org.caleydo.core.data.graph.tree.Tree;

public class PoincareDisk {

	public double radius;
	private Tree<PoincareNode> tree;
	private double treeScaleFactor = 1;
	private boolean dirtyTree = true;
	private int treeNodeCounter;
	protected double nodeSize;
	protected double lineWidth;
	protected double absoluteScalation;
	protected Point2D.Double absolutePosition;

	public PoincareDisk(double diskRadius) {
		radius = diskRadius;
		nodeSize = 0.1;
		lineWidth = 0.01;
		absoluteScalation = 1;
		absolutePosition = new Point2D.Double(0, 0);
	}

	public Tree<PoincareNode> getTree() {
		return tree;

	}

	public void loadTree() {
		// creating a tree for testing
		System.out.println("loadTree Called");

		tree = new Tree<PoincareNode>();

		PoincareNode node = new PoincareNode(tree, "Root", 1);

		tree.setRootNode(node);
		tree.addChild(node, new PoincareNode(tree, "Child1 l1", 3));
		tree.addChild(node, new PoincareNode(tree, "Child2 l1", 3));
		tree.addChild(node, new PoincareNode(tree, "Child1 l1", 3));
		tree.addChild(node, new PoincareNode(tree, "Child2 l1", 3));
		tree.addChild(node, new PoincareNode(tree, "Child1 l1", 3));
		tree.addChild(node, new PoincareNode(tree, "Child2 l1", 3));
		tree.addChild(node, new PoincareNode(tree, "Child1 l1", 3));
		tree.addChild(node, new PoincareNode(tree, "Child2 l1", 3));
		tree.addChild(node, new PoincareNode(tree, "Child1 l1", 3));
		tree.addChild(node, new PoincareNode(tree, "Child2 l1", 3));
		int iCount = 344;
		for (PoincareNode tempNode : tree.getChildren(node)) {
			tree.addChild(tempNode, new PoincareNode(tree, "Child3 l1",
					iCount--));
			tree.addChild(tempNode, new PoincareNode(tree, "Child4 l1",
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
		}

		layoutTree();
		scaleTree(treeScaleFactor,1);
		projectTree();

	}
	
	

	public void centerNode(PoincareNode node) {
		translateTree(new Point2D.Double(node.getPosition().getX() * -1, node
				.getPosition().getY()
				* -1));

	}

	public void translateTree(Point2D.Double translationVector) {
		System.out.println("transform tree;"+translationVector.getX()+"|"+translationVector.getY());
		PoincareNode root = tree.getRoot();
		translateNode(root, translationVector);
		projectTree();
	}

	private boolean translateNode(PoincareNode node,
			Point2D.Double translationVector) {
		absolutePosition.setLocation(absolutePosition.getX()
				+ translationVector.getX(), absolutePosition.getY()
				+ translationVector.getY());

		node.setPosition(new Point2D.Double(node.getPosition().getX()
				+ translationVector.getX(), node.getPosition().getY()
				+ translationVector.getY()));

		if (tree.getChildren(node) == null) {
			return false;
		}

		ArrayList<PoincareNode> children = tree.getChildren(node);
		int numberOfChildren = children.size();

		for (int i = 0; i < numberOfChildren; i++) {

			translateNode(children.get(i), translationVector);
		}
		return true;
	}

	public void scaleTree(double factor,int mode) {
		absoluteScalation = absoluteScalation * factor;
        if (mode == 1){
		nodeSize = nodeSize * factor;
        }
        
		PoincareNode root = tree.getRoot();
		root.setPosition(scalePoint(root.getPosition(), factor));
		scaleNode(root, factor);
		projectTree();
	}

	private boolean scaleNode(PoincareNode node, double factor) {
		if (tree.getChildren(node) == null) {
			return false;
		}
		// System.out.println("scaliere Knoten: " + node.nodeName);
		ArrayList<PoincareNode> children = tree.getChildren(node);
		int numberOfChildren = children.size();
		for (int i = 0; i < numberOfChildren; i++) {
			children.get(i).setPosition(
					scalePoint(children.get(i).getPosition(), factor));
			// recursion step
			scaleNode(children.get(i), factor);
		}
		return true;
	}

	private Point2D.Double scalePoint(Point2D.Double point, double factor) {
		Point2D.Double newPoint = new Point2D.Double(point.getX() * factor,
				point.getY() * factor);
		return newPoint;
	}

	public Point2D.Double projectPoint(Point2D.Double point) {
		// radius = 2;
		Point2D.Double coordinate = new Point2D.Double();
		coordinate.setLocation(point);
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

	protected boolean projectNode(PoincareNode parentNode) {
		double distance = distanceFromOrigin(parentNode.getPosition());

		parentNode.setDistanceFromOrigin(distance);

		if (tree.getChildren(parentNode) == null) {
			return false;
		}

		ArrayList<PoincareNode> children = tree.getChildren(parentNode);
		int numberOfChildren = children.size();
		for (int i = 0; i < numberOfChildren; i++) {
			children.get(i).setProjectedPosition(
					projectPoint(children.get(i).getPosition()));

			// recursion step
			projectNode(children.get(i));
		}
		for (int i = 0; i < numberOfChildren; i++) {
			//System.out.println("Node projziert auf: "
					//+ children.get(i).getProjectedPosition().getX() + "|"
				//	+ children.get(i).getProjectedPosition().getY());
		}
		return true;

	}

	public void projectTree() {
		if (dirtyTree) {
			PoincareNode root = tree.getRoot();
			Point2D.Double projectedPoint = new Point2D.Double(0, 0);
			projectedPoint = projectPoint(root.getPosition());
			root.setProjectedPosition(projectedPoint);
			projectNode(root);
		}
	}

	public void calculateLinePoints() {

	}

	public void layoutTree() {
		System.out.println("layoutTree Called");
		PoincareNode root = tree.getRoot();
		root.setPosition(new Point2D.Double(0, 0));
		root.setDistanceFromOrigin(0);
		treeNodeCounter = 1;
		root.iComparableValue = treeNodeCounter;
		layoutNode(root, 0, 2 * Math.PI);

	}

	// The angleOffset is the starting angle of the available area
	// angle is the length of the area
	// All angles are in radiant
	private boolean layoutNode(PoincareNode parentNode, double angleOffset,
			double angle) {
		// System.out.println("layoutNode " + parentNode.nodeName + " Called");

		if (tree.getChildren(parentNode) == null) {
			// System.out.println("no children");
			return false;
		}

		ArrayList<PoincareNode> children = tree.getChildren(parentNode);
		int numberOfChildren = children.size();
		double splitAngle = angle / (double) (numberOfChildren + 2);

		if (parentNode.iComparableValue == 1) {
			splitAngle = angle / (double) (numberOfChildren);
		}

		double absoluteAngle = angleOffset - angle / 2;
	    double length = 1;
	    
		
			length=0.3+Math.log((double) (children.size()));
			//if(children.size()>=2){
			//	length =(nodeSize*3)/(Math.sin(splitAngle/2));
		//	}
			
			

		Point2D.Double relativePoint = new Point2D.Double(0, 0);
		System.out.println("number of children: " + numberOfChildren);
		for (int i = 0; i < numberOfChildren; i++) {
			absoluteAngle = absoluteAngle + splitAngle;
			
			
			
			Point2D.Double newPoint = new Point2D.Double(parentNode
					.getPosition().getX()
					, parentNode.getPosition().getY() );

			relativePoint = angleToCoordinate(absoluteAngle);
			newPoint.setLocation(newPoint.getX() + relativePoint.getX()*length,
					newPoint.getY() + relativePoint.getY()*length);

			children.get(i).setPosition(
					new Point2D.Double(newPoint.getX(), newPoint.getY()));
			System.out.println("Angle: " + absoluteAngle * 180 / Math.PI);
			System.out.println("SplitAngle: " + splitAngle * 180 / Math.PI);
			System.out.println("Node " + children.get(i).nodeName
					+ " is set to: " + children.get(i).getPosition().getX()
					+ "|" + children.get(i).getPosition().getY());
			// recursion step:
			treeNodeCounter++;
			children.get(i).iComparableValue = treeNodeCounter;

			layoutNode(children.get(i), absoluteAngle, splitAngle);

		}
		return true;
	}

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
	
   public void zoomTree(int mode){
	   //zoom in;
	   if(mode==1){
	     scaleTree(2,2);
	     
	   }
	   else{
	
	   }
   }
}
