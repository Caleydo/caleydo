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
	private double eyeTrackerBorder;
	private double eyeTrackerPrecision;
	private GLHyperbolic hyperbolic;

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
		eyeTrackerBorder = 0.8;
		eyeTrackerPrecision = 0.1;
		hyperbolic = master;
	}

	public void mouseOverNode(int nodeIndex) {
		this.getNodeByCompareableValue(nodeIndex).highLighted = true;
	}

	public void displayDetailLevels() {

		gl.glLineWidth(1);

		drawCircle(levelOfDetailLimits[2] * displayScaleFactorX,
				canvasWidth / 2, canvasHeight / 2);
		drawCircle(levelOfDetailLimits[1] * displayScaleFactorX,
				canvasWidth / 2, canvasHeight / 2);
		drawCircle(levelOfDetailLimits[0] * displayScaleFactorX,
				canvasWidth / 2, canvasHeight / 2);
	}

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

	public void renderTree(GL glHandle, TextureManager texManager,
			PickingManager pickManager, int iViewID, double viewingWidth,
			double viewingHeight) {

		gl = glHandle;

		displayScaleFactorX = viewingWidth / 2;
		displayScaleFactorY = viewingHeight / 2;

		pickingManager = pickManager;
		textureManager = texManager;
		canvasWidth = viewingWidth;
		canvasHeight = viewingHeight;
		iUniqueID = iViewID;

		// displayDetailLevels();

		// drawBackground();
		PoincareNode root = getTree().getRoot();
		renderNode(root, 2);

	}

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
				renderNode(children.get(i), mode);
			}
		}
		// there is nothing drawn beyond detail level 3
		if (distanceToDetaillevel(node.getDistanceFromOrigin()) <= 2) {
			drawNode(node, mode);
		}
		return true;
	}

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
				(float) (-size + node.getPosition().getX()
						* displayScaleFactorX + canvasWidth / 2),
				(float) (-size + node.getPosition().getY()
						* displayScaleFactorY + canvasHeight / 2), 0);
		Vec3f lowerRightCorner = new Vec3f(
				(float) (size + node.getPosition().getX() * displayScaleFactorX + canvasWidth / 2),
				(float) (-size + node.getPosition().getY()
						* displayScaleFactorY + canvasHeight / 2), 0);
		Vec3f upperRightCorner = new Vec3f(
				(float) (size + node.getPosition().getX() * displayScaleFactorX + canvasWidth / 2),
				(float) (size + node.getPosition().getY() * displayScaleFactorY + canvasHeight / 2),
				0);
		Vec3f upperLeftCorner = new Vec3f(
				(float) (-size + node.getPosition().getX()
						* displayScaleFactorX + canvasWidth / 2),
				(float) (size + node.getPosition().getY() * displayScaleFactorY + canvasHeight / 2),
				0);
		Vec3f scalingPivot = new Vec3f(1, 1, 0);

		if (mode == 2) {
			lowerLeftCorner = new Vec3f(
					(float) (-size + node.getZoomedPosition().getX()
							* displayScaleFactorX + canvasWidth / 2),
					(float) (-size * (canvasHeight / canvasWidth)
							+ node.getZoomedPosition().getY()
							* displayScaleFactorY + canvasHeight / 2), 0);
			lowerRightCorner = new Vec3f(
					(float) (size + node.getZoomedPosition().getX()
							* displayScaleFactorX + canvasWidth / 2),
					(float) (-size * (canvasHeight / canvasWidth)
							+ node.getZoomedPosition().getY()
							* displayScaleFactorY + canvasHeight / 2), 0);
			upperRightCorner = new Vec3f(
					(float) (size + node.getZoomedPosition().getX()
							* displayScaleFactorX + canvasWidth / 2),
					(float) (size * (canvasHeight / canvasWidth)
							+ node.getZoomedPosition().getY()
							* displayScaleFactorY + canvasHeight / 2), 0);
			upperLeftCorner = new Vec3f(
					(float) (-size + node.getZoomedPosition().getX()
							* displayScaleFactorX + canvasWidth / 2),
					(float) (size * (canvasHeight / canvasWidth)
							+ node.getZoomedPosition().getY()
							* displayScaleFactorY + canvasHeight / 2), 0);
			scalingPivot = new Vec3f(1, 1, 0);
		}

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

	public void drawLine(PoincareNode node1, PoincareNode node2,
			int numberOfDetails, int mode) {

		if (numberOfDetails != 0) {
			double length = this.distancePoints(node1.getPosition(), node2
					.getPosition());
			
			Point2D.Double stepVector = new Point2D.Double();
			Point2D.Double eV = new Point2D.Double();
			// Point2D.Double actualPosition = new Point2D.Double();

			ComplexNumber actualPositionComplex = new ComplexNumber();
			ComplexNumber stepVectorComplex = new ComplexNumber();
			actualPositionComplex.setValue(node1.getZoomedPosition().getX(),
					node1.getZoomedPosition().getY());
			gl.glLineWidth((float) lineWidth);
			gl.glBegin(GL.GL_LINE_STRIP);
			gl.glColor3i(0, 0, 0);
			gl.glVertex3d(node1.getZoomedPosition().getX()
					* displayScaleFactorX + (canvasWidth / 2), node1
					.getZoomedPosition().getY()
					* displayScaleFactorY + canvasHeight / 2, 0);

			for (int i = 0; i < numberOfDetails; i++) {
				
				eV.setLocation(this.getEV(new Point2D.Double(node2
						.getZoomedPosition().getX()
						- actualPositionComplex.getRealPart(), node2
						.getZoomedPosition().getY()
						- actualPositionComplex.getImaginaryPart())));

				stepVector.setLocation(eV.getX() * length
						/ (numberOfDetails), eV.getY() * length
						/ (numberOfDetails));

				stepVectorComplex.setValue(stepVector.getX(),stepVector.getY());
				
				actualPositionComplex = moebiusTransformation(
						actualPositionComplex, stepVectorComplex);

				//System.out.println("test"+stepVectorComplex.getRealPart());
				
				gl.glVertex3d(actualPositionComplex.getRealPart()
						* displayScaleFactorX + (canvasWidth / 2),
						actualPositionComplex.getImaginaryPart()
								* displayScaleFactorY + canvasHeight / 2, 0);

			}
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

	}

	public void drawCircle(double radius, double k, double h) {
		// code from http://www.swiftless.com/tutorials/opengl/circle.html
		// //20.2.2010
		double circleX = 0;
		double circleY = 0;
		double i = 0;

		gl.glBegin(GL.GL_LINE_STRIP);
		gl.glColor3f(0, 0, 0);
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

	public PoincareNode processEyeTrackerAction(Point2D.Double eyePosition,
			ArrayList<NodeSlerp> arSlerpActions) {
		Point2D.Double offsetFromMiddle = new Point2D.Double();
		offsetFromMiddle.setLocation((eyePosition.getX() - canvasWidth / 2)
				/ displayScaleFactorX, (eyePosition.getY() - canvasHeight / 2)
				/ displayScaleFactorY);

		PoincareNode returnNode;

		if (this.distanceFromOrigin(offsetFromMiddle) < eyeTrackerBorder) {
			System.out.println("inside of direct picking");
			returnNode = findNodeByCoordinate(offsetFromMiddle, 0);

			if (returnNode != null) {
				arSlerpActions.add(new NodeSlerp(4, returnNode.getPosition(),
						new Point2D.Double(0, 0)));
			}

		} else {
			System.out.println("outside of direct picking");
			returnNode = findNodeByCoordinate(offsetFromMiddle,
					eyeTrackerPrecision);

			if (returnNode != null) {

				System.out.println("slerp target:"
						+ returnNode.getPosition().getX() * eyeTrackerBorder
						+ "|" + returnNode.getPosition().getY()
						* eyeTrackerBorder);

				Point2D.Double eV = getEV(returnNode.getPosition());
				double overlaping = 0.1;

				arSlerpActions.add(new NodeSlerp(4, returnNode.getPosition(),
						new Point2D.Double(eV.getX()
								* (eyeTrackerBorder - overlaping), eV.getY()
								* (eyeTrackerBorder - overlaping))));

				// arSlerpActions.add(new nodeSlerp(4, returnNode.getPosition(),
				// new Point2D.Double(0.5, 0.5)));
			}

		}

		return returnNode;
	}

}