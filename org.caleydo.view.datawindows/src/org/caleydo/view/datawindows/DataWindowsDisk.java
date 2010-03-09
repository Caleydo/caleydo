package org.caleydo.view.datawindows;

import gleem.linalg.Vec3f;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.media.opengl.GL;

//import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.PickingManager; //import org.caleydo.core.manager.ICommandManager;
//import org.caleydo.core.manager.picking.EPickingMode;
//import org.caleydo.core.manager.picking.EPickingType;
//import org.caleydo.core.manager.picking.Pick;
//
//import org.caleydo.core.util.clusterer.ClusterNode;
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
	// private double lineFactor = 80;
	private double displayScaleFactor = 10;
	private double eyeTrackerBorder;
	private double eyeTrackerPrecision;

	public DataWindowsDisk() {
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

	}

	public void mouseOverNode(int nodeIndex) {
		this.getNodeByCompareableValue(nodeIndex).highLighted = true;
	}

	public void displayDetailLevels() {

		gl.glLineWidth(1);
		// double radius = projectPoint(
		// new Point2D.Double(levelOfDetailLimits[2], 0)).getX();

		drawCircle(levelOfDetailLimits[2] * displayScaleFactor,
				canvasWidth / 2, canvasHeight / 2);

		// radius = projectPoint(new Point2D.Double(levelOfDetailLimits[1], 0))
		// .getX();
		drawCircle(levelOfDetailLimits[1] * displayScaleFactor,
				canvasWidth / 2, canvasHeight / 2);
		// radius = projectPoint(new Point2D.Double(levelOfDetailLimits[0], 0))
		// .getX();
		drawCircle(levelOfDetailLimits[0] * displayScaleFactor,
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

		displayScaleFactor = viewingHeight / 2;

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
				// render the lines:
				if (distanceToDetaillevel(node.getDistanceFromOrigin()) == 1) {
					drawLine(node, children.get(i), 0, mode);
				}
				if (distanceToDetaillevel(node.getDistanceFromOrigin()) == 2) {
					drawLine(node, children.get(i), 0, mode);
				}
				if (distanceToDetaillevel(node.getDistanceFromOrigin()) == 3) {
					drawLine(node, children.get(i), 0, mode);
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
		// point
		// double modLength=distanceFromOrigin(node.getPosition());
		//		
		//		
		// Point2D.Double pointA=new Point2D.Double(modLength,modLength);
		// Point2D.Double pointB=new
		// Point2D.Double(modLength+nodeSize,modLength+nodeSize);
		//		
		// pointA=this.projectPoint(pointA);
		// pointB=this.projectPoint(pointB);
		//		
		// double size=distancePoints(pointA,pointB);

		double size = getMetric(node.getPosition(), nodeSize);

		if (node.highLighted == true) {
			size = size * 1.5;
		}

		// double size = distancePoints(node.getProjectedPosition(),
		// projectPoint(new Point2D.Double(node.getPosition().getX()
		// - (nodeSize / 2), node.getPosition().getY() - (nodeSize
		// / 2))))
		// * displayScaleFactor / 2;

		//
		// Vec3f lowerLeftCorner = new Vec3f(
		// (float) (-size + node.getProjectedPosition().getX()
		// * displayScaleFactor + canvasWidth / 2), (float) (-size
		// + node.getProjectedPosition().getY()
		// * displayScaleFactor + canvasHeight / 2), 0);
		// Vec3f lowerRightCorner = new Vec3f(
		// (float) (size + node.getProjectedPosition().getX()
		// * displayScaleFactor + canvasWidth / 2), (float) (-size
		// + node.getProjectedPosition().getY()
		// * displayScaleFactor + canvasHeight / 2), 0);
		// Vec3f upperRightCorner = new Vec3f(
		// (float) (size + node.getProjectedPosition().getX()
		// * displayScaleFactor + canvasWidth / 2), (float) (size
		// + node.getProjectedPosition().getY()
		// * displayScaleFactor + canvasHeight / 2), 0);
		// Vec3f upperLeftCorner = new Vec3f(
		// (float) (-size + node.getProjectedPosition().getX()
		// * displayScaleFactor + canvasWidth / 2), (float) (size
		// + node.getProjectedPosition().getY()
		// * displayScaleFactor + canvasHeight / 2), 0);

		// if there is no projection on the poincare disk

		Vec3f lowerLeftCorner = new Vec3f(
				(float) (-size + node.getPosition().getX() * displayScaleFactor + canvasWidth / 2),
				(float) (-size + node.getPosition().getY() * displayScaleFactor + canvasHeight / 2),
				0);
		Vec3f lowerRightCorner = new Vec3f(
				(float) (size + node.getPosition().getX() * displayScaleFactor + canvasWidth / 2),
				(float) (-size + node.getPosition().getY() * displayScaleFactor + canvasHeight / 2),
				0);
		Vec3f upperRightCorner = new Vec3f(
				(float) (size + node.getPosition().getX() * displayScaleFactor + canvasWidth / 2),
				(float) (size + node.getPosition().getY() * displayScaleFactor + canvasHeight / 2),
				0);
		Vec3f upperLeftCorner = new Vec3f(
				(float) (-size + node.getPosition().getX() * displayScaleFactor + canvasWidth / 2),
				(float) (size + node.getPosition().getY() * displayScaleFactor + canvasHeight / 2),
				0);

		Vec3f scalingPivot = new Vec3f(1, 1, 0);

		int iPickingID = pickingManager.getPickingID(iUniqueID,
				EPickingType.DATAW_NODE, node.iComparableValue);

		gl.glPushName(iPickingID);
		// different textures for different detail levels
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
		// Point2D.Double endingPoint = node2.getPosition();

		double width = this.getMetric(startingPoint, lineWidth);
		gl.glLineWidth((float) width);

		// draw the curve:
		// if (numberOfDetails != 0) {
		//
		// // gl.glVertex3d(node1.getProjectedPosition().getX()
		// // * displayScaleFactor + (canvasWidth / 2), node1
		// // .getProjectedPosition().getY()
		// // * displayScaleFactor + canvasHeight / 2, 0);
		// Point2D.Double lastPosition = new Point2D.Double(0, 0);
		//
		// Point2D.Double directionFactor = new Point2D.Double();
		// directionFactor.setLocation((endingPoint.getX() - startingPoint
		// .getX())
		// / (numberOfDetails + 1),
		// (endingPoint.getY() - startingPoint.getY())
		// / (numberOfDetails + 1));
		//
		// Point2D.Double tempLinePoint = new Point2D.Double();
		// tempLinePoint.setLocation(startingPoint);
		// for (int i = 0; i <= numberOfDetails + 1; i++) {
		// Point2D.Double tempLinePoint2 = new Point2D.Double();
		// tempLinePoint2.setLocation(tempLinePoint.getX()
		// + directionFactor.getX() * (i), tempLinePoint.getY()
		// + directionFactor.getY() * (i));
		//
		// width = distancePoints(
		// projectPoint(tempLinePoint2),
		// projectPoint(new Point2D.Double(tempLinePoint2.getX()
		// + lineWidth, tempLinePoint2.getY() + lineWidth)))
		// * lineFactor * displayScaleFactor;
		//
		// gl.glLineWidth((float) width);
		//
		// if (i > 0) {
		// gl.glBegin(GL.GL_LINE_STRIP);
		//
		// gl.glColor3i(0, 0, 0);
		// gl.glVertex3d(lastPosition.getX(), lastPosition.getY(), 0);
		//
		// lastPosition.setLocation(projectPoint(tempLinePoint2)
		// .getX()
		// * displayScaleFactor + (canvasWidth / 2),
		// projectPoint(tempLinePoint2).getY()
		// * displayScaleFactor + canvasHeight / 2);
		//
		// gl.glVertex3d(lastPosition.getX(), lastPosition.getY(), 0);
		// gl.glEnd();
		//
		// } else {
		// lastPosition.setLocation(projectPoint(tempLinePoint2)
		// .getX()
		// * displayScaleFactor + (canvasWidth / 2),
		// projectPoint(tempLinePoint2).getY()
		// * displayScaleFactor + canvasHeight / 2);
		// }
		// }

		// } else {
		gl.glBegin(GL.GL_LINE);

		gl.glColor3i(0, 0, 0);
		gl.glVertex3d(node1.getPosition().getX() * displayScaleFactor
				+ (canvasWidth / 2), node1.getPosition().getY()
				* displayScaleFactor + canvasHeight / 2, 0);

		gl.glVertex3d(node2.getPosition().getX() * displayScaleFactor
				+ (canvasWidth / 2), node2.getPosition().getY()
				* displayScaleFactor + canvasHeight / 2, 0);
		gl.glEnd();
	}

	// }

	public void drawBackground() {

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

	public PoincareNode processEyeTrackerAction(Point2D.Double eyePosition,
			ArrayList<nodeSlerp> arSlerpActions) {
		Point2D.Double offsetFromMiddle = new Point2D.Double();
		offsetFromMiddle.setLocation((eyePosition.getX() - canvasWidth / 2)
				/ displayScaleFactor, (eyePosition.getY() - canvasHeight / 2)
				/ -displayScaleFactor);

		PoincareNode returnNode;

		System.out.println("click:" + offsetFromMiddle.getX() + " "
				+ offsetFromMiddle.getY());

		if (this.distanceFromOrigin(offsetFromMiddle) < eyeTrackerBorder) {
			System.out.println("inside of direct picking");
			returnNode = findNodeByCoordinate(offsetFromMiddle, 0);

			if (returnNode != null) {
				arSlerpActions.add(new nodeSlerp(4, returnNode.getPosition(),
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
				
				 arSlerpActions.add(new nodeSlerp(4, returnNode.getPosition(),
				 new Point2D.Double(returnNode.getPosition().getX()
				 * eyeTrackerBorder, returnNode.getPosition()
				 .getY()
				 * eyeTrackerBorder)));

				

//				arSlerpActions.add(new nodeSlerp(4, returnNode.getPosition(),
//						new Point2D.Double(0.5, 0.5)));

			}

		}

		return returnNode;
	}

}
