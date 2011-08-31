package org.caleydo.view.datagraph.bandlayout;

import gleem.linalg.Vec3f;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.data.container.ADimensionGroupData;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.util.GLHelperFunctions;
import org.caleydo.core.view.opengl.util.spline.ConnectionBandRenderer;
import org.caleydo.view.datagraph.IDataGraphNode;

public class EdgeBandRenderer {

	private final static int SPACING_PIXELS = 2;
	protected final static int MAX_NODE_EDGE_ANCHOR_DISTANCE_PIXELS = 20;

	protected IDataGraphNode node1;
	protected IDataGraphNode node2;
	protected PixelGLConverter pixelGLConverter;
	protected ViewFrustum viewFrustum;

	public EdgeBandRenderer(IDataGraphNode node1, IDataGraphNode node2,
			PixelGLConverter pixelGLConverter, ViewFrustum viewFrustum) {
		this.node1 = node1;
		this.node2 = node2;
		this.pixelGLConverter = pixelGLConverter;
		this.viewFrustum = viewFrustum;
	}

	public void renderEdgeBand(GL2 gl, IEdgeRoutingStrategy edgeRoutingStrategy) {

		List<ADimensionGroupData> commonDimensionGroupsNode1 = new ArrayList<ADimensionGroupData>();
		List<ADimensionGroupData> commonDimensionGroupsNode2 = new ArrayList<ADimensionGroupData>();

		for (ADimensionGroupData dimensionGroupData1 : node1
				.getDimensionGroups()) {
			for (ADimensionGroupData dimensionGroupData2 : node2
					.getDimensionGroups()) {
				if (dimensionGroupData1.getID() == dimensionGroupData2.getID()) {
					commonDimensionGroupsNode1.add(dimensionGroupData1);
					commonDimensionGroupsNode2.add(dimensionGroupData2);
				}
			}
		}

		ConnectionBandRenderer connectionBandRenderer = new ConnectionBandRenderer();

		connectionBandRenderer.init(gl);

		if (!commonDimensionGroupsNode1.isEmpty()) {
			renderBundledBand(gl, node1, node2, commonDimensionGroupsNode1,
					commonDimensionGroupsNode2, edgeRoutingStrategy,
					connectionBandRenderer);
		} else {

			Point2D position1 = node1.getPosition();
			Point2D position2 = node2.getPosition();

			float deltaX = (float) (position1.getX() - position2.getX());
			float deltaY = (float) (position1.getY() - position2.getY());

			IDataGraphNode leftNode = null;
			IDataGraphNode rightNode = null;
			IDataGraphNode bottomNode = null;
			IDataGraphNode topNode = null;

			if (deltaX < 0) {
				if (deltaY < 0) {
					// -2
					// 1-

					leftNode = node1;
					rightNode = node2;
					bottomNode = node1;
					topNode = node2;
				} else {
					// 1-
					// -2

					leftNode = node1;
					rightNode = node2;
					bottomNode = node2;
					topNode = node1;
				}
			} else {
				if (deltaY < 0) {
					// 2-
					// -1

					leftNode = node2;
					rightNode = node1;
					bottomNode = node1;
					topNode = node2;
				} else {
					// -1
					// 2-

					leftNode = node2;
					rightNode = node1;
					bottomNode = node2;
					topNode = node1;
				}
			}

			float spacingX = (float) ((rightNode.getPosition().getX() - rightNode
					.getWidth() / 2.0f) - (leftNode.getPosition().getX() + leftNode
					.getWidth() / 2.0f));
			float spacingY = (float) ((topNode.getPosition().getY() - topNode
					.getHeight() / 2.0f) - (bottomNode.getPosition().getY() + topNode
					.getHeight() / 2.0f));

			if (spacingX > spacingY) {
				renderHorizontalBand(gl, leftNode, rightNode,
						edgeRoutingStrategy, connectionBandRenderer);
			} else {
				renderVerticalBand(gl, bottomNode, topNode,
						edgeRoutingStrategy, connectionBandRenderer);
			}
		}

	}

	protected void renderBundledBand(GL2 gl, IDataGraphNode node1,
			IDataGraphNode node2,
			List<ADimensionGroupData> commonDimensionGroupsNode1,
			List<ADimensionGroupData> commonDimensionGroupsNode2,
			IEdgeRoutingStrategy edgeRoutingStrategy,
			ConnectionBandRenderer connectionBandRenderer) {

		Point2D bundlingPoint1 = calcBundlingPoint(node1,
				commonDimensionGroupsNode1);
		Point2D bundlingPoint2 = calcBundlingPoint(node2,
				commonDimensionGroupsNode2);

//		GLHelperFunctions.drawPointAt(gl, (float) bundlingPoint1.getX(),
//				(float) bundlingPoint1.getY(), 0);
//		GLHelperFunctions.drawPointAt(gl, (float) bundlingPoint2.getX(),
//				(float) bundlingPoint2.getY(), 0);

		List<Point2D> edgePoints = new ArrayList<Point2D>();

		edgePoints.add(bundlingPoint1);
		edgePoints.add(bundlingPoint2);

		edgeRoutingStrategy.createEdge(edgePoints);

		edgePoints.add(0, new Point2D.Float((float) bundlingPoint1.getX(),
				(float) node1.getPosition().getY()));
		edgePoints.add(new Point2D.Float((float) bundlingPoint2.getX(),
				(float) node2.getPosition().getY()));

		List<Vec3f> bandPoints = connectionBandRenderer.calcInterpolatedBand(
				gl, edgePoints, 20, pixelGLConverter);
		connectionBandRenderer.render(gl, bandPoints);

	}

	protected Point2D calcBundlingPoint(IDataGraphNode node,
			List<ADimensionGroupData> dimensionGroups) {
		float summedX = 0;

		for (ADimensionGroupData dimensionGroupData : dimensionGroups) {
			Pair<Point2D, Point2D> anchorPoints = node
					.getBottomDimensionGroupAnchorPoints(dimensionGroupData);
			summedX += anchorPoints.getFirst().getX()
					+ anchorPoints.getSecond().getX();
		}

		return new Point2D.Float(summedX
				/ ((float) dimensionGroups.size() * 2.0f), (float) node
				.getBoundingBox().getMinY() - 0.1f);
	}

	protected void renderVerticalBand(GL2 gl, IDataGraphNode bottomNode,
			IDataGraphNode topNode, IEdgeRoutingStrategy edgeRoutingStrategy,
			ConnectionBandRenderer connectionBandRenderer) {

		Point2D positionBottom = bottomNode.getPosition();
		Point2D positionTop = topNode.getPosition();

		float spacingY = (float) ((positionTop.getY() - topNode.getHeight() / 2.0f) - (positionBottom
				.getY() + bottomNode.getHeight() / 2.0f));
		float deltaX = (float) (positionBottom.getX() - positionTop.getX());

		ArrayList<Point2D> edgePoints = new ArrayList<Point2D>();

		Pair<Point2D, Point2D> anchorPointsBottom = bottomNode
				.getTopAnchorPoints();

		Pair<Point2D, Point2D> anchorPointsTop = topNode
				.getBottomAnchorPoints();

		float ratioX = deltaX / viewFrustum.getWidth();

		float bottomEdgeAnchorX = (float) positionBottom.getX() - ratioX
				* bottomNode.getWidth() / 2.0f;
		float bottomEdgeAnchorY = (float) (anchorPointsBottom.getFirst().getY() + Math
				.min(0.2f * spacingY,
						pixelGLConverter
								.getGLHeightForPixelHeight(MAX_NODE_EDGE_ANCHOR_DISTANCE_PIXELS)));
		Point2D edgeAnchorPointBottom = new Point2D.Float(bottomEdgeAnchorX,
				bottomEdgeAnchorY);

		float topEdgeAnchorX = (float) positionTop.getX() + ratioX
				* topNode.getWidth() / 2.0f;
		float topEdgeAnchorY = (float) (anchorPointsTop.getFirst().getY() - Math
				.min(0.2f * spacingY,
						pixelGLConverter
								.getGLHeightForPixelHeight(MAX_NODE_EDGE_ANCHOR_DISTANCE_PIXELS)));
		Point2D edgeAnchorPointTop = new Point2D.Float(topEdgeAnchorX,
				topEdgeAnchorY);

		edgePoints.add(edgeAnchorPointBottom);
		edgePoints.add(edgeAnchorPointTop);

		edgeRoutingStrategy.createEdge(edgePoints);

		Point2D edgeRoutingHelperPointBottom = new Point2D.Float(
				(float) edgeAnchorPointBottom.getX(),
				(float) anchorPointsBottom.getFirst().getY());
		Point2D edgeRoutingHelperPointTop = new Point2D.Float(
				(float) edgeAnchorPointTop.getX(), (float) anchorPointsTop
						.getFirst().getY());

		edgePoints.add(edgeRoutingHelperPointTop);
		edgePoints.add(0, edgeRoutingHelperPointBottom);

		float nodeEdgeAnchorSpacingBottom = (float) edgeAnchorPointBottom
				.getY() - (float) anchorPointsBottom.getFirst().getY();

		Pair<Point2D, Point2D> offsetAnchorPointsBottom = new Pair<Point2D, Point2D>();
		offsetAnchorPointsBottom.setFirst(new Point2D.Float(
				(float) anchorPointsBottom.getFirst().getX(),
				(float) anchorPointsBottom.getFirst().getY() + 0.3f
						* nodeEdgeAnchorSpacingBottom));
		offsetAnchorPointsBottom.setSecond(new Point2D.Float(
				(float) anchorPointsBottom.getSecond().getX(),
				(float) anchorPointsBottom.getSecond().getY() + 0.3f
						* nodeEdgeAnchorSpacingBottom));

		float nodeEdgeAnchorSpacingTop = (float) Math.abs(edgeAnchorPointTop
				.getY() - (float) anchorPointsTop.getFirst().getY());

		Pair<Point2D, Point2D> offsetAnchorPointsTop = new Pair<Point2D, Point2D>();
		offsetAnchorPointsTop.setFirst(new Point2D.Float(
				(float) anchorPointsTop.getFirst().getX(),
				(float) anchorPointsTop.getFirst().getY() - 0.3f
						* nodeEdgeAnchorSpacingTop));
		offsetAnchorPointsTop.setSecond(new Point2D.Float(
				(float) anchorPointsTop.getSecond().getX(),
				(float) anchorPointsTop.getSecond().getY() - 0.3f
						* nodeEdgeAnchorSpacingTop));

		gl.glColor4f(0, 0, 0, 0.5f);
		List<Vec3f> bandPoints = connectionBandRenderer.calcInterpolatedBand(
				gl, edgePoints, 20, pixelGLConverter);
		connectionBandRenderer.render(gl, bandPoints);
		gl.glColor4f(0, 0, 0, 1f);
		gl.glBegin(GL2.GL_LINE_STRIP);
		for (int i = 0; i < bandPoints.size() / 2; i++) {
			gl.glVertex3f(bandPoints.get(i).x(), bandPoints.get(i).y(),
					bandPoints.get(i).z());
		}
		gl.glEnd();

		gl.glBegin(GL2.GL_LINE_STRIP);
		for (int i = bandPoints.size() / 2; i < bandPoints.size(); i++) {
			gl.glVertex3f(bandPoints.get(i).x(), bandPoints.get(i).y(),
					bandPoints.get(i).z());
		}
		gl.glEnd();

		// }
		// }

		Point2D bandAnchorPoint1Bottom = new Point2D.Float(bandPoints.get(0)
				.x(), bandPoints.get(0).y());
		Point2D bandAnchorPoint2Bottom = new Point2D.Float(bandPoints.get(
				bandPoints.size() - 1).x(), bandPoints.get(
				bandPoints.size() - 1).y());

		Pair<Point2D, Point2D> bandAnchorPointsBottom = new Pair<Point2D, Point2D>(
				bandAnchorPoint2Bottom, bandAnchorPoint1Bottom);

		float vecXPoint1Bottom = (float) bandAnchorPoint1Bottom.getX()
				- bandPoints.get(1).x();
		float vecYPoint1Bottom = (float) bandAnchorPoint1Bottom.getY()
				- bandPoints.get(1).y();

		float vecXPoint2Bottom = (float) bandAnchorPoint2Bottom.getX()
				- bandPoints.get(bandPoints.size() - 2).x();
		float vecYPoint2Bottom = (float) bandAnchorPoint2Bottom.getY()
				- bandPoints.get(bandPoints.size() - 2).y();

		float lambda1 = 0;
		if (vecXPoint1Bottom != 0)
			lambda1 = vecYPoint1Bottom / vecXPoint1Bottom;
		float lambda2 = 0;
		if (vecXPoint2Bottom != 0)
			lambda2 = vecYPoint2Bottom / vecXPoint2Bottom;

		float bandOffsetAnchorPoint1BottomX = (float) bandAnchorPoint1Bottom
				.getX()
				- (lambda1 == 0 ? 0
						: ((float) bandAnchorPoint1Bottom.getY() - (float) offsetAnchorPointsBottom
								.getFirst().getY()) / lambda1);

		if (bandOffsetAnchorPoint1BottomX > offsetAnchorPointsBottom
				.getSecond().getX()
				|| bandOffsetAnchorPoint1BottomX < offsetAnchorPointsBottom
						.getFirst().getX()) {
			bandOffsetAnchorPoint1BottomX = (float) offsetAnchorPointsBottom
					.getSecond().getX();
		}

		float bandOffsetAnchorPoint2BottomX = (float) bandAnchorPoint2Bottom
				.getX()
				- (lambda2 == 0 ? 0
						: ((float) bandAnchorPoint2Bottom.getY() - (float) offsetAnchorPointsBottom
								.getSecond().getY()) / lambda2);

		if (bandOffsetAnchorPoint2BottomX < offsetAnchorPointsBottom.getFirst()
				.getX()
				|| bandOffsetAnchorPoint2BottomX > offsetAnchorPointsBottom
						.getSecond().getX()) {
			bandOffsetAnchorPoint2BottomX = (float) offsetAnchorPointsBottom
					.getFirst().getX();
		}

		Pair<Point2D, Point2D> bandOffsetAnchorPointsBottom = new Pair<Point2D, Point2D>();
		bandOffsetAnchorPointsBottom.setSecond(new Point2D.Float(
				bandOffsetAnchorPoint1BottomX, (float) offsetAnchorPointsBottom
						.getFirst().getY()));

		bandOffsetAnchorPointsBottom.setFirst(new Point2D.Float(
				bandOffsetAnchorPoint2BottomX, (float) offsetAnchorPointsBottom
						.getSecond().getY()));

		Point2D bandAnchorPoint1Top = new Point2D.Float(bandPoints.get(
				bandPoints.size() / 2 - 1).x(), bandPoints.get(
				bandPoints.size() / 2 - 1).y());
		Point2D bandAnchorPoint2Top = new Point2D.Float(bandPoints.get(
				bandPoints.size() / 2).x(), bandPoints.get(
				bandPoints.size() / 2).y());

		Pair<Point2D, Point2D> bandAnchorPointsTop = new Pair<Point2D, Point2D>(
				bandAnchorPoint2Top, bandAnchorPoint1Top);

		float vecXPoint1Top = (float) bandAnchorPoint1Top.getX()
				- bandPoints.get(bandPoints.size() / 2 - 2).x();
		float vecYPoint1Top = (float) bandAnchorPoint1Top.getY()
				- bandPoints.get(bandPoints.size() / 2 - 2).y();

		float vecXPoint2Top = (float) bandAnchorPoint2Top.getX()
				- bandPoints.get(bandPoints.size() / 2 + 1).x();
		float vecYPoint2Top = (float) bandAnchorPoint2Top.getY()
				- bandPoints.get(bandPoints.size() / 2 + 1).y();

		lambda1 = 0;
		if (vecXPoint1Top != 0)
			lambda1 = vecYPoint1Top / vecXPoint1Top;
		lambda2 = 0;
		if (vecXPoint2Top != 0)
			lambda2 = vecYPoint2Top / vecXPoint2Top;

		float bandOffsetAnchorPoint1TopX = (float) bandAnchorPoint1Top.getX()
				- (lambda1 == 0 ? 0
						: ((float) bandAnchorPoint1Top.getY() - (float) offsetAnchorPointsTop
								.getFirst().getY()) / lambda1);
		if (bandOffsetAnchorPoint1TopX > offsetAnchorPointsTop.getSecond()
				.getX()
				|| bandOffsetAnchorPoint1TopX < offsetAnchorPointsTop
						.getFirst().getX()) {
			bandOffsetAnchorPoint1TopX = (float) offsetAnchorPointsTop
					.getSecond().getX();
		}

		float bandOffsetAnchorPoint2TopX = (float) bandAnchorPoint2Top.getX()
				- (lambda2 == 0 ? 0
						: ((float) bandAnchorPoint2Top.getY() - (float) offsetAnchorPointsTop
								.getSecond().getY()) / lambda2);

		if (bandOffsetAnchorPoint2TopX < offsetAnchorPointsTop.getFirst()
				.getX()
				|| bandOffsetAnchorPoint2TopX > offsetAnchorPointsTop
						.getSecond().getX()) {
			bandOffsetAnchorPoint2TopX = (float) offsetAnchorPointsTop
					.getFirst().getX();
		}

		Pair<Point2D, Point2D> bandOffsetAnchorPointsTop = new Pair<Point2D, Point2D>();
		bandOffsetAnchorPointsTop.setSecond(new Point2D.Float(
				bandOffsetAnchorPoint1TopX, (float) offsetAnchorPointsTop
						.getFirst().getY()));

		bandOffsetAnchorPointsTop.setFirst(new Point2D.Float(
				bandOffsetAnchorPoint2TopX, (float) offsetAnchorPointsTop
						.getSecond().getY()));

		List<Pair<Point2D, Point2D>> bottomBandConnectionPoints = new ArrayList<Pair<Point2D, Point2D>>();
		bottomBandConnectionPoints.add(anchorPointsBottom);
		bottomBandConnectionPoints.add(offsetAnchorPointsBottom);
		bottomBandConnectionPoints.add(bandOffsetAnchorPointsBottom);
		bottomBandConnectionPoints.add(bandAnchorPointsBottom);

		List<Pair<Point2D, Point2D>> topBandConnectionPoints = new ArrayList<Pair<Point2D, Point2D>>();
		topBandConnectionPoints.add(anchorPointsTop);
		topBandConnectionPoints.add(offsetAnchorPointsTop);
		topBandConnectionPoints.add(bandOffsetAnchorPointsTop);
		topBandConnectionPoints.add(bandAnchorPointsTop);

		// GLHelperFunctions.drawPointAt(gl, (float)
		// bandOffsetAnchorPointsBottom
		// .getFirst().getX(), (float) bandOffsetAnchorPointsBottom
		// .getFirst().getY(), 0);
		//
		// GLHelperFunctions.drawPointAt(gl, (float) offsetAnchorPointsBottom
		// .getFirst().getX(), (float) offsetAnchorPointsBottom.getFirst()
		// .getY(), 0);
		//
		// GLHelperFunctions.drawPointAt(gl, (float) bandAnchorPointsBottom
		// .getFirst().getX(), (float) bandAnchorPointsBottom.getFirst()
		// .getY(), 0);

		connectionBandRenderer.renderComplexBand(gl,
				bottomBandConnectionPoints, false, new float[] { 0, 0, 0 },
				0.5f);

		connectionBandRenderer.renderComplexBand(gl, topBandConnectionPoints,
				false, new float[] { 0, 0, 0 }, 0.5f);
	}

	protected void renderHorizontalBand(GL2 gl, IDataGraphNode leftNode,
			IDataGraphNode rightNode, IEdgeRoutingStrategy edgeRoutingStrategy,
			ConnectionBandRenderer connectionBandRenderer) {

		Point2D positionLeft = leftNode.getPosition();
		Point2D positionRight = rightNode.getPosition();
		float spacingX = (float) ((positionRight.getX() - rightNode.getWidth() / 2.0f) - (positionLeft
				.getX() + leftNode.getWidth() / 2.0f));
		float deltaY = (float) (positionLeft.getY() - positionRight.getY());

		Pair<Point2D, Point2D> anchorPointsLeft = leftNode
				.getRightAnchorPoints();
		Pair<Point2D, Point2D> anchorPointsRight = rightNode
				.getLeftAnchorPoints();

		ArrayList<Point2D> edgePoints = new ArrayList<Point2D>();

		float ratioY = deltaY / viewFrustum.getHeight();

		float leftEdgeAnchorY = (float) positionLeft.getY() - ratioY
				* leftNode.getHeight() / 2.0f;
		float leftEdgeAnchorX = (float) (anchorPointsLeft.getFirst().getX() + Math
				.min(0.2f * spacingX,
						pixelGLConverter
								.getGLWidthForPixelWidth(MAX_NODE_EDGE_ANCHOR_DISTANCE_PIXELS)));
		Point2D edgeAnchorPointLeft = new Point2D.Float(leftEdgeAnchorX,
				leftEdgeAnchorY);

		float rightEdgeAnchorY = (float) positionRight.getY() + ratioY
				* rightNode.getHeight() / 2.0f;
		float rightEdgeAnchorX = (float) (anchorPointsRight.getFirst().getX() - Math
				.min(0.2f * spacingX,
						pixelGLConverter
								.getGLWidthForPixelWidth(MAX_NODE_EDGE_ANCHOR_DISTANCE_PIXELS)));
		Point2D edgeAnchorPointRight = new Point2D.Float(rightEdgeAnchorX,
				rightEdgeAnchorY);

		edgePoints.add(edgeAnchorPointLeft);
		edgePoints.add(edgeAnchorPointRight);

		edgeRoutingStrategy.createEdge(edgePoints);

		Point2D bandRoutingHelperPointLeft = new Point2D.Float(
				(float) anchorPointsLeft.getFirst().getX(),
				(float) edgeAnchorPointLeft.getY());
		Point2D bandRoutingHelperPointRight = new Point2D.Float(
				(float) anchorPointsRight.getFirst().getX(),
				(float) edgeAnchorPointRight.getY());

		edgePoints.add(bandRoutingHelperPointRight);
		edgePoints.add(0, bandRoutingHelperPointLeft);

		float nodeEdgeAnchorSpacingLeft = (float) edgeAnchorPointLeft.getX()
				- (float) anchorPointsLeft.getFirst().getX();

		Pair<Point2D, Point2D> offsetAnchorPointsLeft = new Pair<Point2D, Point2D>();
		offsetAnchorPointsLeft.setFirst(new Point2D.Float(
				(float) anchorPointsLeft.getFirst().getX() + 0.3f
						* nodeEdgeAnchorSpacingLeft, (float) anchorPointsLeft
						.getFirst().getY()));
		offsetAnchorPointsLeft.setSecond(new Point2D.Float(
				(float) anchorPointsLeft.getSecond().getX() + 0.3f
						* nodeEdgeAnchorSpacingLeft, (float) anchorPointsLeft
						.getSecond().getY()));

		float nodeEdgeAnchorSpacingRight = (float) Math
				.abs(edgeAnchorPointRight.getX()
						- (float) anchorPointsRight.getFirst().getX());

		Pair<Point2D, Point2D> offsetAnchorPointsRight = new Pair<Point2D, Point2D>();
		offsetAnchorPointsRight.setFirst(new Point2D.Float(
				(float) anchorPointsRight.getFirst().getX() - 0.3f
						* nodeEdgeAnchorSpacingRight, (float) anchorPointsRight
						.getFirst().getY()));
		offsetAnchorPointsRight.setSecond(new Point2D.Float(
				(float) anchorPointsRight.getSecond().getX() - 0.3f
						* nodeEdgeAnchorSpacingRight, (float) anchorPointsRight
						.getSecond().getY()));

		gl.glColor4f(0, 0, 0, 0.5f);
		List<Vec3f> bandPoints = connectionBandRenderer.calcInterpolatedBand(
				gl, edgePoints, 20, pixelGLConverter);
		connectionBandRenderer.render(gl, bandPoints);
		gl.glColor4f(0, 0, 0, 1f);
		gl.glBegin(GL2.GL_LINE_STRIP);
		for (int i = 0; i < bandPoints.size() / 2; i++) {
			gl.glVertex3f(bandPoints.get(i).x(), bandPoints.get(i).y(),
					bandPoints.get(i).z());
		}
		gl.glEnd();

		gl.glBegin(GL2.GL_LINE_STRIP);
		for (int i = bandPoints.size() / 2; i < bandPoints.size(); i++) {
			gl.glVertex3f(bandPoints.get(i).x(), bandPoints.get(i).y(),
					bandPoints.get(i).z());
		}
		gl.glEnd();

		// }

		Point2D bandAnchorPoint1Left = new Point2D.Float(bandPoints.get(0).x(),
				bandPoints.get(0).y());
		Point2D bandAnchorPoint2Left = new Point2D.Float(bandPoints.get(
				bandPoints.size() - 1).x(), bandPoints.get(
				bandPoints.size() - 1).y());

		Pair<Point2D, Point2D> bandAnchorPointsLeft = new Pair<Point2D, Point2D>(
				bandAnchorPoint2Left, bandAnchorPoint1Left);

		float vecXPoint1Left = (float) bandAnchorPoint1Left.getX()
				- bandPoints.get(1).x();
		float vecYPoint1Left = (float) bandAnchorPoint1Left.getY()
				- bandPoints.get(1).y();

		float vecXPoint2Left = (float) bandAnchorPoint2Left.getX()
				- bandPoints.get(bandPoints.size() - 2).x();
		float vecYPoint2Left = (float) bandAnchorPoint2Left.getY()
				- bandPoints.get(bandPoints.size() - 2).y();

		float lambda1 = 0;
		if (vecXPoint1Left != 0)
			lambda1 = vecYPoint1Left / vecXPoint1Left;
		float lambda2 = 0;
		if (vecXPoint2Left != 0)
			lambda2 = vecYPoint2Left / vecXPoint2Left;

		float bandOffsetAnchorPoint1LeftY = (float) bandAnchorPoint1Left.getY()
				- ((float) bandAnchorPoint1Left.getX() - (float) offsetAnchorPointsLeft
						.getSecond().getX()) * lambda1;

		if (bandOffsetAnchorPoint1LeftY < offsetAnchorPointsLeft.getSecond()
				.getY()
				|| bandOffsetAnchorPoint1LeftY > offsetAnchorPointsLeft
						.getFirst().getY()) {
			bandOffsetAnchorPoint1LeftY = (float) offsetAnchorPointsLeft
					.getSecond().getY();
		}

		float bandOffsetAnchorPoint2LeftY = (float) bandAnchorPoint2Left.getY()
				- ((float) bandAnchorPoint2Left.getX() - (float) offsetAnchorPointsLeft
						.getFirst().getX()) * lambda2;

		if (bandOffsetAnchorPoint2LeftY < offsetAnchorPointsLeft.getSecond()
				.getY()
				|| bandOffsetAnchorPoint2LeftY > offsetAnchorPointsLeft
						.getFirst().getY()) {
			bandOffsetAnchorPoint2LeftY = (float) offsetAnchorPointsLeft
					.getFirst().getY();
		}

		Pair<Point2D, Point2D> bandOffsetAnchorPointsLeft = new Pair<Point2D, Point2D>();
		bandOffsetAnchorPointsLeft.setSecond(new Point2D.Float(
				(float) offsetAnchorPointsLeft.getFirst().getX(),
				bandOffsetAnchorPoint1LeftY));

		bandOffsetAnchorPointsLeft.setFirst(new Point2D.Float(
				(float) offsetAnchorPointsLeft.getSecond().getX(),
				bandOffsetAnchorPoint2LeftY));

		Point2D bandAnchorPoint1Right = new Point2D.Float(bandPoints.get(
				bandPoints.size() / 2 - 1).x(), bandPoints.get(
				bandPoints.size() / 2 - 1).y());
		Point2D bandAnchorPoint2Right = new Point2D.Float(bandPoints.get(
				bandPoints.size() / 2).x(), bandPoints.get(
				bandPoints.size() / 2).y());

		Pair<Point2D, Point2D> bandAnchorPointsRight = new Pair<Point2D, Point2D>(
				bandAnchorPoint2Right, bandAnchorPoint1Right);

		float vecXPoint1Right = (float) bandAnchorPoint1Right.getX()
				- bandPoints.get(bandPoints.size() / 2 - 2).x();
		float vecYPoint1Right = (float) bandAnchorPoint1Right.getY()
				- bandPoints.get(bandPoints.size() / 2 - 2).y();

		float vecXPoint2Right = (float) bandAnchorPoint2Right.getX()
				- bandPoints.get(bandPoints.size() / 2 + 1).x();
		float vecYPoint2Right = (float) bandAnchorPoint2Right.getY()
				- bandPoints.get(bandPoints.size() / 2 + 1).y();

		lambda1 = 0;
		if (vecXPoint1Right != 0)
			lambda1 = vecYPoint1Right / vecXPoint1Right;
		lambda2 = 0;
		if (vecXPoint2Right != 0)
			lambda2 = vecYPoint2Right / vecXPoint2Right;

		float bandOffsetAnchorPoint1RightY = (float) bandAnchorPoint1Right
				.getY()
				- ((float) bandAnchorPoint1Right.getX() - (float) offsetAnchorPointsRight
						.getSecond().getX()) * lambda1;

		if (bandOffsetAnchorPoint1RightY < offsetAnchorPointsRight.getSecond()
				.getY()
				|| bandOffsetAnchorPoint1RightY > offsetAnchorPointsRight
						.getFirst().getY()) {
			bandOffsetAnchorPoint1RightY = (float) offsetAnchorPointsRight
					.getSecond().getY();
		}

		float bandOffsetAnchorPoint2RightY = (float) bandAnchorPoint2Right
				.getY()
				- ((float) bandAnchorPoint2Right.getX() - (float) offsetAnchorPointsRight
						.getFirst().getX()) * lambda2;

		if (bandOffsetAnchorPoint2RightY < offsetAnchorPointsRight.getSecond()
				.getY()
				|| bandOffsetAnchorPoint2RightY > offsetAnchorPointsRight
						.getFirst().getY()) {
			bandOffsetAnchorPoint2RightY = (float) offsetAnchorPointsRight
					.getFirst().getY();
		}

		Pair<Point2D, Point2D> bandOffsetAnchorPointsRight = new Pair<Point2D, Point2D>();
		bandOffsetAnchorPointsRight.setSecond(new Point2D.Float(
				(float) offsetAnchorPointsRight.getFirst().getX(),
				bandOffsetAnchorPoint1RightY));

		bandOffsetAnchorPointsRight.setFirst(new Point2D.Float(
				(float) offsetAnchorPointsRight.getSecond().getX(),
				bandOffsetAnchorPoint2RightY));

		List<Pair<Point2D, Point2D>> leftBandConnectionPoints = new ArrayList<Pair<Point2D, Point2D>>();
		leftBandConnectionPoints.add(anchorPointsLeft);
		leftBandConnectionPoints.add(offsetAnchorPointsLeft);
		leftBandConnectionPoints.add(bandOffsetAnchorPointsLeft);
		leftBandConnectionPoints.add(bandAnchorPointsLeft);

		List<Pair<Point2D, Point2D>> rightBandConnectionPoints = new ArrayList<Pair<Point2D, Point2D>>();
		rightBandConnectionPoints.add(anchorPointsRight);
		rightBandConnectionPoints.add(offsetAnchorPointsRight);
		rightBandConnectionPoints.add(bandOffsetAnchorPointsRight);
		rightBandConnectionPoints.add(bandAnchorPointsRight);

		connectionBandRenderer.renderComplexBand(gl, leftBandConnectionPoints,
				false, new float[] { 0, 0, 0 }, 0.5f);

		connectionBandRenderer.renderComplexBand(gl, rightBandConnectionPoints,
				false, new float[] { 0, 0, 0 }, 0.5f);
	}
}
