package org.caleydo.view.datawindows;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import org.caleydo.core.data.graph.tree.Tree;

public class PoincareDisk {

	private double radius;
	private Tree<PoincareNode> tree;
	private double treeScaleFactor = 1;
	private boolean dirtyTree = true;

	public PoincareDisk(double diskRadius) {
		radius = diskRadius;
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
		tree.addChild(node, new PoincareNode(tree, "Child1 l1", 1));
		tree.addChild(node, new PoincareNode(tree, "Child2 l1", 3));
		tree.addChild(node, new PoincareNode(tree, "Child3 l1", 5));

		int iCount = 19;
		for (PoincareNode tempNode : tree.getChildren(node)) {
			tree.addChild(tempNode, new PoincareNode(tree, "Child3 l1",
					iCount--));
			tree.addChild(tempNode, new PoincareNode(tree, "Child4 l1",
					iCount--));
			tree.addChild(tempNode, new PoincareNode(tree, "Child5 l1",
					iCount--));
			PoincareNode tempNode2 = new PoincareNode(tree, "Child6 l1",
					iCount--);
			tree.addChild(tempNode, tempNode2);
			PoincareNode tempNode3 = new PoincareNode(tree, "Child6 l1",
					iCount--);
			tree.addChild(tempNode2, tempNode3);
		}

		layoutTree();
		scaleTree(treeScaleFactor);
		projectTree();
		
	}

	public void centerNode() {

	}

	public void translateTree() {

	}

	public void scaleTree(double factor) {

	}

	public Point2D.Double projectPoint(Point2D.Double coordinate) {
		radius = 2;

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

		if (tree.getChildren(parentNode) == null) {
			return false;
		}

		ArrayList<PoincareNode> children = tree.getChildren(parentNode);
		int numberOfChildren = children.size();
		for (int i = 0; i < numberOfChildren; i++) {
			
			children.get(i).setProjectedPosition(
					projectPoint(children.get(i).getPosition()));
			System.out.println("name"+children.get(i).nodeName + "position vor projekt:" +children.get(i).getPosition().getX()+"|"+children.get(i).getPosition().getX());
			// recursion step
			projectNode(children.get(i));
		}
		for (int i = 0; i < numberOfChildren; i++) {
			System.out.println("Node projziert auf: "
					+ children.get(i).getProjectedPosition().getX() + "|"
					+ children.get(i).getProjectedPosition().getY());
			PoincareNode node1 = children.get(i);
			if(node1.getPosition().getX()==node1.getProjectedPosition().getX()){
				
				System.out.println("panic!!!:"+node1.nodeName);
			}
			else
			{
				System.out.println("keine panic!!!: "+node1.nodeName);
			}
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
		layoutNode(root, 0, 2 * Math.PI);

	}

	// The angleOffset is the starting angle of the available area
	// angle is the length of the area
	// All angles are in radiant
	private boolean layoutNode(PoincareNode parentNode, double angleOffset,
			double angle) {
		System.out.println("layoutNode " + parentNode.nodeName + " Called");

		if (tree.getChildren(parentNode) == null) {
			System.out.println("no children");
			return false;
		}

		ArrayList<PoincareNode> children = tree.getChildren(parentNode);
		int numberOfChildren = children.size();
		double splitAngle = angle / (double) (numberOfChildren);
		double absoluteAngle = angleOffset;
		Point2D.Double newPoint = new Point2D.Double(0, 0);
		Point2D.Double relativePoint = new Point2D.Double(0, 0);

		for (int i = 0; i < numberOfChildren; i++) {
			newPoint = parentNode.getPosition();
			relativePoint = angleToCoordinate(absoluteAngle);
			newPoint.setLocation(newPoint.getX() + relativePoint.getX(),
					newPoint.getY() + relativePoint.getY());

			children.get(i).setPosition(
					new Point2D.Double(newPoint.getX() + relativePoint.getX(),
							newPoint.getY() + relativePoint.getY()));
			System.out.println("Angle: " + absoluteAngle * 180 / Math.PI);
			System.out.println("Node set to: "
					+ children.get(i).getPosition().getX() + "|"
					+ children.get(i).getPosition().getX());
			// recursion step:
			layoutNode(children.get(i), absoluteAngle, splitAngle);
			absoluteAngle = absoluteAngle + splitAngle;
		}

		for (int i = 0; i < numberOfChildren; i++) {
			// children.get(i).setPosition(new Point2D.Double((double)i,6));
			System.out.println("Node is at: "
					+ children.get(i).getPosition().getX() + "|"
					+ children.get(i).getPosition().getY());
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
}
