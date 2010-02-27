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

import org.caleydo.core.view.opengl.util.slerp.SlerpAction;
import org.caleydo.core.view.opengl.util.slerp.SlerpMod;
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

	private static final int SLERP_RANGE = 1000;
	private static final int SLERP_SPEED = 1400;
	
	private ArrayList<nodeSlerp> arSlerpActions;



	public DataWindowsDisk(double diskRadius) {
		super(diskRadius);
		// Create the borders of the discrete DetailLevels:
		this.levelOfDetailLimits = new double[3];
		// display views
		levelOfDetailLimits[0] = diskRadius * 1.5;
		// display icons
		levelOfDetailLimits[1] = diskRadius * 20;
		// display only lines
		levelOfDetailLimits[2] = diskRadius * 100;

		
		arSlerpActions = new ArrayList<nodeSlerp>();
		
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
		renderNode(root);

	}

	public boolean renderNode(PoincareNode node) {

		// System.out.println("detail: " +
		// distanceToDetaillevel(node.getDistanceFromOrigin()));
		// System.out.println("distance: " + node.getDistanceFromOrigin());

		// node.getPosition().getY());
		// System.out.println("An projezierter Position: " +
		// node.getProjectedPosition().getX()+ "|" +
		// node.getProjectedPosition().getY());
		//		 

		if (node.getChildren() != null) {

			ArrayList<PoincareNode> children = node.getChildren();
			int numberOfChildren = children.size();

			for (int i = 0; i < numberOfChildren; i++) {

				// render the line:
				if (distanceToDetaillevel(node.getDistanceFromOrigin()) <= 3) {
					drawLine(node, children.get(i), 6);
				}
				renderNode(children.get(i));

			}

		}

		if (distanceToDetaillevel(node.getDistanceFromOrigin()) <= 2) {
			drawNode(node);
		}

		return true;
	}

	public void drawNode(PoincareNode node) {

		// drawCircle(0.05f, node.getProjectedPosition().getX()
		// + (canvasWidth / 2), node.getProjectedPosition().getY()
		// + canvasHeight / 2);

		double size = this.distancePoints(node.getProjectedPosition(), this
				.projectPoint(new Point2D.Double(node.getPosition().getX()
						+ nodeSize, node.getPosition().getY() + nodeSize)));

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
			int numberOfDetails) {

		Point2D.Double startingPoint = node1.getPosition();
		Point2D.Double endingPoint = node2.getPosition();

		double width = distancePoints(node1.getProjectedPosition(),
				projectPoint(new Point2D.Double(node1.getPosition().getX()
						+ lineWidth, node1.getPosition().getY() + lineWidth)));

		width = width * 700;

		gl.glLineWidth((float) width);

		gl.glBegin(GL.GL_LINE_STRIP);

		gl.glColor3i(0, 0, 0);

		gl.glVertex3d(node1.getProjectedPosition().getX() + (canvasWidth / 2),
				node1.getProjectedPosition().getY() + canvasHeight / 2, 0);
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

		gl.glVertex3d(node2.getProjectedPosition().getX() + (canvasWidth / 2),
				node2.getProjectedPosition().getY() + canvasHeight / 2, 0);

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
