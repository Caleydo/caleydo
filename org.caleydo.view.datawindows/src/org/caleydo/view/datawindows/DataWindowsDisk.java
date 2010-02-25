package org.caleydo.view.datawindows;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.data.graph.tree.Tree;

public class DataWindowsDisk extends PoincareDisk {

	// Tree<PoincareNode> tree;
	private double canvasWidth;
	private double canvasHeight;
	private GL gl;

	public DataWindowsDisk(double diskRadius) {
		super(diskRadius);

	}

	public void renderTree(GL glHandle, double viewingWidth, double viewingHeight) {
		 System.out.println("Baum wird gezeichnet: ");
		gl = glHandle;
		canvasWidth = viewingWidth;
		canvasHeight = viewingHeight;
		PoincareNode root = getTree().getRoot();
		renderNode(root);

	}

	public boolean renderNode(PoincareNode node) {

		drawNode(node);
		 System.out.println("Node wird dargestellt: " + node.nodeName);
		 System.out.println("An Position: " + node.getPosition().getX()+ "|" + node.getPosition().getY());
		 System.out.println("An projezierter Position: " + node.getProjectedPosition().getX()+ "|" + node.getProjectedPosition().getY());
		 
		 
		 
		 
		if (node.getChildren() == null) {
			return false;
		}

		ArrayList<PoincareNode> children = node.getChildren();
		int numberOfChildren = children.size();

		for (int i = 0; i < numberOfChildren; i++) {

			// render the line:
			drawLine(node, children.get(i), 5);
			renderNode(children.get(i));
		}

		return true;
	}

	public void drawNode(PoincareNode node) {
		drawCircle(0.05f, node.getProjectedPosition().getX()
				+ (canvasWidth / 2), node.getProjectedPosition().getY()
				+ canvasHeight / 2);
	}

	public void drawLine(PoincareNode node1, PoincareNode node2,
			int numberOfDetails) {

		Point2D.Double startingPoint = node1.getPosition();
		Point2D.Double endingPoint = node2.getPosition();
		
		gl.glBegin(GL.GL_LINE_STRIP);
		gl.glVertex3d(node1.getProjectedPosition().getX() + (canvasWidth / 2),
				node1.getProjectedPosition().getY() + canvasHeight / 2, 0);
		//draw the curve:
		if (numberOfDetails != 0) {
			Point2D.Double directionFactor = new Point2D.Double();
			directionFactor.setLocation((endingPoint.getX() - startingPoint
					.getX())
					/ (numberOfDetails + 1),
					(endingPoint.getY() - startingPoint.getY())
							/ (numberOfDetails + 1));

			Point2D.Double tempLinePoint = new Point2D.Double();
			tempLinePoint.setLocation(startingPoint);	
			for (int i = 0; i < numberOfDetails; i++) {
				Point2D.Double tempLinePoint2 = new Point2D.Double();
				tempLinePoint2.setLocation(tempLinePoint.getX()
						+ directionFactor.getX() * (i + 1), tempLinePoint
						.getY()
						+ directionFactor.getY() * (i + 1));

				gl.glVertex3d(projectPoint(tempLinePoint2).getX()
						+ (canvasWidth / 2), projectPoint(tempLinePoint2)
						.getY()
						+ canvasHeight / 2, 0);

			}
		}

		gl.glVertex3d(node2.getProjectedPosition().getX() + (canvasWidth / 2),
				node2.getProjectedPosition().getY() + canvasHeight / 2, 0);
		gl.glEnd();
	}

	public void drawBackground() {

	}

	private void drawCircle(double radius, double k, double h) {
		// code from http://www.swiftless.com/tutorials/opengl/circle.html
		// //20.2.2010
		double circleX = 0;
		double circleY = 0;
		double i = 0;

		gl.glBegin(GL.GL_LINE_STRIP);
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
