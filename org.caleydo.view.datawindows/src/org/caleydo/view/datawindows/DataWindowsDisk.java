package org.caleydo.view.datawindows;

import gleem.linalg.Vec3f;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;

import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.core.view.opengl.util.texture.TextureManager;

public class DataWindowsDisk extends PoincareDisk {

	// Tree<PoincareNode> tree;
	private double canvasWidth;
	private double canvasHeight;
	private TextureManager textureManager;
	PickingManager pickingManager;
	private GL gl;
	int iUniqueID;
	private double[] levelOfDetailLimits;
	private double lineFactor = 700;

	public DataWindowsDisk(double diskRadius) {
		super(diskRadius);
		// Create the borders of the discrete DetailLevels:
		this.levelOfDetailLimits = new double[3];
		// display views
		levelOfDetailLimits[0] = diskRadius * 1.5;
		// display icons
		levelOfDetailLimits[1] = diskRadius * 10;
		// display only lines
		levelOfDetailLimits[2] = diskRadius * 100;

	}

	public void mouseOverNode(int nodeIndex) {
		this.getNodeByCompareableValue(nodeIndex).highLighted = true;
	}

	public void displayDetailLevels() {

		gl.glLineWidth(1);
		double radius = projectPoint(
				new Point2D.Double(levelOfDetailLimits[2], 2)).getX();
		drawCircle(radius, canvasWidth / 2, canvasHeight / 2);

		radius = projectPoint(new Point2D.Double(levelOfDetailLimits[1], 2))
				.getX();
		drawCircle(radius, canvasWidth / 2, canvasHeight / 2);
		radius = projectPoint(new Point2D.Double(levelOfDetailLimits[0], 2))
				.getX();
		drawCircle(radius, canvasWidth / 2, canvasHeight / 2);

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
		// System.out.println("Baum wird gezeichnet: ");
		gl = glHandle;
		pickingManager = pickManager;
		textureManager = texManager;
		canvasWidth = viewingWidth;
		canvasHeight = viewingHeight;
		iUniqueID = iViewID;

		displayDetailLevels();

		// drawBackground();
		PoincareNode root = getTree().getRoot();
		renderNode(root, 2);

	}

	public boolean renderNode(PoincareNode node, int mode) {

		if (node.getChildren() != null) {
			ArrayList<PoincareNode> children = node.getChildren();
			int numberOfChildren = children.size();
			for (int i = 0; i < numberOfChildren; i++) {
				// render the line:
				if (distanceToDetaillevel(node.getDistanceFromOrigin()) == 1) {

					drawLine(node, children.get(i), 6, mode);
				}
				if (distanceToDetaillevel(node.getDistanceFromOrigin()) == 2) {

					drawLine(node, children.get(i), 3, mode);
				}
				if (distanceToDetaillevel(node.getDistanceFromOrigin()) == 3) {

					drawLine(node, children.get(i), 2, mode);
				}
				renderNode(children.get(i), mode);
			}
		}
		if (distanceToDetaillevel(node.getDistanceFromOrigin()) <= 2) {
			drawNode(node, mode);
		}
		return true;
	}

	public void drawNode(PoincareNode node, int mode) {

		double size = distancePoints(node.getProjectedPosition(), this
				.projectPoint(new Point2D.Double(node.getPosition().getX()
						+ nodeSize / 2, node.getPosition().getY() + nodeSize
						/ 2))) * 2;

		// if (node.highLighted==true){
		// size=size*2;
		// }

		Vec3f lowerLeftCorner = new Vec3f(
				(float) (-size + node.getProjectedPosition().getX() + canvasWidth / 2),
				(float) (-size + node.getProjectedPosition().getY() + canvasHeight / 2),
				0);
		Vec3f lowerRightCorner = new Vec3f(
				(float) (size + node.getProjectedPosition().getX() + canvasWidth / 2),
				(float) (-size + node.getProjectedPosition().getY() + canvasHeight / 2),
				0);
		Vec3f upperRightCorner = new Vec3f(
				(float) (size + node.getProjectedPosition().getX() + canvasWidth / 2),
				(float) (size + node.getProjectedPosition().getY() + canvasHeight / 2),
				0);
		Vec3f upperLeftCorner = new Vec3f(
				(float) (-size + node.getProjectedPosition().getX() + canvasWidth / 2),
				(float) (size + node.getProjectedPosition().getY() + canvasHeight / 2),
				0);

		if (mode == 2) {
			lowerLeftCorner = new Vec3f(
					(float) (-size + node.getPosition().getX() + canvasWidth / 2),
					(float) (-size + node.getPosition().getY() + canvasHeight / 2),
					0);
			lowerRightCorner = new Vec3f(
					(float) (size + node.getPosition().getX() + canvasWidth / 2),
					(float) (-size + node.getPosition().getY() + canvasHeight / 2),
					0);
			upperRightCorner = new Vec3f(
					(float) (size + node.getPosition().getX() + canvasWidth / 2),
					(float) (size + node.getPosition().getY() + canvasHeight / 2),
					0);
			upperLeftCorner = new Vec3f(
					(float) (-size + node.getPosition().getX() + canvasWidth / 2),
					(float) (size + node.getPosition().getY() + canvasHeight / 2),
					0);
		}

		Vec3f scalingPivot = new Vec3f(1, 1, 0);

		int iPickingID = pickingManager.getPickingID(iUniqueID,
				EPickingType.DATAW_NODE, node.iComparableValue);

		gl.glPushName(iPickingID);

		if (distanceToDetaillevel(node.getDistanceFromOrigin()) == 2) {
			textureManager.renderGUITexture(gl, EIconTextures.PATHWAY_ICON,
					lowerLeftCorner, lowerRightCorner, upperRightCorner,
					upperLeftCorner, scalingPivot, 1, 1, 1, 1, 100);
		} else {
			textureManager.renderGUITexture(gl, EIconTextures.PATHWAY_SYMBOL,
					lowerLeftCorner, lowerRightCorner, upperRightCorner,
					upperLeftCorner, scalingPivot, 1, 1, 1, 1, 100);
		}
		gl.glPopName();
	}

	public void drawLine(PoincareNode node1, PoincareNode node2,
			int numberOfDetails, int mode) {

		Point2D.Double startingPoint = node1.getPosition();
		Point2D.Double endingPoint = node2.getPosition();

		double width = distancePoints(node1.getProjectedPosition(),
				projectPoint(new Point2D.Double(node1.getPosition().getX()
						+ lineWidth, node1.getPosition().getY() + lineWidth)));

		width = width * lineFactor;

		gl.glLineWidth((float) width);

		gl.glBegin(GL.GL_LINE_STRIP);

		gl.glColor3i(0, 0, 0);
		if (mode == 1) {
			gl.glVertex3d(node1.getProjectedPosition().getX()
					+ (canvasWidth / 2), node1.getProjectedPosition().getY()
					+ canvasHeight / 2, 0);
		}
		if (mode == 2) {
			gl.glVertex3d(node1.getPosition().getX() + (canvasWidth / 2), node1
					.getPosition().getY()
					+ canvasHeight / 2, 0);
		}
		if (mode == 1) {
		// draw the curve:
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
		}
		if (mode == 1) {
		gl.glVertex3d(node2.getProjectedPosition().getX() + (canvasWidth / 2),
				node2.getProjectedPosition().getY() + canvasHeight / 2, 0);
		}
		if (mode == 2) {
			gl.glVertex3d(node2.getPosition().getX() + (canvasWidth / 2),
					node2.getPosition().getY() + canvasHeight / 2, 0);
		}
		gl.glEnd();
	}

	public void drawBackground() {

		double size = 3;

		Vec3f lowerLeftCorner = new Vec3f((float) (-size + canvasWidth / 2),
				(float) (-size + canvasHeight / 2), 0);
		Vec3f lowerRightCorner = new Vec3f((float) (size + canvasWidth / 2),
				(float) (-size + canvasHeight / 2), 0);
		Vec3f upperRightCorner = new Vec3f((float) (size + canvasWidth / 2),
				(float) (size + canvasHeight / 2), 0);
		Vec3f upperLeftCorner = new Vec3f((float) (-size + canvasWidth / 2),
				(float) (size + canvasHeight / 2), 0);
		Vec3f scalingPivot = new Vec3f(1, 1, 0);

		int iPickingID = pickingManager.getPickingID(iUniqueID,
				EPickingType.DATAW_NODE, 666);

		gl.glPushName(iPickingID);
		textureManager.renderGUITexture(gl, EIconTextures.GATE_BODY,
				lowerLeftCorner, lowerRightCorner, upperRightCorner,
				upperLeftCorner, scalingPivot, 1, 1, 1, 1, 100);

		gl.glPopName();
	}

	private void drawCircle(double radius, double k, double h) {
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

}
