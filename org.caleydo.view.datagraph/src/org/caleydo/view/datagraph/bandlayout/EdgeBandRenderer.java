package org.caleydo.view.datagraph.bandlayout;

import gleem.linalg.Vec3f;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL2;

import org.caleydo.core.data.container.ADimensionGroupData;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.util.spline.ConnectionBandRenderer;
import org.caleydo.view.datagraph.IDataGraphNode;

public class EdgeBandRenderer {

	private final static int SPACING_PIXELS = 2;
	protected final static int MAX_NODE_EDGE_ANCHOR_DISTANCE_PIXELS = 20;
	protected final static int DEFAULT_MAX_BAND_WIDTH = 30;

	protected IDataGraphNode node1;
	protected IDataGraphNode node2;
	protected PixelGLConverter pixelGLConverter;
	protected ViewFrustum viewFrustum;
	protected int maxBandWidth = DEFAULT_MAX_BAND_WIDTH;

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

		Map<ADimensionGroupData, Integer> bandWidthMap = new HashMap<ADimensionGroupData, Integer>();

		int bandWidth = 0;

		for (ADimensionGroupData dimensionGroupData : commonDimensionGroupsNode1) {
			int width = calcDimensionGroupBandWidthPixels(dimensionGroupData);
			bandWidth += width;
			bandWidthMap.put(dimensionGroupData, width);
		}
		for (ADimensionGroupData dimensionGroupData : commonDimensionGroupsNode2) {
			int width = calcDimensionGroupBandWidthPixels(dimensionGroupData);
			bandWidthMap.put(dimensionGroupData, width);
		}

		if (bandWidth > maxBandWidth)
			bandWidth = maxBandWidth;

		// GLHelperFunctions.drawPointAt(gl, (float) bundlingPoint1.getX(),
		// (float) bundlingPoint1.getY(), 0);
		// GLHelperFunctions.drawPointAt(gl, (float) bundlingPoint2.getX(),
		// (float) bundlingPoint2.getY(), 0);

		// TODO: calc dimension group data size in comparison to

		List<Point2D> edgePoints = new ArrayList<Point2D>();

		edgePoints.add(bundlingPoint1);
		edgePoints.add(bundlingPoint2);

		edgeRoutingStrategy.createEdge(edgePoints);

		edgePoints.add(0, new Point2D.Float((float) bundlingPoint1.getX(),
				(float) node1.getPosition().getY()));
		edgePoints.add(new Point2D.Float((float) bundlingPoint2.getX(),
				(float) node2.getPosition().getY()));

		List<Vec3f> bandPoints = connectionBandRenderer.calcInterpolatedBand(
				gl, edgePoints, bandWidth, pixelGLConverter);
		renderBand(gl, connectionBandRenderer, bandPoints);

		Point2D bandStartPointAnchorNode1 = new Point2D.Float(bandPoints.get(0)
				.x(), bandPoints.get(0).y());
		Point2D bandEndPointAnchorNode1 = new Point2D.Float(bandPoints.get(
				bandPoints.size() - 1).x(), bandPoints.get(
				bandPoints.size() - 1).y());

		Point2D bandStartPointAnchorNode2 = new Point2D.Float(bandPoints.get(
				bandPoints.size() / 2).x(), bandPoints.get(
				bandPoints.size() / 2).y());
		Point2D bandEndPointAnchorNode2 = new Point2D.Float(bandPoints.get(
				bandPoints.size() / 2 - 1).x(), bandPoints.get(
				bandPoints.size() / 2 - 1).y());

		renderBundle(gl, node1, bandStartPointAnchorNode1,
				bandEndPointAnchorNode1, bandWidth, commonDimensionGroupsNode1,
				bandWidthMap, connectionBandRenderer);
		renderBundle(gl, node2, bandStartPointAnchorNode2,
				bandEndPointAnchorNode2, bandWidth, commonDimensionGroupsNode2,
				bandWidthMap, connectionBandRenderer);

	}

	protected void renderBundle(GL2 gl, IDataGraphNode node,
			Point2D bandStartPointAnchor, Point2D bandEndPointAnchor,
			int bandWidth, List<ADimensionGroupData> commonDimensionGroups,
			Map<ADimensionGroupData, Integer> bandWidthMap,
			ConnectionBandRenderer connectionBandRenderer) {
		float vecBandEndX = (float) (bandEndPointAnchor.getX() - bandStartPointAnchor
				.getX()) / bandWidth;
		float vecBandEndY = (float) (bandEndPointAnchor.getY() - bandStartPointAnchor
				.getY()) / bandWidth;

		float vecNormalX = vecBandEndY;
		float vecNormalY = -vecBandEndX;

		Point2D leftBandBundleConnecionPoint = null;
		Point2D rightBandBundleConnecionPoint = null;
		Point2D leftBundleConnectionPointOffsetAnchor = null;
		Point2D rightBundleConnectionPointOffsetAnchor = null;

		if (bandStartPointAnchor.getY() > bandEndPointAnchor.getY()) {
			leftBandBundleConnecionPoint = bandStartPointAnchor;
			rightBandBundleConnecionPoint = new Point2D.Float(
					(float) bandStartPointAnchor.getX()
							+ pixelGLConverter
									.getGLWidthForPixelWidth(bandWidth),
					(float) bandStartPointAnchor.getY());
			leftBundleConnectionPointOffsetAnchor = leftBandBundleConnecionPoint;
			float minY = (float) bandEndPointAnchor.getY() - 0.1f;
			float maxY = (float) rightBandBundleConnecionPoint.getY();
			rightBundleConnectionPointOffsetAnchor = calcPointOnLineWithFixedX(
					bandEndPointAnchor, vecNormalX, vecNormalY,
					(float) rightBandBundleConnecionPoint.getX(), minY, maxY,
					minY, maxY);

		} else {
			leftBandBundleConnecionPoint = new Point2D.Float(
					(float) bandEndPointAnchor.getX()
							- pixelGLConverter
									.getGLWidthForPixelWidth(bandWidth),
					(float) bandEndPointAnchor.getY());
			rightBandBundleConnecionPoint = bandEndPointAnchor;

			rightBundleConnectionPointOffsetAnchor = rightBandBundleConnecionPoint;
			float minY = (float) bandStartPointAnchor.getY() - 0.1f;
			float maxY = (float) leftBandBundleConnecionPoint.getY();
			leftBundleConnectionPointOffsetAnchor = calcPointOnLineWithFixedX(
					bandStartPointAnchor, vecNormalX, vecNormalY,
					(float) leftBandBundleConnecionPoint.getX(), minY, maxY,
					minY, maxY);
		}

		List<Pair<Point2D, Point2D>> anchorPoints = new ArrayList<Pair<Point2D, Point2D>>();

		anchorPoints.add(new Pair<Point2D, Point2D>(bandStartPointAnchor,
				bandEndPointAnchor));
		anchorPoints.add(new Pair<Point2D, Point2D>(
				leftBundleConnectionPointOffsetAnchor,
				rightBundleConnectionPointOffsetAnchor));
		anchorPoints.add(new Pair<Point2D, Point2D>(
				leftBandBundleConnecionPoint, rightBandBundleConnecionPoint));

		connectionBandRenderer.renderComplexBand(gl, anchorPoints, false,
				new float[] { 0, 0, 0 }, 0.5f);

		Point2D prevBandAnchorPoint = leftBandBundleConnecionPoint;

		for (ADimensionGroupData dimensionGroupData : commonDimensionGroups) {
			anchorPoints = new ArrayList<Pair<Point2D, Point2D>>();
			Pair<Point2D, Point2D> dimensionGroupAnchorPoints = node
					.getBottomDimensionGroupAnchorPoints(dimensionGroupData);
			Pair<Point2D, Point2D> dimensionGroupAnchorOffsetPoints = new Pair<Point2D, Point2D>();
			dimensionGroupAnchorOffsetPoints
					.setFirst(new Point2D.Float(
							(float) dimensionGroupAnchorPoints.getFirst()
									.getX(), (float) dimensionGroupAnchorPoints
									.getFirst().getY() - 0.1f));

			dimensionGroupAnchorOffsetPoints
					.setSecond(new Point2D.Float(
							(float) dimensionGroupAnchorPoints.getSecond()
									.getX(), (float) dimensionGroupAnchorPoints
									.getSecond().getY() - 0.1f));

			int width = bandWidthMap.get(dimensionGroupData);

			Point2D nextBandAnchorPoint = new Point2D.Float(
					(float) prevBandAnchorPoint.getX()
							+ pixelGLConverter.getGLWidthForPixelWidth(width),
					(float) prevBandAnchorPoint.getY());

			Point2D bandOffsetPoint1 = new Point2D.Float(
					(float) prevBandAnchorPoint.getX(),
					(float) dimensionGroupAnchorPoints.getFirst().getY() - 0.2f);

			Point2D bandOffsetPoint2 = new Point2D.Float(
					(float) nextBandAnchorPoint.getX(),
					(float) dimensionGroupAnchorPoints.getSecond().getY() - 0.2f);

			anchorPoints.add(dimensionGroupAnchorPoints);
			anchorPoints.add(dimensionGroupAnchorOffsetPoints);
			anchorPoints.add(new Pair<Point2D, Point2D>(bandOffsetPoint1,
					bandOffsetPoint2));
			anchorPoints.add(new Pair<Point2D, Point2D>(prevBandAnchorPoint,
					nextBandAnchorPoint));

			connectionBandRenderer.renderComplexBand(gl, anchorPoints, false,
					new float[] { 0, 0, 0 }, 0.5f);

			prevBandAnchorPoint = nextBandAnchorPoint;
		}
	}

	protected int calcDimensionGroupBandWidthPixels(
			ADimensionGroupData dimensionGroupData) {
		// TODO: implement properly

		return 5;
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

		
		List<Vec3f> bandPoints = connectionBandRenderer.calcInterpolatedBand(
				gl, edgePoints, 20, pixelGLConverter);
		
		renderBand(gl, connectionBandRenderer, bandPoints);

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

		Point2D bandOffsetAnchorPoint1Bottom = calcPointOnLineWithFixedY(
				bandAnchorPoint1Bottom, vecXPoint1Bottom, vecYPoint1Bottom,
				(float) offsetAnchorPointsBottom.getFirst().getY(),
				(float) offsetAnchorPointsBottom.getFirst().getX(),
				(float) offsetAnchorPointsBottom.getSecond().getX(),
				(float) offsetAnchorPointsBottom.getSecond().getX(),
				(float) offsetAnchorPointsBottom.getSecond().getX());

		Point2D bandOffsetAnchorPoint2Bottom = calcPointOnLineWithFixedY(
				bandAnchorPoint2Bottom, vecXPoint2Bottom, vecYPoint2Bottom,
				(float) offsetAnchorPointsBottom.getSecond().getY(),
				(float) offsetAnchorPointsBottom.getFirst().getX(),
				(float) offsetAnchorPointsBottom.getSecond().getX(),
				(float) offsetAnchorPointsBottom.getFirst().getX(),
				(float) offsetAnchorPointsBottom.getFirst().getX());

		Pair<Point2D, Point2D> bandOffsetAnchorPointsBottom = new Pair<Point2D, Point2D>(
				bandOffsetAnchorPoint2Bottom, bandOffsetAnchorPoint1Bottom);

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

		Point2D bandOffsetAnchorPoint1Top = calcPointOnLineWithFixedY(
				bandAnchorPoint1Top, vecXPoint1Top, vecYPoint1Top,
				(float) offsetAnchorPointsTop.getFirst().getY(),
				(float) offsetAnchorPointsTop.getFirst().getX(),
				(float) offsetAnchorPointsTop.getSecond().getX(),
				(float) offsetAnchorPointsTop.getSecond().getX(),
				(float) offsetAnchorPointsTop.getSecond().getX());

		Point2D bandOffsetAnchorPoint2Top = calcPointOnLineWithFixedY(
				bandAnchorPoint2Top, vecXPoint2Top, vecYPoint2Top,
				(float) offsetAnchorPointsTop.getSecond().getY(),
				(float) offsetAnchorPointsTop.getFirst().getX(),
				(float) offsetAnchorPointsTop.getSecond().getX(),
				(float) offsetAnchorPointsTop.getFirst().getX(),
				(float) offsetAnchorPointsTop.getFirst().getX());

		Pair<Point2D, Point2D> bandOffsetAnchorPointsTop = new Pair<Point2D, Point2D>(
				bandOffsetAnchorPoint2Top, bandOffsetAnchorPoint1Top);

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

		connectionBandRenderer.renderComplexBand(gl,
				bottomBandConnectionPoints, false, new float[] { 0, 0, 0 },
				0.5f);

		connectionBandRenderer.renderComplexBand(gl, topBandConnectionPoints,
				false, new float[] { 0, 0, 0 }, 0.5f);
	}

	protected void renderBand(GL2 gl,
			ConnectionBandRenderer connectionBandRenderer,
			List<Vec3f> bandPoints) {

		gl.glColor4f(0, 0, 0, 0.5f);
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
		
	}

	protected Point2D calcPointOnLineWithFixedX(Point2D pointOnLine,
			float vecX, float vecY, float fixedX, float minY, float maxY,
			float exceedingMinLimitValY, float exceedingMaxLimitValY) {

		float lambda = 0;
		if (vecX != 0)
			lambda = vecY / vecX;

		float pointY = (float) pointOnLine.getY()
				- ((float) pointOnLine.getX() - fixedX) * lambda;

		if (pointY < minY) {
			pointY = exceedingMinLimitValY;
		}
		if (pointY > maxY) {
			pointY = exceedingMaxLimitValY;
		}

		return new Point2D.Float(fixedX, pointY);
	}

	protected Point2D calcPointOnLineWithFixedY(Point2D pointOnLine,
			float vecX, float vecY, float fixedY, float minX, float maxX,
			float exceedingMinLimitValX, float exceedingMaxLimitValX) {

		float lambda = 0;
		if (vecX != 0)
			lambda = vecY / vecX;

		float pointX = (float) pointOnLine.getX()
				- (lambda == 0 ? 0 : ((float) pointOnLine.getY() - fixedY)
						/ lambda);

		if (pointX < minX) {
			pointX = exceedingMinLimitValX;
		}
		if (pointX > maxX) {
			pointX = exceedingMaxLimitValX;
		}

		return new Point2D.Float(pointX, fixedY);
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

		List<Vec3f> bandPoints = connectionBandRenderer.calcInterpolatedBand(
				gl, edgePoints, 20, pixelGLConverter);
		renderBand(gl, connectionBandRenderer, bandPoints);

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

		Point2D bandOffsetAnchorPoint1Left = calcPointOnLineWithFixedX(
				bandAnchorPoint1Left, vecXPoint1Left, vecYPoint1Left,
				(float) offsetAnchorPointsLeft.getSecond().getX(),
				(float) offsetAnchorPointsLeft.getSecond().getY(),
				(float) offsetAnchorPointsLeft.getFirst().getY(),
				(float) offsetAnchorPointsLeft.getSecond().getY(),
				(float) offsetAnchorPointsLeft.getSecond().getY());

		Point2D bandOffsetAnchorPoint2Left = calcPointOnLineWithFixedX(
				bandAnchorPoint2Left, vecXPoint2Left, vecYPoint2Left,
				(float) offsetAnchorPointsLeft.getFirst().getX(),
				(float) offsetAnchorPointsLeft.getSecond().getY(),
				(float) offsetAnchorPointsLeft.getFirst().getY(),
				(float) offsetAnchorPointsLeft.getFirst().getY(),
				(float) offsetAnchorPointsLeft.getFirst().getY());

		Pair<Point2D, Point2D> bandOffsetAnchorPointsLeft = new Pair<Point2D, Point2D>(
				bandOffsetAnchorPoint2Left, bandOffsetAnchorPoint1Left);

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

		Point2D bandOffsetAnchorPoint1Right = calcPointOnLineWithFixedX(
				bandAnchorPoint1Right, vecXPoint1Right, vecYPoint1Right,
				(float) offsetAnchorPointsRight.getSecond().getX(),
				(float) offsetAnchorPointsRight.getSecond().getY(),
				(float) offsetAnchorPointsRight.getFirst().getY(),
				(float) offsetAnchorPointsRight.getSecond().getY(),
				(float) offsetAnchorPointsRight.getSecond().getY());

		Point2D bandOffsetAnchorPoint2Right = calcPointOnLineWithFixedX(
				bandAnchorPoint2Right, vecXPoint2Right, vecYPoint2Right,
				(float) offsetAnchorPointsRight.getFirst().getX(),
				(float) offsetAnchorPointsRight.getSecond().getY(),
				(float) offsetAnchorPointsRight.getFirst().getY(),
				(float) offsetAnchorPointsRight.getFirst().getY(),
				(float) offsetAnchorPointsRight.getFirst().getY());

		Pair<Point2D, Point2D> bandOffsetAnchorPointsRight = new Pair<Point2D, Point2D>(
				bandOffsetAnchorPoint2Right, bandOffsetAnchorPoint1Right);

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

	public int getMaxBandWidth() {
		return maxBandWidth;
	}

	public void setMaxBandWidth(int maxBandWidth) {
		this.maxBandWidth = maxBandWidth;
	}
}
