package org.caleydo.view.datagraph.bandlayout;

import gleem.linalg.Vec3f;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.data.container.ADimensionGroupData;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.util.spline.ConnectionBandRenderer;
import org.caleydo.view.datagraph.node.IDataGraphNode;

public class TopBundleConnector extends ABundleConnector {

	protected Point2D bundlingPoint;

	public TopBundleConnector(IDataGraphNode node,

	PixelGLConverter pixelGLConverter,
			ConnectionBandRenderer connectionBandRenderer,
			List<ADimensionGroupData> commonDimensionGroups, int minBandWidth,
			int maxBandWidth, int maxDataAmount) {
		super(node, pixelGLConverter, connectionBandRenderer,
				commonDimensionGroups, minBandWidth, maxBandWidth,
				maxDataAmount);

		calcBundlingPoint();
	}

	protected void calcBundlingPoint() {
		float summedX = 0;

		for (ADimensionGroupData dimensionGroupData : commonDimensionGroups) {
			Pair<Point2D, Point2D> anchorPoints = node
					.getBottomDimensionGroupAnchorPoints(dimensionGroupData);
			summedX += anchorPoints.getFirst().getX()
					+ anchorPoints.getSecond().getX();
		}

		bundlingPoint = new Point2D.Float(summedX
				/ ((float) commonDimensionGroups.size() * 2.0f), (float) node
				.getBoundingBox().getMaxY() + 0.1f);
	}

	@Override
	public Point2D getBandConnectionPoint() {
		return bundlingPoint;
	}

	@Override
	public Point2D getBandHelperPoint() {
		return new Point2D.Float((float) bundlingPoint.getX(), (float) node
				.getPosition().getY());
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
				.getX()) / bandWidth;
		float vecBandEndY = (float) (bandAnchorPoint2.getY() - bandAnchorPoint1
				.getY()) / bandWidth;

		float vecNormalX = vecBandEndY;
		float vecNormalY = -vecBandEndX;

		Point2D rightBandBundleConnecionPoint = null;
		Point2D leftBandBundleConnecionPoint = null;
		Point2D rightBundleConnectionPointOffsetAnchor = null;
		Point2D leftBundleConnectionPointOffsetAnchor = null;

		if (bandAnchorPoint1.getY() < bandAnchorPoint2.getY()) {
			rightBandBundleConnecionPoint = bandAnchorPoint1;
			leftBandBundleConnecionPoint = new Point2D.Float(
					(float) bandAnchorPoint1.getX()
							- pixelGLConverter
									.getGLWidthForPixelWidth(bandWidth),
					(float) bandAnchorPoint1.getY());
			rightBundleConnectionPointOffsetAnchor = rightBandBundleConnecionPoint;
			float maxY = (float) bandAnchorPoint2.getY() + 0.1f;
			float minY = (float) leftBandBundleConnecionPoint.getY();
			leftBundleConnectionPointOffsetAnchor = calcPointOnLineWithFixedX(
					bandAnchorPoint2, vecNormalX, vecNormalY,
					(float) leftBandBundleConnecionPoint.getX(), minY, maxY,
					minY, maxY);

		} else {
			rightBandBundleConnecionPoint = new Point2D.Float(
					(float) bandAnchorPoint2.getX()
							+ pixelGLConverter
									.getGLWidthForPixelWidth(bandWidth),
					(float) bandAnchorPoint2.getY());
			leftBandBundleConnecionPoint = bandAnchorPoint2;

			leftBundleConnectionPointOffsetAnchor = leftBandBundleConnecionPoint;
			float maxY = (float) bandAnchorPoint1.getY() + 0.1f;
			float minY = (float) rightBandBundleConnecionPoint.getY();
			rightBundleConnectionPointOffsetAnchor = calcPointOnLineWithFixedX(
					bandAnchorPoint1, vecNormalX, vecNormalY,
					(float) rightBandBundleConnecionPoint.getX(), minY, maxY,
					minY, maxY);
		}

		List<Pair<Point2D, Point2D>> anchorPoints = new ArrayList<Pair<Point2D, Point2D>>();

		anchorPoints.add(new Pair<Point2D, Point2D>(bandAnchorPoint1,
				bandAnchorPoint2));
		anchorPoints.add(new Pair<Point2D, Point2D>(
				rightBundleConnectionPointOffsetAnchor,
				leftBundleConnectionPointOffsetAnchor));
		anchorPoints.add(new Pair<Point2D, Point2D>(
				rightBandBundleConnecionPoint, leftBandBundleConnecionPoint));

		connectionBandRenderer.renderComplexBand(gl, anchorPoints, false,
				color.getRGB(), 0.5f);

		Point2D prevBandAnchorPoint = leftBandBundleConnecionPoint;

		List<Pair<Double, ADimensionGroupData>> sortedDimensionGroups = new ArrayList<Pair<Double, ADimensionGroupData>>(
				commonDimensionGroups.size());
		for (ADimensionGroupData dimensionGroupData : commonDimensionGroups) {
			sortedDimensionGroups.add(new Pair<Double, ADimensionGroupData>(
					node.getTopDimensionGroupAnchorPoints(dimensionGroupData)
							.getFirst().getX(), dimensionGroupData));
		}

		Collections.sort(sortedDimensionGroups);

		for (int i = 0; i < sortedDimensionGroups.size(); i++) {
			ADimensionGroupData dimensionGroupData = sortedDimensionGroups.get(
					i).getSecond();
			anchorPoints = new ArrayList<Pair<Point2D, Point2D>>();
			Pair<Point2D, Point2D> dimensionGroupAnchorPoints = node
					.getTopDimensionGroupAnchorPoints(dimensionGroupData);
			Pair<Point2D, Point2D> dimensionGroupAnchorOffsetPoints = new Pair<Point2D, Point2D>();
			Pair<Point2D, Point2D> nodeTopAnchorPoints = node
					.getTopAnchorPoints();
			dimensionGroupAnchorOffsetPoints.setFirst(new Point2D.Float(
					(float) dimensionGroupAnchorPoints.getFirst().getX(),
					(float) nodeTopAnchorPoints.getFirst().getY() + 0.1f));

			dimensionGroupAnchorOffsetPoints.setSecond(new Point2D.Float(
					(float) dimensionGroupAnchorPoints.getSecond().getX(),
					(float) nodeTopAnchorPoints.getSecond().getY() + 0.1f));

//			dimensionGroupAnchorPoints = new Pair<Point2D, Point2D>(
//					dimensionGroupAnchorPoints.getSecond(),
//					dimensionGroupAnchorPoints.getFirst());


			int width = bandWidthMap.get(dimensionGroupData);

			Point2D nextBandAnchorPoint = null;

			if (i == commonDimensionGroups.size() - 1) {
				nextBandAnchorPoint = rightBandBundleConnecionPoint;
			} else {
				nextBandAnchorPoint = new Point2D.Float(
						(float) prevBandAnchorPoint.getX()
								+ pixelGLConverter
										.getGLWidthForPixelWidth(width),
						(float) prevBandAnchorPoint.getY());
			}

			Point2D bandOffsetPoint1 = new Point2D.Float(
					(float) prevBandAnchorPoint.getX(),
					(float) nodeTopAnchorPoints.getFirst().getY() + 0.17f);

			Point2D bandOffsetPoint2 = new Point2D.Float(
					(float) nextBandAnchorPoint.getX(),
					(float) nodeTopAnchorPoints.getSecond().getY() + 0.17f);

			anchorPoints.add(dimensionGroupAnchorPoints);
			anchorPoints.add(dimensionGroupAnchorOffsetPoints);
			anchorPoints.add(new Pair<Point2D, Point2D>(bandOffsetPoint1,
					bandOffsetPoint2));
			anchorPoints.add(new Pair<Point2D, Point2D>(prevBandAnchorPoint,
					nextBandAnchorPoint));

			connectionBandRenderer.renderComplexBand(gl, anchorPoints, false,
					color.getRGB(), 0.5f);

			prevBandAnchorPoint = nextBandAnchorPoint;
		}
	}

}
