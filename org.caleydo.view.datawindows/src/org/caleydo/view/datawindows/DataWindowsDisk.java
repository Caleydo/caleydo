package org.caleydo.view.datawindows;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.data.graph.tree.DefaultNode;
import org.caleydo.core.data.graph.tree.Tree;

public class DataWindowsDisk extends PoincareDisk {

	// Tree<PoincareNode> tree;
	private double canvasWidth;
	private double canvasHeight;

	public DataWindowsDisk(double diskRadius) {
		super(diskRadius);

	}

	public void renderTree(GL gl, float viewingWidth, float viewingHeight) {

		canvasWidth = (double) viewingWidth;
		canvasHeight = (double) viewingHeight;

		PoincareNode root = getTree().getRoot();

		renderNode(root, gl);

	}

	public boolean renderNode(PoincareNode node, GL gl) {

		drawNode(node, gl);
		// System.out.println("Node wird dargestellt: " + node.nodeName);

		if (node.getChildren() == null) {
			return false;
		}

		ArrayList<PoincareNode> children = node.getChildren();
		int numberOfChildren = children.size();

		for (int i = 0; i < numberOfChildren; i++) {

			// render the line:

			drawLine(gl, node, children.get(i), 0);

			renderNode(children.get(i), gl);
		}

		return true;
	}

	public void drawNode(PoincareNode node, GL gl) {
		drawCircle(gl, 0.05f, node.getProjectedPosition().getX()
				+ (canvasWidth / 2), node.getProjectedPosition().getY()
				+ canvasHeight / 2);
		// System.out.println("node drawn at"+node.getProjectedPosition().getX()+2.5f+"|"+node.getProjectedPosition().getY()+2.5f);
	}

	public void drawLine(GL gl, PoincareNode node1, PoincareNode node2,
			int numberOfDetails) {

		Point2D.Double startingPoint = node1.getPosition();
		Point2D.Double endingPoint = node2.getPosition();
		
//		if(node1.getPosition().getX()==node1.getProjectedPosition().getX()){
//			
//			System.out.println("panic!!!:"+node1.nodeName);
//		}
//		else
//		{
//			System.out.println("keine panic!!!: "+node1.nodeName);
//		}

		gl.glBegin(GL.GL_LINES);
		gl.glVertex3d(node1.getProjectedPosition().getX() + (canvasWidth / 2),
				node1.getProjectedPosition().getY() + canvasHeight / 2, 0);

		if (numberOfDetails != 0) {
			// double lineLength = distancePoints(startingPoint,endingPoint);

			// double relativeLineLenght = lineLength/(numberOfDetails+2);
			Point2D.Double directionFactor = new Point2D.Double();
			directionFactor.setLocation((endingPoint.getX() - startingPoint
					.getX())
					/ (numberOfDetails + 1),
					(endingPoint.getY() - startingPoint.getY())
							/ (numberOfDetails + 1));

			Point2D.Double tempLinePoint = new Point2D.Double();
			tempLinePoint.setLocation(startingPoint);
			Point2D.Double tempLinePoint2 = new Point2D.Double();
			Point2D.Double projectedPoint;
			System.out.println("startpunkt: " + startingPoint.getX() + "|"
					+ startingPoint.getY());
			System.out.println("endpunkt: " + endingPoint.getX() + "|"
					+ endingPoint.getY());
			System.out.println("factor: " + directionFactor.getX() + "|"
					+ directionFactor.getY());
			
			for (int i = 0; i < numberOfDetails; i++) {
				tempLinePoint2.setLocation(tempLinePoint.getX()
						+ directionFactor.getX() * (i + 1), tempLinePoint
						.getY()
						+ directionFactor.getY() * (i + 1));
				System.out.println("tempPunkt: " + tempLinePoint2.getX() + "|"
						+ tempLinePoint2.getY());
				projectedPoint = projectPoint(tempLinePoint2);
				gl.glVertex3d(projectedPoint.getX() + (canvasWidth / 2),
						projectedPoint.getY() + canvasHeight / 2, 0);

			}
		}

		gl.glVertex3d(node2.getProjectedPosition().getX() + (canvasWidth / 2),
				node2.getProjectedPosition().getY() + canvasHeight / 2, 0);
		gl.glEnd();
	}

	public void drawBackground() {

	}

	private void drawCircle(GL gl, double radius, double k, double h) {
		// code from http://www.swiftless.com/tutorials/opengl/circle.html
		// //20.2.2010
		double circleX = 0;
		double circleY = 0;
		double i = 0;

		gl.glBegin(GL.GL_LINES);
		for (double counter = 0; counter < 360; counter++) {
			i = counter * Math.PI / 180;

			circleX = radius * Math.cos(i);
			circleY = radius * Math.sin(i);
			gl.glVertex3d(circleX + k, circleY + h, 0);

			circleX = radius * Math.cos(i + Math.PI / 180);
			circleY = radius * Math.sin(i + Math.PI / 180);
			gl.glVertex3d(circleX + k, circleY + h, 0);
		}
		gl.glEnd();
	}

}
