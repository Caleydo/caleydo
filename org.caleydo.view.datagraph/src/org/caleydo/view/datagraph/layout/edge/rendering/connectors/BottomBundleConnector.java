package org.caleydo.view.datagraph.layout.edge.rendering.connectors;

import gleem.linalg.Vec2f;
import gleem.linalg.Vec3f;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.util.spline.ConnectionBandRenderer;
import org.caleydo.view.datagraph.node.IDataGraphNode;

public class BottomBundleConnector extends ABundleConnector {

	public BottomBundleConnector(IDataGraphNode node,
			PixelGLConverter pixelGLConverter,
			ConnectionBandRenderer connectionBandRenderer,
			List<DataContainer> commonDataContainers, int minBandWidth,
			int maxBandWidth, int maxDataAmount, IDataGraphNode otherNode,
			ViewFrustum viewFrustum) {
		super(node, pixelGLConverter, connectionBandRenderer,
				commonDataContainers, minBandWidth, maxBandWidth,
				maxDataAmount, otherNode, viewFrustum);

		calcBundlingPoint();
		calcBandConnectionPoint();
	}

	protected void calcBundlingPoint() {
		float bundlingPositionX = calcXPositionOfBundlingPoint(node,
				commonDataContainers);

		float nodeBottomPositionY = (float) node.getBottomAnchorPoints()
				.getFirst().getY();

		float bundlingPositionY = nodeBottomPositionY
				- pixelGLConverter.getGLHeightForPixelHeight(30);

		bundlingPoint = new Point2D.Float(bundlingPositionX, bundlingPositionY);
	}

	protected void calcBandConnectionPoint() {
		float bundlingPositionX = (float) bundlingPoint.getX();
		float bundlingPositionXOtherNode = calcXPositionOfBundlingPoint(
				otherNode, commonDataContainers);

		float deltaX = (float) (bundlingPositionXOtherNode - bundlingPositionX);
		float ratioX = deltaX / viewFrustum.getWidth();

		float edgeAnchorX = (float) bundlingPositionX + ratioX
				* node.getWidth() / 2.0f;

		float bundleDeltaX = (float) (edgeAnchorX - bundlingPoint.getX());

		use4ControlPointsForBandBundleConnection = true;
		if ((Math.abs(bundleDeltaX) < pixelGLConverter
				.getGLWidthForPixelWidth(bandWidthPixels) && commonDataContainers
				.size() > 1)
				|| otherNode.getPosition().getY() > node.getPosition().getY()) {
			use4ControlPointsForBandBundleConnection = false;
			edgeAnchorX = bundlingPositionX;
		}

		// float edgeAnchorY = (float) (nodeAnchorPoints.getFirst().getY() -
		// Math.min(
		// 0.2f * spacingY, pixelGLConverter
		// .getGLHeightForPixelHeight(MAX_NODE_EDGE_ANCHOR_DISTANCE_PIXELS)));
		bandConnectionPoint = new Point2D.Float(edgeAnchorX, (float) node
				.getBoundingBox().getMinY() - 0.1f);
	}

	@Override
	public Point2D getBandConnectionPoint() {
		return bandConnectionPoint;
	}

	@Override
	public Point2D getBandHelperPoint() {

		// float bundleDeltaX = (float) (bandConnectionPoint.getX() -
		// bundlingPoint
		// .getX());
		// if (Math.abs(bundleDeltaX) != 0)
		if (use4ControlPointsForBandBundleConnection)
			return bundlingPoint;

		return new Point2D.Float((float) bandConnectionPoint.getX(),
				(float) node.getPosition().getY());
	}

	@Override
	public void render(GL2 gl, List<Vec3f> bandPoints, boolean isEnd1,
			Color color) {

		// Point2D bandAnchorPoint1 = null;
		// Point2D bandAnchorPoint2 = null;

		calcBandAnchorPoints(isEnd1, bandPoints);

		if (!isEnd1) {
			Point2D temp = bandAnchorPoint1;
			bandAnchorPoint1 = bandAnchorPoint2;
			bandAnchorPoint2 = temp;
		}

		float vecBandEndX = (float) (bandAnchorPoint2.getX() - bandAnchorPoint1
				.getX()) / bandWidthPixels;
		float vecBandEndY = (float) (bandAnchorPoint2.getY() - bandAnchorPoint1
				.getY()) / bandWidthPixels;

		// The direction of this vector is correct, since bandAnchorPoint1 is
		// always the left point...
		// float vecNormalX = -vecBandEndY;
		// float vecNormalY = vecBandEndX;

		Vec2f bandVec = new Vec2f(-vecBandEndY, vecBandEndX);
		bandVec.normalize();
		Vec2f bundleVec = new Vec2f(0, -1);

		Point2D leftBandBundleConnectionPoint = null;
		Point2D rightBandBundleConnectionPoint = null;
		Point2D leftBandConnectionPointOffsetAnchor = null;
		Point2D rightBandConnectionPointOffsetAnchor = null;
		Point2D leftBundleConnectionPointOffsetAnchor = null;
		Point2D rightBundleConnectionPointOffsetAnchor = null;
		Point2D leftDataContainerBundleConnectionPoint = null;
		Point2D rightDataContainerBundleConnectionPoint = null;

		float bandWidth = pixelGLConverter
				.getGLWidthForPixelWidth(bandWidthPixels);

		leftBandBundleConnectionPoint = new Point2D.Float(
				(float) bundlingPoint.getX() - bandWidth / 2.0f,
				(float) bundlingPoint.getY());
		rightBandBundleConnectionPoint = new Point2D.Float(
				(float) bundlingPoint.getX() + bandWidth / 2.0f,
				(float) bundlingPoint.getY());

		if (!use4ControlPointsForBandBundleConnection) {

			if (bandAnchorPoint1.getY() > bandAnchorPoint2.getY()) {

				leftBandBundleConnectionPoint = bandAnchorPoint1;
				rightBandBundleConnectionPoint = new Point2D.Float(
						(float) bandAnchorPoint1.getX() + bandWidth,
						(float) bandAnchorPoint1.getY());
				leftBandConnectionPointOffsetAnchor = leftBandBundleConnectionPoint;
				float minY = (float) bandAnchorPoint2.getY() - 0.1f;
				float maxY = (float) rightBandBundleConnectionPoint.getY();
				rightBandConnectionPointOffsetAnchor = calcPointOnLineWithFixedX(
						bandAnchorPoint2, bandVec.x(), bandVec.y(),
						(float) rightBandBundleConnectionPoint.getX(), minY,
						maxY, minY, maxY);

			} else {
				leftBandBundleConnectionPoint = new Point2D.Float(
						(float) bandAnchorPoint2.getX() - bandWidth,
						(float) bandAnchorPoint2.getY());
				rightBandBundleConnectionPoint = bandAnchorPoint2;
				float minY = (float) bandAnchorPoint1.getY() - 0.1f;
				float maxY = (float) leftBandBundleConnectionPoint.getY();
				float fixedX = (float) (leftBandBundleConnectionPoint.getX());
				leftBandConnectionPointOffsetAnchor = calcPointOnLineWithFixedX(
						bandAnchorPoint1, bandVec.x(), bandVec.y(), fixedX,
						minY, maxY, minY, maxY);
				rightBandConnectionPointOffsetAnchor = rightBandBundleConnectionPoint;
			}

			leftDataContainerBundleConnectionPoint = new Point2D.Float(
					(float) leftBandBundleConnectionPoint.getX(),
					(float) bundlingPoint.getY());
			rightDataContainerBundleConnectionPoint = new Point2D.Float(
					(float) rightBandBundleConnectionPoint.getX(),
					(float) bundlingPoint.getY());

		} else {

			float dotProduct = bandVec.dot(bundleVec);
			float distanceScaling = Math.max((1 + dotProduct) * 0.5f, 0.3f);

			if (!use4ControlPointsForBandBundleConnection)
				distanceScaling = 1;

			float minY = (float) bandAnchorPoint1.getY() - 0.5f;
			float maxY = (float) leftBandBundleConnectionPoint.getY() - 0.05f;
			float fixedX = (float) (bandAnchorPoint1.getX() + (leftBandBundleConnectionPoint
					.getX() - bandAnchorPoint1.getX()) * distanceScaling);
			leftBandConnectionPointOffsetAnchor = calcPointOnLineWithFixedX(
					bandAnchorPoint1, bandVec.x(), bandVec.y(), fixedX, minY,
					maxY, minY, maxY);

			minY = (float) bandAnchorPoint2.getY() - 0.5f;
			maxY = (float) rightBandBundleConnectionPoint.getY() - 0.05f;
			fixedX = (float) (bandAnchorPoint2.getX() + (rightBandBundleConnectionPoint
					.getX() - bandAnchorPoint2.getX()) * distanceScaling);
			rightBandConnectionPointOffsetAnchor = calcPointOnLineWithFixedX(
					bandAnchorPoint2, bandVec.x(), bandVec.y(), fixedX, minY,
					maxY, minY, maxY);

			if (bandVec.x() == 0) {
				leftBandConnectionPointOffsetAnchor
						.setLocation(
								leftBandBundleConnectionPoint.getX(),
								leftBandBundleConnectionPoint.getY()
										- (leftBandBundleConnectionPoint.getY() - bandAnchorPoint1
												.getY()) / 2);

				rightBandConnectionPointOffsetAnchor
						.setLocation(
								rightBandBundleConnectionPoint.getX(),
								rightBandBundleConnectionPoint.getY()
										- (rightBandBundleConnectionPoint
												.getY() - bandAnchorPoint2
												.getY()) / 2);
			}

			float minX = (float) bandAnchorPoint1.getX();
			float maxX = (float) leftBandBundleConnectionPoint.getX();
			float fixedY = (float) (rightBandBundleConnectionPoint.getY() - (leftBandBundleConnectionPoint
					.getY() - bandAnchorPoint1.getY()) * 0.3f);
			leftBundleConnectionPointOffsetAnchor = calcPointOnLineWithFixedY(
					leftBandBundleConnectionPoint, 0, -1, fixedY, minX, maxY,
					minX, maxX);
			leftBundleConnectionPointOffsetAnchor = new Point2D.Float(
					(float) leftBandBundleConnectionPoint.getX(), fixedY);

			fixedY = (float) (rightBandBundleConnectionPoint.getY() - (rightBandBundleConnectionPoint
					.getY() - bandAnchorPoint2.getY()) * 0.3f);
			rightBundleConnectionPointOffsetAnchor = new Point2D.Float(
					(float) rightBandBundleConnectionPoint.getX(), fixedY);

			leftDataContainerBundleConnectionPoint = leftBandBundleConnectionPoint;
			rightDataContainerBundleConnectionPoint = rightBandBundleConnectionPoint;
		}

		// gl.glBegin(GL2.GL_LINES);
		// gl.glVertex3d(0, fixedY, 3);
		// gl.glVertex3d(5, fixedY, 3);
		// gl.glEnd();

		// }

		// gl.glPointSize(3);
		// gl.glColor3f(1, 0, 0);
		// gl.glBegin(GL2.GL_POINTS);
		// gl.glVertex3d(bandAnchorPoint1.getX(), bandAnchorPoint1.getY(), 2);
		// gl.glVertex3d(bandAnchorPoint2.getX(), bandAnchorPoint2.getY(), 2);
		// gl.glColor3f(0, 0, 1);
		// gl.glVertex3d(bandConnectionPoint.getX(), bandConnectionPoint.getY(),
		// 2);
		// gl.glEnd();

		// gl.glPointSize(3);
		// gl.glColor3f(0, 1, 0);
		// gl.glBegin(GL2.GL_POINTS);
		// gl.glVertex3d(leftBandConnectionPointOffsetAnchor.getX(),
		// leftBandConnectionPointOffsetAnchor.getY(), 3);
		//
		// gl.glVertex3d(rightBandConnectionPointOffsetAnchor.getX(),
		// rightBandConnectionPointOffsetAnchor.getY(), 3);
		// if (use4ControlPointsForBandBundleConnection) {
		// gl.glVertex3d(leftBundleConnectionPointOffsetAnchor.getX(),
		// leftBundleConnectionPointOffsetAnchor.getY(), 3);
		// gl.glVertex3d(rightBundleConnectionPointOffsetAnchor.getX(),
		// rightBundleConnectionPointOffsetAnchor.getY(), 3);
		// }
		// gl.glColor3f(0, 1, 1);
		// gl.glVertex3d(leftDataContainerBundleConnectionPoint.getX(),
		// leftDataContainerBundleConnectionPoint.getY(), 3);
		// gl.glVertex3d(rightDataContainerBundleConnectionPoint.getX(),
		// rightDataContainerBundleConnectionPoint.getY(), 3);
		// gl.glEnd();

		List<Pair<Point2D, Point2D>> anchorPoints = new ArrayList<Pair<Point2D, Point2D>>();

		anchorPoints.add(new Pair<Point2D, Point2D>(bandAnchorPoint1,
				bandAnchorPoint2));
		anchorPoints.add(new Pair<Point2D, Point2D>(
				leftBandConnectionPointOffsetAnchor,
				rightBandConnectionPointOffsetAnchor));
		if (use4ControlPointsForBandBundleConnection) {
			anchorPoints.add(new Pair<Point2D, Point2D>(
					leftBundleConnectionPointOffsetAnchor,
					rightBundleConnectionPointOffsetAnchor));
		}
		anchorPoints.add(new Pair<Point2D, Point2D>(
				leftBandBundleConnectionPoint, rightBandBundleConnectionPoint));

		connectionBandRenderer.renderComplexBand(gl, anchorPoints, false,
				color.getRGB(), (highlightBand) ? 1 : 0.5f);

		if (!use4ControlPointsForBandBundleConnection) {
			connectionBandRenderer.renderStraightBand(
					gl,
					new float[] {
							(float) rightDataContainerBundleConnectionPoint
									.getX(),
							(float) rightDataContainerBundleConnectionPoint
									.getY() },
					new float[] {
							(float) leftDataContainerBundleConnectionPoint
									.getX(),
							(float) leftDataContainerBundleConnectionPoint
									.getY() }, new float[] {
							(float) rightBandBundleConnectionPoint.getX(),
							(float) rightBandBundleConnectionPoint.getY() },
					new float[] { (float) leftBandBundleConnectionPoint.getX(),
							(float) leftBandBundleConnectionPoint.getY() },
					false, 0, 0, color.getRGB(), (highlightBand) ? 1 : 0.5f);
		}

		Point2D prevBandAnchorPoint = leftDataContainerBundleConnectionPoint;

		List<Pair<Double, DataContainer>> sortedDimensionGroups = new ArrayList<Pair<Double, DataContainer>>(
				commonDataContainers.size());
		for (DataContainer dataContainer : commonDataContainers) {
			sortedDimensionGroups.add(new Pair<Double, DataContainer>(node
					.getBottomDataContainerAnchorPoints(dataContainer)
					.getFirst().getX(), dataContainer));
		}

		Collections.sort(sortedDimensionGroups);

		for (int i = 0; i < sortedDimensionGroups.size(); i++) {
			DataContainer dataContainer = sortedDimensionGroups.get(i)
					.getSecond();
			anchorPoints = new ArrayList<Pair<Point2D, Point2D>>();
			Pair<Point2D, Point2D> dimensionGroupAnchorPoints = node
					.getBottomDataContainerAnchorPoints(dataContainer);
			Pair<Point2D, Point2D> dimensionGroupAnchorOffsetPoints = new Pair<Point2D, Point2D>();
			Pair<Point2D, Point2D> nodeBottomAnchorPoints = node
					.getBottomAnchorPoints();

			float offsetPositionY = (float) (nodeBottomAnchorPoints.getFirst()
					.getY() - 0.03f);

			dimensionGroupAnchorOffsetPoints.setFirst(new Point2D.Float(
					(float) dimensionGroupAnchorPoints.getFirst().getX(),
					offsetPositionY));

			dimensionGroupAnchorOffsetPoints.setSecond(new Point2D.Float(
					(float) dimensionGroupAnchorPoints.getSecond().getX(),
					offsetPositionY));

			int width = bandWidthMap.get(dataContainer);

			Point2D nextBandAnchorPoint = null;

			if (i == commonDataContainers.size() - 1) {
				nextBandAnchorPoint = rightDataContainerBundleConnectionPoint;
			} else {
				nextBandAnchorPoint = new Point2D.Float(
						(float) prevBandAnchorPoint.getX()
								+ pixelGLConverter
										.getGLWidthForPixelWidth(width),
						(float) prevBandAnchorPoint.getY());
			}

			float bandOffsetPositionY = (float) nodeBottomAnchorPoints
					.getFirst().getY() - 0.07f;

			Point2D bandOffsetPoint1 = new Point2D.Float(
					(float) prevBandAnchorPoint.getX(), bandOffsetPositionY);

			Point2D bandOffsetPoint2 = new Point2D.Float(
					(float) nextBandAnchorPoint.getX(), bandOffsetPositionY);

			anchorPoints.add(dimensionGroupAnchorPoints);
			anchorPoints.add(dimensionGroupAnchorOffsetPoints);
			anchorPoints.add(new Pair<Point2D, Point2D>(bandOffsetPoint1,
					bandOffsetPoint2));
			anchorPoints.add(new Pair<Point2D, Point2D>(prevBandAnchorPoint,
					nextBandAnchorPoint));

			connectionBandRenderer.renderComplexBand(gl, anchorPoints, false,
					color.getRGB(), (highlightBand) ? 1 : 0.5f);

			prevBandAnchorPoint = nextBandAnchorPoint;
		}
	}
	// @Override
	// public void render(GL2 gl, List<Vec3f> bandPoints, boolean isEnd1,
	// Color color) {
	//
	// // Point2D bandAnchorPoint1 = null;
	// // Point2D bandAnchorPoint2 = null;
	//
	// calcBandAnchorPoints(isEnd1, bandPoints);
	//
	// if (!isEnd1) {
	// Point2D temp = bandAnchorPoint1;
	// bandAnchorPoint1 = bandAnchorPoint2;
	// bandAnchorPoint2 = temp;
	// }
	//
	// gl.glPointSize(3);
	// gl.glColor3f(1, 0, 0);
	// gl.glBegin(GL2.GL_POINTS);
	// gl.glVertex3d(bandAnchorPoint1.getX(), bandAnchorPoint1.getY(), 2);
	// gl.glVertex3d(bandAnchorPoint2.getX(), bandAnchorPoint2.getY(), 2);
	// gl.glColor3f(0, 0, 1);
	// gl.glVertex3d(bundlingPoint.getX(), bundlingPoint.getY(), 2);
	// gl.glEnd();
	//
	// float vecBandEndX = (float) (bandAnchorPoint2.getX() - bandAnchorPoint1
	// .getX()) / bandWidth;
	// float vecBandEndY = (float) (bandAnchorPoint2.getY() - bandAnchorPoint1
	// .getY()) / bandWidth;
	//
	// float vecNormalX = vecBandEndY;
	// float vecNormalY = -vecBandEndX;
	//
	// Point2D leftBandBundleConnecionPoint = null;
	// Point2D rightBandBundleConnecionPoint = null;
	// Point2D leftBundleConnectionPointOffsetAnchor = null;
	// Point2D rightBundleConnectionPointOffsetAnchor = null;
	//
	// if (bandAnchorPoint1.getY() > bandAnchorPoint2.getY()) {
	// leftBandBundleConnecionPoint = bandAnchorPoint1;
	// rightBandBundleConnecionPoint = new Point2D.Float(
	// (float) bandAnchorPoint1.getX()
	// + pixelGLConverter
	// .getGLWidthForPixelWidth(bandWidth),
	// (float) bandAnchorPoint1.getY());
	// leftBundleConnectionPointOffsetAnchor = leftBandBundleConnecionPoint;
	// float minY = (float) bandAnchorPoint2.getY() - 0.1f;
	// float maxY = (float) rightBandBundleConnecionPoint.getY();
	// rightBundleConnectionPointOffsetAnchor = calcPointOnLineWithFixedX(
	// bandAnchorPoint2, vecNormalX, vecNormalY,
	// (float) rightBandBundleConnecionPoint.getX(), minY, maxY,
	// minY, maxY);
	//
	// } else {
	// leftBandBundleConnecionPoint = new Point2D.Float(
	// (float) bandAnchorPoint2.getX()
	// - pixelGLConverter
	// .getGLWidthForPixelWidth(bandWidth),
	// (float) bandAnchorPoint2.getY());
	// rightBandBundleConnecionPoint = bandAnchorPoint2;
	//
	// rightBundleConnectionPointOffsetAnchor = rightBandBundleConnecionPoint;
	// float minY = (float) bandAnchorPoint1.getY() - 0.1f;
	// float maxY = (float) leftBandBundleConnecionPoint.getY();
	// leftBundleConnectionPointOffsetAnchor = calcPointOnLineWithFixedX(
	// bandAnchorPoint1, vecNormalX, vecNormalY,
	// (float) leftBandBundleConnecionPoint.getX(), minY, maxY,
	// minY, maxY);
	// }
	//
	// List<Pair<Point2D, Point2D>> anchorPoints = new ArrayList<Pair<Point2D,
	// Point2D>>();
	//
	// anchorPoints.add(new Pair<Point2D, Point2D>(bandAnchorPoint1,
	// bandAnchorPoint2));
	// anchorPoints.add(new Pair<Point2D, Point2D>(
	// leftBundleConnectionPointOffsetAnchor,
	// rightBundleConnectionPointOffsetAnchor));
	// anchorPoints.add(new Pair<Point2D, Point2D>(
	// leftBandBundleConnecionPoint, rightBandBundleConnecionPoint));
	//
	// connectionBandRenderer.renderComplexBand(gl, anchorPoints, false,
	// color.getRGB(), (highlightBand) ? 1 : 0.5f);
	//
	// Point2D prevBandAnchorPoint = leftBandBundleConnecionPoint;
	//
	// List<Pair<Double, DataContainer>> sortedDimensionGroups = new
	// ArrayList<Pair<Double, DataContainer>>(
	// commonDataContainers.size());
	// for (DataContainer dataContainer : commonDataContainers) {
	// sortedDimensionGroups.add(new Pair<Double, DataContainer>(node
	// .getBottomDataContainerAnchorPoints(dataContainer)
	// .getFirst().getX(), dataContainer));
	// }
	//
	// Collections.sort(sortedDimensionGroups);
	//
	// for (int i = 0; i < sortedDimensionGroups.size(); i++) {
	// DataContainer dataContainer = sortedDimensionGroups.get(i)
	// .getSecond();
	// anchorPoints = new ArrayList<Pair<Point2D, Point2D>>();
	// Pair<Point2D, Point2D> dimensionGroupAnchorPoints = node
	// .getBottomDataContainerAnchorPoints(dataContainer);
	// Pair<Point2D, Point2D> dimensionGroupAnchorOffsetPoints = new
	// Pair<Point2D, Point2D>();
	// Pair<Point2D, Point2D> nodeBottomAnchorPoints = node
	// .getBottomAnchorPoints();
	// dimensionGroupAnchorOffsetPoints.setFirst(new Point2D.Float(
	// (float) dimensionGroupAnchorPoints.getFirst().getX(),
	// (float) nodeBottomAnchorPoints.getFirst().getY() - 0.1f));
	//
	// dimensionGroupAnchorOffsetPoints.setSecond(new Point2D.Float(
	// (float) dimensionGroupAnchorPoints.getSecond().getX(),
	// (float) nodeBottomAnchorPoints.getSecond().getY() - 0.1f));
	//
	// int width = bandWidthMap.get(dataContainer);
	//
	// Point2D nextBandAnchorPoint = null;
	//
	// if (i == commonDataContainers.size() - 1) {
	// nextBandAnchorPoint = rightBandBundleConnecionPoint;
	// } else {
	// nextBandAnchorPoint = new Point2D.Float(
	// (float) prevBandAnchorPoint.getX()
	// + pixelGLConverter
	// .getGLWidthForPixelWidth(width),
	// (float) prevBandAnchorPoint.getY());
	// }
	//
	// Point2D bandOffsetPoint1 = new Point2D.Float(
	// (float) prevBandAnchorPoint.getX(),
	// (float) nodeBottomAnchorPoints.getFirst().getY() - 0.17f);
	//
	// Point2D bandOffsetPoint2 = new Point2D.Float(
	// (float) nextBandAnchorPoint.getX(),
	// (float) nodeBottomAnchorPoints.getSecond().getY() - 0.17f);
	//
	// anchorPoints.add(dimensionGroupAnchorPoints);
	// anchorPoints.add(dimensionGroupAnchorOffsetPoints);
	// anchorPoints.add(new Pair<Point2D, Point2D>(bandOffsetPoint1,
	// bandOffsetPoint2));
	// anchorPoints.add(new Pair<Point2D, Point2D>(prevBandAnchorPoint,
	// nextBandAnchorPoint));
	//
	// connectionBandRenderer.renderComplexBand(gl, anchorPoints, false,
	// color.getRGB(), (highlightBand) ? 1 : 0.5f);
	//
	// prevBandAnchorPoint = nextBandAnchorPoint;
	// }
	// }

}
