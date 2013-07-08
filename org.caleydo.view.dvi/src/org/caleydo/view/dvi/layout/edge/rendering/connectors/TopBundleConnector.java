/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.dvi.layout.edge.rendering.connectors;

import gleem.linalg.Vec2f;
import gleem.linalg.Vec3f;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.util.spline.ConnectionBandRenderer;
import org.caleydo.view.dvi.GLDataViewIntegrator;
import org.caleydo.view.dvi.node.IDVINode;

public class TopBundleConnector extends ABundleConnector {
	protected Point2D bundlingPoint;

	public TopBundleConnector(IDVINode node, PixelGLConverter pixelGLConverter,
			ConnectionBandRenderer connectionBandRenderer,
			List<TablePerspective> commonTablePerspectives, int minBandWidth,
			int maxBandWidth, int maxDataAmount, IDVINode otherNode,
			ViewFrustum viewFrustum, GLDataViewIntegrator view) {
		super(node, pixelGLConverter, connectionBandRenderer, commonTablePerspectives,
				minBandWidth, maxBandWidth, maxDataAmount, otherNode, viewFrustum, view);

		calcBundlingPoint();
		calcBandConnectionPoint();
	}

	// protected void calcBundlingPoint() {
	// float summedX = 0;
	//
	// for (TablePerspective tablePerspective : commonTablePerspectives) {
	// Pair<Point2D, Point2D> anchorPoints = node
	// .getTopTablePerspectiveAnchorPoints(tablePerspective);
	// summedX += anchorPoints.getFirst().getX()
	// + anchorPoints.getSecond().getX();
	// }
	//
	// bundlingPoint = new Point2D.Float(summedX
	// / ((float) commonTablePerspectives.size() * 2.0f), (float) node
	// .getBoundingBox().getMaxY() + 0.1f);
	// }
	//
	protected void calcBundlingPoint() {
		float bundlingPositionX = calcXPositionOfBundlingPoint(node,
				commonTablePerspectives);

		float nodeTopPositionY = (float) node.getTopAnchorPoints().getFirst().getY();

		float bundlingPositionY = nodeTopPositionY
				+ pixelGLConverter
						.getGLHeightForPixelHeight(BUNDLING_POINT_NODE_DISTANCE_Y);

		bundlingPoint = new Point2D.Float(bundlingPositionX, bundlingPositionY);
	}

	protected void calcBandConnectionPoint() {
		float bundlingPositionX = (float) bundlingPoint.getX();
		float bundlingPositionXOtherNode = calcXPositionOfBundlingPoint(otherNode,
				commonTablePerspectives);

		float deltaX = bundlingPositionXOtherNode - bundlingPositionX;
		float ratioX = deltaX / viewFrustum.getWidth();

		float edgeAnchorX = bundlingPositionX + ratioX * node.getWidth() / 2.0f;
		float edgeAnchorXOtherNode = bundlingPositionXOtherNode - ratioX
				* otherNode.getWidth() / 2.0f;
		float edgeAnchorY = (float) node.getBoundingBox().getMaxY()
				+ pixelGLConverter
						.getGLHeightForPixelHeight(BOUNDING_BOX_BAND_CONNECTIONPOINT_DISTANCE_Y);
		float edgeAnchorYOtherNode = otherNode.isUpsideDown() ? ((float) otherNode
				.getBoundingBox().getMaxY() + pixelGLConverter
				.getGLHeightForPixelHeight(BOUNDING_BOX_BAND_CONNECTIONPOINT_DISTANCE_Y))
				: (float) otherNode.getBoundingBox().getMinY()
						- pixelGLConverter
								.getGLHeightForPixelHeight(BOUNDING_BOX_BAND_CONNECTIONPOINT_DISTANCE_Y);

		boolean hasEdgeNodeIntersection = doesLineIntersectWithNode(new Point2D.Float(
				edgeAnchorX, edgeAnchorY), new Point2D.Float(edgeAnchorXOtherNode,
				edgeAnchorYOtherNode));

		float bundleDeltaX = (float) (edgeAnchorX - bundlingPoint.getX());
		float deltaXLimit = commonTablePerspectives.size() > 1 ? pixelGLConverter
				.getGLWidthForPixelWidth(bandWidthPixels) : pixelGLConverter
				.getGLWidthForPixelWidth(bandWidthPixels) / 2.0f;
		use4ControlPointsForBandBundleConnection = true;
		if (hasEdgeNodeIntersection || (Math.abs(bundleDeltaX) < deltaXLimit)
				|| (otherNode.getPosition().getY() < node.getPosition().getY())
				|| (otherNode.isUpsideDown() && node.getSpacingX(otherNode) < 0)) {
			use4ControlPointsForBandBundleConnection = false;
			edgeAnchorX = bundlingPositionX;
		}

		// float edgeAnchorY = (float) (nodeAnchorPoints.getFirst().getY() -
		// Math.min(
		// 0.2f * spacingY, pixelGLConverter
		// .getGLHeightForPixelHeight(MAX_NODE_EDGE_ANCHOR_DISTANCE_PIXELS)));
		bandConnectionPoint = new Point2D.Float(edgeAnchorX, edgeAnchorY);
	}

	@Override
	public Point2D getBandConnectionPoint() {
		return bandConnectionPoint;
	}

	@Override
	public Point2D getBandHelperPoint() {
		if (use4ControlPointsForBandBundleConnection)
			return bundlingPoint;

		return new Point2D.Float((float) bandConnectionPoint.getX(), (float) node
				.getPosition().getY());
	}

	@Override
	public void render(GL2 gl, List<Vec3f> bandPoints, boolean isEnd1, Color color) {

		// Point2D bandAnchorPoint1 = null;
		// Point2D bandAnchorPoint2 = null;


		calcBandAnchorPoints(isEnd1, bandPoints);

		if (!isEnd1) {
			Point2D temp = bandAnchorPoint1;
			bandAnchorPoint1 = bandAnchorPoint2;
			bandAnchorPoint2 = temp;
		}

		float vecBandEndX = (float) (bandAnchorPoint2.getX() - bandAnchorPoint1.getX())
				/ bandWidthPixels;
		float vecBandEndY = (float) (bandAnchorPoint2.getY() - bandAnchorPoint1.getY())
				/ bandWidthPixels;

		Vec2f bandVec = new Vec2f(-vecBandEndY, vecBandEndX);
		bandVec.normalize();
		Vec2f bundleVec = new Vec2f(0, 1);

		Point2D leftBandBundleConnectionPoint = null;
		Point2D rightBandBundleConnectionPoint = null;
		Point2D leftBandConnectionPointOffsetAnchor = null;
		Point2D rightBandConnectionPointOffsetAnchor = null;
		Point2D leftBundleConnectionPointOffsetAnchor = null;
		Point2D rightBundleConnectionPointOffsetAnchor = null;
		Point2D leftTablePerspectiveBundleConnectionPoint = null;
		Point2D rightTablePerspectiveBundleConnectionPoint = null;

		float bandWidth = pixelGLConverter.getGLWidthForPixelWidth(bandWidthPixels);

		leftBandBundleConnectionPoint = new Point2D.Float((float) bundlingPoint.getX()
				- bandWidth / 2.0f, (float) bundlingPoint.getY());
		rightBandBundleConnectionPoint = new Point2D.Float((float) bundlingPoint.getX()
				+ bandWidth / 2.0f, (float) bundlingPoint.getY());

		if (!use4ControlPointsForBandBundleConnection) {
			if (bandAnchorPoint1.getY() < bandAnchorPoint2.getY()) {
				rightBandBundleConnectionPoint = bandAnchorPoint1;
				leftBandBundleConnectionPoint = new Point2D.Float(
						(float) bandAnchorPoint1.getX()
								- pixelGLConverter
										.getGLWidthForPixelWidth(bandWidthPixels),
						(float) bandAnchorPoint1.getY());
				rightBandConnectionPointOffsetAnchor = rightBandBundleConnectionPoint;
				float maxY = (float) bandAnchorPoint2.getY()
						+ pixelGLConverter
								.getGLHeightForPixelHeight(MAX_BAND_ANCHOR_OFFSET_DISTANCE_Y_2_CP);
				float minY = (float) leftBandBundleConnectionPoint.getY();
				leftBandConnectionPointOffsetAnchor = calcPointOnLineWithFixedX(
						bandAnchorPoint2, bandVec.x(), bandVec.y(),
						(float) leftBandBundleConnectionPoint.getX(), minY, maxY, minY,
						maxY);
			} else {
				rightBandBundleConnectionPoint = new Point2D.Float(
						(float) bandAnchorPoint2.getX()
								+ pixelGLConverter
										.getGLWidthForPixelWidth(bandWidthPixels),
						(float) bandAnchorPoint2.getY());
				leftBandBundleConnectionPoint = bandAnchorPoint2;

				leftBandConnectionPointOffsetAnchor = leftBandBundleConnectionPoint;
				float maxY = (float) bandAnchorPoint1.getY()
						+ pixelGLConverter
								.getGLHeightForPixelHeight(MAX_BAND_ANCHOR_OFFSET_DISTANCE_Y_2_CP);
				float minY = (float) rightBandBundleConnectionPoint.getY();
				rightBandConnectionPointOffsetAnchor = calcPointOnLineWithFixedX(
						bandAnchorPoint1, bandVec.x(), bandVec.y(),
						(float) rightBandBundleConnectionPoint.getX(), minY, maxY, minY,
						maxY);

			}

			leftTablePerspectiveBundleConnectionPoint = new Point2D.Float(
					(float) leftBandBundleConnectionPoint.getX(),
					(float) bundlingPoint.getY());
			rightTablePerspectiveBundleConnectionPoint = new Point2D.Float(
					(float) rightBandBundleConnectionPoint.getX(),
					(float) bundlingPoint.getY());
		} else {
			float dotProduct = bandVec.dot(bundleVec);
			float distanceScaling = Math.max((1 + dotProduct) * 0.5f, 0.3f);

			// if (!use4ControlPointsForBandBundleConnection)
			// distanceScaling = 1;

			float maxY = (float) bandAnchorPoint2.getY()
					+ pixelGLConverter
							.getGLHeightForPixelHeight(MAX_BAND_ANCHOR_OFFSET_DISTANCE_Y_4_CP);
			float minY = (float) leftBandBundleConnectionPoint.getY()
					+ pixelGLConverter
							.getGLHeightForPixelHeight(MIN_BAND_ANCHOR_OFFSET_DISTANCE_Y_4_CP);
			float fixedX = (float) (bandAnchorPoint2.getX() + (leftBandBundleConnectionPoint
					.getX() - bandAnchorPoint2.getX()) * distanceScaling);
			leftBandConnectionPointOffsetAnchor = calcPointOnLineWithFixedX(
					bandAnchorPoint2, bandVec.x(), bandVec.y(), fixedX, minY, maxY, minY,
					maxY);

			maxY = (float) bandAnchorPoint1.getY()
					+ pixelGLConverter
							.getGLHeightForPixelHeight(MAX_BAND_ANCHOR_OFFSET_DISTANCE_Y_4_CP);
			minY = (float) rightBandBundleConnectionPoint.getY()
					+ pixelGLConverter
							.getGLHeightForPixelHeight(MIN_BAND_ANCHOR_OFFSET_DISTANCE_Y_4_CP);
			fixedX = (float) (bandAnchorPoint1.getX() + (rightBandBundleConnectionPoint
					.getX() - bandAnchorPoint1.getX()) * distanceScaling);
			rightBandConnectionPointOffsetAnchor = calcPointOnLineWithFixedX(
					bandAnchorPoint1, bandVec.x(), bandVec.y(), fixedX, minY, maxY, minY,
					maxY);

			if (bandVec.x() == 0) {
				leftBandConnectionPointOffsetAnchor
						.setLocation(
								leftBandBundleConnectionPoint.getX(),
								leftBandBundleConnectionPoint.getY()
										- (leftBandBundleConnectionPoint.getY() - bandAnchorPoint2
												.getY()) / 2);

				rightBandConnectionPointOffsetAnchor
						.setLocation(
								rightBandBundleConnectionPoint.getX(),
								rightBandBundleConnectionPoint.getY()
										- (rightBandBundleConnectionPoint.getY() - bandAnchorPoint1
												.getY()) / 2);
			}

			// Vec2f anchorVec = new Vec2f(
			// (float) (leftBandBundleConnectionPoint.getX() -
			// bandAnchorPoint2.getX()),
			// (float) (leftBandBundleConnectionPoint.getY() -
			// bandAnchorPoint2.getY()));
			//
			// if ((anchorVec.x() < 0 && bandVec.x() > 0)
			// || (anchorVec.x() > 0 && bandVec.x() < 0)
			// || (anchorVec.y() < 0 && bandVec.y() > 0)
			// || (anchorVec.x() > 0 && bandVec.x() < 0)

			// gl.glBegin(GL.GL_LINES);
			// gl.glVertex3d(0, minY, 3);
			// gl.glVertex3d(5, minY, 3);
			// gl.glEnd();

			if ((float) rightBandConnectionPointOffsetAnchor.getY() <= minY)

			{
				Vec2f miniVec = bandVec.times(pixelGLConverter
						.getGLHeightForPixelHeight(2));

				leftBandConnectionPointOffsetAnchor.setLocation(bandAnchorPoint2.getX()
						+ miniVec.x(), bandAnchorPoint2.getY() + miniVec.y());

				rightBandConnectionPointOffsetAnchor.setLocation(bandAnchorPoint1.getX()
						+ miniVec.x(), bandAnchorPoint1.getY() + miniVec.y());
			}

			// float maxX = (float) bandAnchorPoint1.getX();
			// float minX = (float) leftBandBundleConnectionPoint.getX();
			float fixedY = (float) (rightBandBundleConnectionPoint.getY() - (leftBandBundleConnectionPoint
					.getY() - bandAnchorPoint2.getY()) * 0.3f);
			// leftBundleConnectionPointOffsetAnchor =
			// calcPointOnLineWithFixedY(
			// leftBandBundleConnectionPoint, 0, -1, fixedY, minX, maxY,
			// minX, maxX);
			leftBundleConnectionPointOffsetAnchor = new Point2D.Float(
					(float) leftBandBundleConnectionPoint.getX(), fixedY);

			fixedY = (float) (rightBandBundleConnectionPoint.getY() - (rightBandBundleConnectionPoint
					.getY() - bandAnchorPoint1.getY()) * 0.3f);
			rightBundleConnectionPointOffsetAnchor = new Point2D.Float(
					(float) rightBandBundleConnectionPoint.getX(), fixedY);

			leftTablePerspectiveBundleConnectionPoint = leftBandBundleConnectionPoint;
			rightTablePerspectiveBundleConnectionPoint = rightBandBundleConnectionPoint;
		}

		// gl.glPointSize(3);
		// gl.glColor3f(1, 0, 0);
		// gl.glBegin(GL2.GL_POINTS);
		// gl.glVertex3d(bandAnchorPoint1.getX(), bandAnchorPoint1.getY(), 2);
		// gl.glVertex3d(bandAnchorPoint2.getX(), bandAnchorPoint2.getY(), 2);
		// // gl.glColor3f(0, 0, 1);
		// // gl.glVertex3d(bundlingPoint.getX(), bundlingPoint.getY(), 2);
		// gl.glEnd();
		// // //
		// // // gl.glPointSize(3);
		// gl.glColor3f(0, 1, 0);
		// gl.glBegin(GL2.GL_POINTS);
		//
		// gl.glVertex3d(rightBandConnectionPointOffsetAnchor.getX(),
		// rightBandConnectionPointOffsetAnchor.getY(), 3);
		// gl.glVertex3d(leftBandConnectionPointOffsetAnchor.getX(),
		// leftBandConnectionPointOffsetAnchor.getY(), 3);
		//
		// gl.glEnd();

		List<Pair<Point2D, Point2D>> anchorPoints = new ArrayList<Pair<Point2D, Point2D>>();

		anchorPoints.add(new Pair<Point2D, Point2D>(bandAnchorPoint1, bandAnchorPoint2));
		anchorPoints.add(new Pair<Point2D, Point2D>(rightBandConnectionPointOffsetAnchor,
				leftBandConnectionPointOffsetAnchor));
		if (use4ControlPointsForBandBundleConnection) {
			anchorPoints.add(new Pair<Point2D, Point2D>(
					rightBundleConnectionPointOffsetAnchor,
					leftBundleConnectionPointOffsetAnchor));
		}

		anchorPoints.add(new Pair<Point2D, Point2D>(rightBandBundleConnectionPoint,
				leftBandBundleConnectionPoint));

		connectionBandRenderer.renderComplexBand(gl, anchorPoints, false, color.getRGB(),
				(highlightBand) ? 1 : 0.5f);

		if (!use4ControlPointsForBandBundleConnection) {
			connectionBandRenderer.renderStraightBand(
					gl,
					new float[] {
							(float) rightTablePerspectiveBundleConnectionPoint.getX(),
							(float) rightTablePerspectiveBundleConnectionPoint.getY() },
					new float[] {
							(float) leftTablePerspectiveBundleConnectionPoint.getX(),
							(float) leftTablePerspectiveBundleConnectionPoint.getY() },
					new float[] { (float) rightBandBundleConnectionPoint.getX(),
							(float) rightBandBundleConnectionPoint.getY() }, new float[] {
							(float) leftBandBundleConnectionPoint.getX(),
							(float) leftBandBundleConnectionPoint.getY() }, false, 0,
					color.getRGB(), (highlightBand) ? 1 : 0.5f);
		}

		Point2D prevBandAnchorPoint = leftTablePerspectiveBundleConnectionPoint;

		List<Pair<Double, TablePerspective>> sortedTablePerspectives = new ArrayList<Pair<Double, TablePerspective>>(
				commonTablePerspectives.size());
		for (TablePerspective tablePerspective : commonTablePerspectives) {
			sortedTablePerspectives.add(new Pair<Double, TablePerspective>(node
					.getTopTablePerspectiveAnchorPoints(tablePerspective).getFirst()
					.getX(), tablePerspective));
		}

		Collections.sort(sortedTablePerspectives, Pair.<Double> compareFirst());

		for (int i = 0; i < sortedTablePerspectives.size(); i++) {
			TablePerspective tablePerspective = sortedTablePerspectives.get(i)
					.getSecond();
			anchorPoints = new ArrayList<Pair<Point2D, Point2D>>();
			Pair<Point2D, Point2D> dimensionGroupAnchorPoints = node
					.getTopTablePerspectiveAnchorPoints(tablePerspective);
			Pair<Point2D, Point2D> dimensionGroupAnchorOffsetPoints = new Pair<Point2D, Point2D>();
			Pair<Point2D, Point2D> nodeTopAnchorPoints = node.getTopAnchorPoints();

			float offsetPositionY = (float) (nodeTopAnchorPoints.getFirst().getY() + pixelGLConverter
					.getGLHeightForPixelHeight(TABLEPERSPECTIVE_OFFSET_Y));

			dimensionGroupAnchorOffsetPoints
					.setFirst(new Point2D.Float((float) dimensionGroupAnchorPoints
							.getFirst().getX(), offsetPositionY));

			dimensionGroupAnchorOffsetPoints.setSecond(new Point2D.Float(
					(float) dimensionGroupAnchorPoints.getSecond().getX(),
					offsetPositionY));

			// dimensionGroupAnchorPoints = new Pair<Point2D, Point2D>(
			// dimensionGroupAnchorPoints.getSecond(),
			// dimensionGroupAnchorPoints.getFirst());

			int width = bandWidthMap.get(tablePerspective);

			Point2D nextBandAnchorPoint = null;

			if (i == commonTablePerspectives.size() - 1) {
				nextBandAnchorPoint = rightTablePerspectiveBundleConnectionPoint;
			} else {
				nextBandAnchorPoint = new Point2D.Float(
						(float) prevBandAnchorPoint.getX()
								+ pixelGLConverter.getGLWidthForPixelWidth(width),
						(float) prevBandAnchorPoint.getY());
			}

			float bundlingOffsetPositionY = (float) nodeTopAnchorPoints.getFirst().getY()
					+ pixelGLConverter
							.getGLHeightForPixelHeight(TABLEPERSPECTIVE_TO_BUNDLE_OFFSET_Y);

			Point2D bundlingOffsetPoint1 = new Point2D.Float(
					(float) prevBandAnchorPoint.getX(), bundlingOffsetPositionY);

			Point2D bundlingOffsetPoint2 = new Point2D.Float(
					(float) nextBandAnchorPoint.getX(), bundlingOffsetPositionY);

			anchorPoints.add(dimensionGroupAnchorPoints);
			anchorPoints.add(dimensionGroupAnchorOffsetPoints);
			anchorPoints.add(new Pair<Point2D, Point2D>(bundlingOffsetPoint1,
					bundlingOffsetPoint2));
			anchorPoints.add(new Pair<Point2D, Point2D>(prevBandAnchorPoint,
					nextBandAnchorPoint));

			connectionBandRenderer.renderComplexBand(gl, anchorPoints, false,
					color.getRGB(), (highlightBand) ? 1 : 0.5f);

			prevBandAnchorPoint = nextBandAnchorPoint;
		}
	}
}
