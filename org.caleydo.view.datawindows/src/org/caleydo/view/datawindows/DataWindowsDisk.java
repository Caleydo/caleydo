package org.caleydo.view.datawindows;

import gleem.linalg.Vec3f;

import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.core.view.opengl.util.texture.TextureManager;

public class DataWindowsDisk extends PoincareDisk {

	private float canvasWidth;
	private float canvasHeight;
	private TextureManager textureManager;
	PickingManager pickingManager;
	private GL gl;
	int iUniqueID;
	private float[] levelOfDetailLimits;
	private float displayScaleFactorX = 10;
	private float displayScaleFactorY = 10;
	private GLHyperbolic hyperbolic;
	private float radiussquare;

	public DataWindowsDisk(GLHyperbolic master) {
		super();

		// Create the borders of the discrete DetailLevels:
		this.levelOfDetailLimits = new float[3];
		// display views
		levelOfDetailLimits[0] = 0.8f;
		// display icons
		levelOfDetailLimits[1] = 0.96f;
		// display only lines
		levelOfDetailLimits[2] = 1;

		hyperbolic = master;

		radiussquare = radius * radius;
	}

	public void mouseOverNode(int nodeIndex) {
		this.getNodeByCompareableValue(nodeIndex).highLighted = true;
	}

	public void displayDetailLevels() {

		// drawCircle(levelOfDetailLimits[2] * displayScaleFactorX,
		// canvasWidth / 2, canvasHeight / 2);
		// drawCircle(levelOfDetailLimits[1] * displayScaleFactorX,
		// canvasWidth / 2, canvasHeight / 2);
		// drawCircle(levelOfDetailLimits[0] * displayScaleFactorX,
		// canvasWidth / 2, canvasHeight / 2);
	}

	// returns the detail level of a given distance from the middle
	private int distanceToDetaillevel(float distance) {

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

	public void renderTree(GL gl, TextureManager texManager, PickingManager pickManager,
			int iViewID, float viewingWidth, float viewingHeight) {

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
		float[] size = new float[2];
		if (node != this.getCenteredNode()) {
			size = getMetric(node.getPosition(), nodeSize);
		}

		int iPickingID = pickingManager.getPickingID(iUniqueID, EPickingType.DATAW_NODE,
				node.iComparableValue);

		gl.glPushName(iPickingID);
		// different textures for different detail levels
		if (distanceToDetaillevel(node.getDistanceFromOrigin()) == 2) {
			node.eyeTrackable = false;

			this.eyeTrackableNodes.remove(node);

			size[0] = size[0] * 4;
			size[1] = size[1] * 4;
			Vec3f lowerLeftCorner = new Vec3f((-size[0] + node.getZoomedPosition()[0]
					* displayScaleFactorX + canvasWidth / 2), (-size[1]
					* (canvasHeight / canvasWidth) + node.getZoomedPosition()[1]
					* displayScaleFactorY + canvasHeight / 2), 0);
			Vec3f lowerRightCorner = new Vec3f((size[0] + node.getZoomedPosition()[0]
					* displayScaleFactorX + canvasWidth / 2), (-size[1]
					* (canvasHeight / canvasWidth) + node.getZoomedPosition()[1]
					* displayScaleFactorY + canvasHeight / 2), 0);
			Vec3f upperRightCorner = new Vec3f((size[0] + node.getZoomedPosition()[0]
					* displayScaleFactorX + canvasWidth / 2), (size[1]
					* (canvasHeight / canvasWidth) + node.getZoomedPosition()[1]
					* displayScaleFactorY + canvasHeight / 2), 0);
			Vec3f upperLeftCorner = new Vec3f((-size[0] + node.getZoomedPosition()[0]
					* displayScaleFactorX + canvasWidth / 2), (size[1]
					* (canvasHeight / canvasWidth) + node.getZoomedPosition()[1]
					* displayScaleFactorY + canvasHeight / 2), 0);

			Vec3f scalingPivot = new Vec3f(1, 1, 1);

			textureManager.renderGUITexture(gl, EIconTextures.PATHWAY_ICON,
					lowerLeftCorner, lowerRightCorner, upperRightCorner, upperLeftCorner,
					scalingPivot, 1, 1, 1, 1, 0);
		}
		if (distanceToDetaillevel(node.getDistanceFromOrigin()) == 1) {

			if (distanceToDetaillevel(node.getDistanceFromOrigin()) == 1) {
				if (node == this.getCenteredNode()) {

					size = this.findOptimalCenterNodeSize(node, 0.001f);

				}
			}

			if (this.eyeTrackableNodes.contains(node) == false) {
				this.eyeTrackableNodes.add(node);
			}

			node.eyeTrackable = true;

			float[] position = new float[2];
			position[0] = -size[0] * canvasWidth / 2 + node.getZoomedPosition()[0]
					* displayScaleFactorX + canvasWidth / 2;
			position[1] = -size[1] * canvasHeight / 2 * (canvasHeight / canvasWidth)
					+ node.getZoomedPosition()[1] * displayScaleFactorY + canvasHeight
					/ 2;

			hyperbolic.drawRemoteView(gl, node, position, size[0]);

			// if (node == this.getCenteredNode()) {
			// this.drawCircle(size[0] * canvasWidth / 2 * 1.4142f, size[1]
			// * canvasHeight / 2 * 1.4142f, canvasWidth / 2,
			// canvasHeight / 2);
			// }

			// draw rectangle around the view for debuging reasons
			// gl.glLineWidth((float) lineWidth);
			// gl.glBegin(GL.GL_LINE_STRIP);
			// gl.glColor3i(0, 0, 0);
			//
			// gl.glVertex3f(position[0], position[1], 0);
			// gl.glVertex3f(position[0] + size[0] * canvasWidth, position[1],
			// 0);
			// gl.glVertex3f(position[0] + size[0] * canvasWidth, position[1]
			// + size[1] * canvasHeight, 0);
			// gl.glVertex3f(position[0], position[1] + size[1] * canvasHeight,
			// 0);
			// gl.glVertex3f(position[0], position[1], 0);
			//
			// gl.glEnd();
		}
		gl.glPopName();
	}

	public float[] projectPoint(float[] point, boolean direction) {

		// if direction is true, the point will be projected, otherwise it will
		// be unprojected

		float[] coordinate = new float[2];
		coordinate = point.clone();

		float coordinateLength = coordinate[0] * coordinate[0] + coordinate[1]
				* coordinate[1];
		float coordinateLength2;

		coordinateLength = (float) Math.sqrt((double) coordinateLength);
		float projectionFactor = (2 * radiussquare) / (radiussquare + coordinateLength);
		// if direction is true, the point will be projected, otherwise the
		// point will be unprojected
		if (direction == true) {
			coordinate[0] = coordinate[0] * projectionFactor;
			coordinate[1] = coordinate[1] * projectionFactor;
		} else {
			float[] testCoord = new float[2];
			float error;
			for (coordinateLength = 0; coordinateLength < 100; coordinateLength += 0.01) {

				projectionFactor = (2 * radiussquare) / (radiussquare + coordinateLength);

				coordinate[0] = point[0] * projectionFactor;
				coordinate[1] = point[1] * projectionFactor;

				coordinateLength2 = coordinate[0] * coordinate[0] + coordinate[1]
						* coordinate[1];
				coordinateLength2 = (float) Math.sqrt(coordinateLength2);

				projectionFactor = (2 * radiussquare)
						/ (radiussquare + coordinateLength2);

				testCoord[0] = coordinate[0] * projectionFactor;
				testCoord[1] = coordinate[1] * projectionFactor;

				error = distancePoints(testCoord, point);

				if (error < 0.01) {

					return coordinate;
				}

			}
		}

		return coordinate;
	}

	public void drawLine(PoincareNode node1, PoincareNode node2, int numberOfDetails,
			int mode) {

		if (numberOfDetails != 0) {

			// transfer the start and end point into real coordinates:
			float[] startPoint = new float[2];
			startPoint = this.projectPoint(node1.getZoomedPosition(), false);

			float[] endPoint = new float[2];
			endPoint = projectPoint(node2.getZoomedPosition(), false);
			float length = this.distancePoints(startPoint, endPoint);

			gl.glLineWidth((float) lineWidth);
			gl.glBegin(GL.GL_LINE_STRIP);
			gl.glColor3i(0, 0, 0);
			gl.glVertex3d(node1.getZoomedPosition()[0] * displayScaleFactorX
					+ (canvasWidth / 2), node1.getZoomedPosition()[1]
					* displayScaleFactorY + canvasHeight / 2, -2);

			float[] eV = new float[2];
			eV[0] = endPoint[0] - startPoint[0];
			eV[1] = endPoint[1] - startPoint[1];
			eV = getEV(eV);

			float[] actPosition = new float[2];
			actPosition = startPoint.clone();
			float[] actProjectedPosition = new float[2];

			for (int i = 0; i < numberOfDetails; i++) {
				actPosition[0] = actPosition[0] + eV[0]
						* (length / (float) numberOfDetails);
				actPosition[1] = actPosition[1] + eV[1]
						* (length / (float) numberOfDetails);

				actProjectedPosition = this.projectPoint(actPosition, true);

				gl.glLineWidth((float) lineWidth);
				gl.glBegin(GL.GL_LINE_STRIP);
				gl.glColor3i(0, 0, 0);
				gl.glVertex3d(actProjectedPosition[0] * displayScaleFactorX
						+ (canvasWidth / 2), actProjectedPosition[1]
						* displayScaleFactorY + canvasHeight / 2, -2);
			}

			gl.glVertex3d(node2.getZoomedPosition()[0] * displayScaleFactorX
					+ (canvasWidth / 2), node2.getZoomedPosition()[1]
					* displayScaleFactorY + canvasHeight / 2, -2);
			gl.glEnd();

		} else {
			gl.glLineWidth((float) lineWidth);
			gl.glBegin(GL.GL_LINE_STRIP);
			gl.glColor3i(0, 0, 0);
			gl.glVertex3d(node1.getZoomedPosition()[0] * displayScaleFactorX
					+ (canvasWidth / 2), node1.getZoomedPosition()[1]
					* displayScaleFactorY + canvasHeight / 2, 0);
			gl.glVertex3d(node2.getZoomedPosition()[0] * displayScaleFactorX
					+ (canvasWidth / 2), node2.getZoomedPosition()[1]
					* displayScaleFactorY + canvasHeight / 2, 0);
			gl.glEnd();
		}
	}

	public void drawBackground() {
		drawCircle(1 * this.displayScaleFactorX, 1 * this.displayScaleFactorY,
				canvasWidth / 2, canvasHeight / 2);
	}

	public void drawCircle(float width, float height, float k, float h) {

		// code from http://www.swiftless.com/tutorials/opengl/circle.html
		// //20.2.2010
		float circleX = 0;
		float circleY = 0;
		float i = 0;

		gl.glBegin(GL.GL_LINE_STRIP);
		gl.glColor3f(0, 0, 0);
		for (float counter = 0; counter < 360; counter++) {
			i = counter * (float) Math.PI / 180;

			circleX = width * (float) Math.cos(i);
			circleY = height * (float) Math.sin(i);
			gl.glVertex3d(circleX + k, circleY + h, 0);

			circleX = width * (float) Math.cos(i + (float) Math.PI / 180);
			circleY = height * (float) Math.sin(i + (float) Math.PI / 180);
			gl.glVertex3d(circleX + k, circleY + h, 0);
		}
		gl.glEnd();
	}

	public PoincareNode processEyeTrackerAction(float[] normedEyePosition,
			ArrayList<NodeSlerp> arSlerpActions, boolean mouseControlled) {
		PoincareNode returnNode = null;

		if (mouseControlled) {

			returnNode = findNodeByCoordinate(normedEyePosition, 0);
			if (returnNode != null) {
				float[] emptyPoint = new float[2];
				emptyPoint[0] = 0;
				emptyPoint[1] = 0;
				arSlerpActions
						.add(new NodeSlerp(4, returnNode.getPosition(), emptyPoint));
			}

			return returnNode;
		}

		// if (this.distanceFromOrigin(normedEyePosition) <
		// this.levelOfDetailLimits[0]) {

		returnNode = findNodeByCoordinate(normedEyePosition, 1f);
		// }

		if (this.eyeTrackableNodes.contains(returnNode)) {
			System.out.println("returnnode: " + normedEyePosition[0]);
			float[] emptyPoint = new float[2];
			emptyPoint[0] = 0;
			emptyPoint[1] = 0;
			arSlerpActions.add(new NodeSlerp(4, returnNode.getPosition(), emptyPoint));
			return returnNode;
		} else {
			return null;
		}

		// }

		// if (returnNode != null) {

		// //focus far nodes with the eyetracker
		// System.out.println("outside of direct picking");
		// returnNode = findNodeByCoordinate(offsetFromMiddle,
		// eyeTrackerPrecision);
		//
		// if (returnNode != null) {
		//
		// System.out.println("slerp target:"
		// + returnNode.getPosition()[0] * eyeTrackerBorder
		// + "|" + returnNode.getPosition()[1]
		// * eyeTrackerBorder);
		//
		// Point2D.float eV = getEV(returnNode.getPosition());
		// float overlaping = 0.1;
		//
		// arSlerpActions.add(new NodeSlerp(4, returnNode.getPosition(),
		// new Point2D.float(eV[0]
		// * (eyeTrackerBorder - overlaping), eV[1]
		// * (eyeTrackerBorder - overlaping))));
		//
		// // arSlerpActions.add(new nodeSlerp(4, returnNode.getPosition(),
		// // new Point2D.float(0.5, 0.5)));
		// }
		//
		// }

	}

	public void insertNode(PoincareNode node, PoincareNode parentNode) {

		float[] actualPosition = new float[2];

		this.getTree().addChild(parentNode, node);
		this.moebiusLayoutTree(2);
		actualPosition[0] = node.getPosition()[0] * -1;
		actualPosition[1] = node.getPosition()[1] * -1;
		this.translateTreeMoebius(actualPosition);

	}
}