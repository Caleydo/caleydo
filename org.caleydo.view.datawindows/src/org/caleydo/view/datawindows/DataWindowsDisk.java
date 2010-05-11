package org.caleydo.view.datawindows;

import gleem.linalg.Vec3f;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.core.view.opengl.util.texture.TextureManager;

public class DataWindowsDisk extends PoincareDisk {

	private double canvasWidth;
	private double canvasHeight;
	private TextureManager textureManager;
	PickingManager pickingManager;
	private GL gl;
	int iUniqueID;
	private double[] levelOfDetailLimits;
	private double displayScaleFactorX = 10;
	private double displayScaleFactorY = 10;
	private GLHyperbolic hyperbolic;
	private double radiussquare;

	public DataWindowsDisk(GLHyperbolic master) {
		super();

		// Create the borders of the discrete DetailLevels:
		this.levelOfDetailLimits = new double[3];
		// display views
		levelOfDetailLimits[0] = 0.8;
		// display icons
		levelOfDetailLimits[1] = 0.96;
		// display only lines
		levelOfDetailLimits[2] = 1;

		hyperbolic = master;

		radiussquare = radius * radius;

	}

	public void mouseOverNode(int nodeIndex) {
		this.getNodeByCompareableValue(nodeIndex).highLighted = true;
	}

	public void displayDetailLevels() {

		gl.glLineWidth(1);

		// drawCircle(levelOfDetailLimits[2] * displayScaleFactorX,
		// canvasWidth / 2, canvasHeight / 2);
		// drawCircle(levelOfDetailLimits[1] * displayScaleFactorX,
		// canvasWidth / 2, canvasHeight / 2);
		// drawCircle(levelOfDetailLimits[0] * displayScaleFactorX,
		// canvasWidth / 2, canvasHeight / 2);
	}

	// returns the detail level of a given distance from the middle
	private int distanceToDetaillevel(double distance) {

		if (distance <= levelOfDetailLimits[0]) {
			return 1;
		}
		if (distance <= levelOfDetailLimits[1]) {
			return 2;
		}
		if (distance <= levelOfDetailLimits[2]) {
			return 3;
		}
		return 0;
	}

	public void renderTree(GL gl, TextureManager texManager,
			PickingManager pickManager, int iViewID, double viewingWidth,
			double viewingHeight) {

		this.gl = gl;

		displayScaleFactorX = viewingWidth / 2;
		displayScaleFactorY = viewingHeight / 2;

		pickingManager = pickManager;
		textureManager = texManager;
		canvasWidth = viewingWidth;
		canvasHeight = viewingHeight;
		iUniqueID = iViewID;

		// displayDetailLevels();

		drawBackground();
		PoincareNode root = getTree().getRoot();

		// start rendering the nodes of the tree recursively
		renderNode(root, 2);
	}

	// renders a node, and calls this method for all children
	public boolean renderNode(PoincareNode node, int mode) {

		if (node.nonExistent == true) {
			return true;
		}

		if (node.getChildren() != null) {
			ArrayList<PoincareNode> children = node.getChildren();
			int numberOfChildren = children.size();
			for (int i = 0; i < numberOfChildren; i++) {
				// render the lines:
				if (distanceToDetaillevel(node.getDistanceFromOrigin()) == 1) {
					drawLine(node, children.get(i), 10, mode);
				}
				if (distanceToDetaillevel(node.getDistanceFromOrigin()) == 2) {
					drawLine(node, children.get(i), 10, mode);
				}
				if (distanceToDetaillevel(node.getDistanceFromOrigin()) == 3) {
					drawLine(node, children.get(i), 10, mode);
				}
				// recursion step
				renderNode(children.get(i), mode);
			}
		}
		// there is nothing drawn beyond detail level 3
		if (distanceToDetaillevel(node.getDistanceFromOrigin()) <= 2) {
			drawNode(node, mode);
		}
		return true;
	}

	// draw the node to the display
	public void drawNode(PoincareNode node, int mode) {

		// for a realistic size, the size is a projected offset of the current
		double size = getMetric(node.getPosition(), nodeSize);

		if (distanceToDetaillevel(node.getDistanceFromOrigin()) == 1) {
			if (node.highLighted == true) {
				size = size * 1.5;
			}

			if (node == this.getCenteredNode()) {
				size = centeredNodeSize;
			}
		}

		Vec3f lowerLeftCorner = new Vec3f(
				(float) (-size + node.getZoomedPosition().getX()
						* displayScaleFactorX + canvasWidth / 2),
				(float) (-size * (canvasHeight / canvasWidth)
						+ node.getZoomedPosition().getY() * displayScaleFactorY + canvasHeight / 2),
				0);
		Vec3f lowerRightCorner = new Vec3f(
				(float) (size + node.getZoomedPosition().getX()
						* displayScaleFactorX + canvasWidth / 2),
				(float) (-size * (canvasHeight / canvasWidth)
						+ node.getZoomedPosition().getY() * displayScaleFactorY + canvasHeight / 2),
				0);
		Vec3f upperRightCorner = new Vec3f(
				(float) (size + node.getZoomedPosition().getX()
						* displayScaleFactorX + canvasWidth / 2),
				(float) (size * (canvasHeight / canvasWidth)
						+ node.getZoomedPosition().getY() * displayScaleFactorY + canvasHeight / 2),
				0);
		Vec3f upperLeftCorner = new Vec3f(
				(float) (-size + node.getZoomedPosition().getX()
						* displayScaleFactorX + canvasWidth / 2),
				(float) (size * (canvasHeight / canvasWidth)
						+ node.getZoomedPosition().getY() * displayScaleFactorY + canvasHeight / 2),
				0);

		Vec3f scalingPivot = new Vec3f(1, 1, 0);

		int iPickingID = pickingManager.getPickingID(iUniqueID,
				EPickingType.DATAW_NODE, node.iComparableValue);

		gl.glPushName(iPickingID);
		// different textures for different detail levels
		if (distanceToDetaillevel(node.getDistanceFromOrigin()) == 2) {
			float alpha = 1;
			if (node.markedToRemove == true) {
				alpha = 0.7f;
			}
			textureManager.renderGUITexture(gl, EIconTextures.PATHWAY_ICON,
					lowerLeftCorner, lowerRightCorner, upperRightCorner,
					upperLeftCorner, scalingPivot, 1, 1, 1, alpha, 100);
		} else {

			hyperbolic.drawRemoteView(gl, node, new Point2D.Double(-size
					+ node.getZoomedPosition().getX() * displayScaleFactorX
					+ canvasWidth / 2, -size * (canvasHeight / canvasWidth)
					+ node.getZoomedPosition().getY() * displayScaleFactorY
					+ canvasHeight / 2), size / 4);
		}
		gl.glPopName();
	}

	public Point2D.Double projectPoint(Point2D.Double point, boolean direction) {

		// if direction is true, the point will be projected, otherwise it will
		// be unprojected

		Point2D.Double coordinate = new Point2D.Double();
		coordinate.setLocation(point);
		double coordinateLength = coordinate.getX() * coordinate.getX()
				+ coordinate.getY() * coordinate.getY();
		double coordinateLength2;

		coordinateLength = Math.sqrt(coordinateLength);
		double projectionFactor = (2 * radiussquare)
				/ (radiussquare + coordinateLength);
		// if direction is true, the point will be projected, otherwise the
		// point will be unprojected
		if (direction == true) {
			coordinate.setLocation(coordinate.getX() * projectionFactor,
					coordinate.getY() * projectionFactor);
		} else {
			Point2D.Double testCoord = new Point2D.Double();
			double error;
			for (coordinateLength = 0; coordinateLength < 100; coordinateLength += 0.01) {

				projectionFactor = (2 * radiussquare)
						/ (radiussquare + coordinateLength);

				coordinate.setLocation(point.getX() * projectionFactor, (point
						.getY() * projectionFactor));

				coordinateLength2 = coordinate.getX() * coordinate.getX()
						+ coordinate.getY() * coordinate.getY();
				coordinateLength2 = Math.sqrt(coordinateLength2);

				projectionFactor = (2 * radiussquare)
						/ (radiussquare + coordinateLength2);

				testCoord.setLocation(coordinate.getX() * (projectionFactor),
						(coordinate.getY() * (projectionFactor)));

				error = distancePoints(testCoord, point);

				if (error < 0.01) {

					return coordinate;
				}

			}
		}

		return coordinate;
	}

	public void drawLine(PoincareNode node1, PoincareNode node2,
			int numberOfDetails, int mode) {

		if (numberOfDetails != 0) {

			// transfer the start and end point into real coordinates:
			Point2D.Double startPoint = new Point2D.Double();
			startPoint.setLocation(this.projectPoint(node1.getZoomedPosition(),
					false));
			Point2D.Double endPoint = new Point2D.Double();
			endPoint
					.setLocation(projectPoint(node2.getZoomedPosition(), false));
			double length = this.distancePoints(startPoint, endPoint);

			gl.glLineWidth((float) lineWidth);
			gl.glBegin(GL.GL_LINE_STRIP);
			gl.glColor3i(0, 0, 0);
			gl.glVertex3d(node1.getZoomedPosition().getX()
					* displayScaleFactorX + (canvasWidth / 2), node1
					.getZoomedPosition().getY()
					* displayScaleFactorY + canvasHeight / 2, 0);

			Point2D.Double eV = new Point2D.Double();
			eV.setLocation(this.getEV(new Point2D.Double(endPoint.getX()
					- startPoint.getX(), endPoint.getY() - startPoint.getY())));

			Point2D.Double actPosition = new Point2D.Double();
			actPosition.setLocation(startPoint);
			Point2D.Double actProjectedPosition = new Point2D.Double();

			for (int i = 0; i < numberOfDetails; i++) {
				actPosition.setLocation(actPosition.getX() + eV.getX()
						* (length / (double) numberOfDetails), actPosition
						.getY()
						+ eV.getY() * (length / (double) numberOfDetails));
				actProjectedPosition = this.projectPoint(actPosition, true);

				gl.glLineWidth((float) lineWidth);
				gl.glBegin(GL.GL_LINE_STRIP);
				gl.glColor3i(0, 0, 0);
				gl.glVertex3d(actProjectedPosition.getX() * displayScaleFactorX
						+ (canvasWidth / 2), actProjectedPosition.getY()
						* displayScaleFactorY + canvasHeight / 2, 0);
			}

			// double stepWidth=length/numberOfDetails;
			//			
			// while (length > 0.02) {
			//
			// eV.setLocation(this.getEV(new Point2D.Double(node2
			// .getZoomedPosition().getX()
			// - actualPositionComplex.getRealPart(), node2
			// .getZoomedPosition().getY()
			// - actualPositionComplex.getImaginaryPart())));
			//
			// stepVector.setLocation(eV.getX() * stepWidth,
			// eV.getY() * stepWidth);
			//
			// stepVectorComplex
			// .setValue(stepVector.getX(), stepVector.getY());
			//
			// actualPositionComplex = moebiusTransformation(
			// actualPositionComplex, stepVectorComplex);
			//
			// // System.out.println("test"+stepVectorComplex.getRealPart());
			// length = this.distancePoints(new Point2D.Double(
			// actualPositionComplex.getRealPart(),
			// actualPositionComplex.getImaginaryPart()), node2
			// .getPosition());
			//
			// gl.glVertex3d(actualPositionComplex.getRealPart()
			// * displayScaleFactorX + (canvasWidth / 2),
			// actualPositionComplex.getImaginaryPart()
			// * displayScaleFactorY + canvasHeight / 2, 0);
			//
			// }

			gl.glVertex3d(node2.getZoomedPosition().getX()
					* displayScaleFactorX + (canvasWidth / 2), node2
					.getZoomedPosition().getY()
					* displayScaleFactorY + canvasHeight / 2, 0);
			gl.glEnd();

			// System.out.println("vector:" + stepVector.getX() + "|"
			// + stepVector.getY());
		} else {
			gl.glLineWidth((float) lineWidth);
			gl.glBegin(GL.GL_LINE_STRIP);
			gl.glColor3i(0, 0, 0);
			gl.glVertex3d(node1.getZoomedPosition().getX()
					* displayScaleFactorX + (canvasWidth / 2), node1
					.getZoomedPosition().getY()
					* displayScaleFactorY + canvasHeight / 2, 0);
			gl.glVertex3d(node2.getZoomedPosition().getX()
					* displayScaleFactorX + (canvasWidth / 2), node2
					.getZoomedPosition().getY()
					* displayScaleFactorY + canvasHeight / 2, 0);
			gl.glEnd();
		}
	}

	public void drawBackground() {
		drawCircle(1 * this.displayScaleFactorX, 1 * this.displayScaleFactorY,
				canvasWidth / 2, canvasHeight / 2);
	}

	public void drawCircle(double width, double height, double k, double h) {

		// code from http://www.swiftless.com/tutorials/opengl/circle.html
		// //20.2.2010
		double circleX = 0;
		double circleY = 0;
		double i = 0;

		gl.glBegin(GL.GL_LINE_STRIP);
		gl.glColor3f(0, 0, 0);
		for (double counter = 0; counter < 360; counter++) {
			i = counter * Math.PI / 180;

			circleX = width * Math.cos(i);
			circleY = height * Math.sin(i);
			gl.glVertex3d(circleX + k, circleY + h, 0);

			circleX = width * Math.cos(i + Math.PI / 180);
			circleY = height * Math.sin(i + Math.PI / 180);
			gl.glVertex3d(circleX + k, circleY + h, 0);
		}
		gl.glEnd();
	}

	public PoincareNode processEyeTrackerAction(Point2D.Double normedEyePosition,
			ArrayList<NodeSlerp> arSlerpActions) {
		//Point2D.Double offsetFromMiddle = new Point2D.Double();
		
		
//		offsetFromMiddle.setLocation((eyePosition.getX() - canvasWidth / 2)
//				/ displayScaleFactorX, (eyePosition.getY() - canvasHeight / 2)
//				/ displayScaleFactorY);
//
//		System.out.println("offset from Middle: " + offsetFromMiddle.getX()
	//	+ "|" + offsetFromMiddle.getY());
		PoincareNode returnNode = null;

		// comment in for eyetracker focus
		// if (this.distanceFromOrigin(offsetFromMiddle) < eyeTrackerBorder) {
		//System.out.println("inside of direct picking");
		returnNode = findNodeByCoordinate(normedEyePosition, 0);

		if (returnNode != null) {
			arSlerpActions.add(new NodeSlerp(4, returnNode.getPosition(),
					new Point2D.Double(0, 0)));
		}

		// } else {
		// //focus far nodes with the eyetracker
		// System.out.println("outside of direct picking");
		// returnNode = findNodeByCoordinate(offsetFromMiddle,
		// eyeTrackerPrecision);
		//
		// if (returnNode != null) {
		//
		// System.out.println("slerp target:"
		// + returnNode.getPosition().getX() * eyeTrackerBorder
		// + "|" + returnNode.getPosition().getY()
		// * eyeTrackerBorder);
		//
		// Point2D.Double eV = getEV(returnNode.getPosition());
		// double overlaping = 0.1;
		//
		// arSlerpActions.add(new NodeSlerp(4, returnNode.getPosition(),
		// new Point2D.Double(eV.getX()
		// * (eyeTrackerBorder - overlaping), eV.getY()
		// * (eyeTrackerBorder - overlaping))));
		//
		// // arSlerpActions.add(new nodeSlerp(4, returnNode.getPosition(),
		// // new Point2D.Double(0.5, 0.5)));
		// }
		//
		// }

		return returnNode;
	}

}