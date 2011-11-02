package org.caleydo.view.datagraph.layout.edge.rendering.connectors;

import gleem.linalg.Vec3f;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.util.spline.ConnectionBandRenderer;
import org.caleydo.view.datagraph.node.IDataGraphNode;

public class BottomBundleConnector extends ABundleConnector {

	protected Point2D bundlingPoint;

	public BottomBundleConnector(IDataGraphNode node,

	PixelGLConverter pixelGLConverter,
			ConnectionBandRenderer connectionBandRenderer,
			List<DataContainer> commonDataContainers, int minBandWidth,
			int maxBandWidth, int maxDataAmount) {
		super(node, pixelGLConverter, connectionBandRenderer,
				commonDataContainers, minBandWidth, maxBandWidth,
				maxDataAmount);
		calcBundlingPoint();
	}

	protected void calcBundlingPoint() {
		float summedX = 0;

		for (DataContainer dataContainer : commonDataContainers) {
			Pair<Point2D, Point2D> anchorPoints = node
					.getBottomDataContainerAnchorPoints(dataContainer);
			summedX += anchorPoints.getFirst().getX()
					+ anchorPoints.getSecond().getX();
		}

		bundlingPoint = new Point2D.Float(summedX
				/ ((float) commonDataContainers.size() * 2.0f), (float) node
				.getBoundingBox().getMinY() - 0.1f);
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

		Point2D leftBandBundleConnecionPoint = null;
		Point2D rightBandBundleConnecionPoint = null;
		Point2D leftBundleConnectionPointOffsetAnchor = null;
		Point2D rightBundleConnectionPointOffsetAnchor = null;

		if (bandAnchorPoint1.getY() > bandAnchorPoint2.getY()) {
			leftBandBundleConnecionPoint = bandAnchorPoint1;
			rightBandBundleConnecionPoint = new Point2D.Float(
					(float) bandAnchorPoint1.getX()
							+ pixelGLConverter
									.getGLWidthForPixelWidth(bandWidth),
					(float) bandAnchorPoint1.getY());
			leftBundleConnectionPointOffsetAnchor = leftBandBundleConnecionPoint;
			float minY = (float) bandAnchorPoint2.getY() - 0.1f;
			float maxY = (float) rightBandBundleConnecionPoint.getY();
			rightBundleConnectionPointOffsetAnchor = calcPointOnLineWithFixedX(
					bandAnchorPoint2, vecNormalX, vecNormalY,
					(float) rightBandBundleConnecionPoint.getX(), minY, maxY,
					minY, maxY);

		} else {
			leftBandBundleConnecionPoint = new Point2D.Float(
					(float) bandAnchorPoint2.getX()
							- pixelGLConverter
									.getGLWidthForPixelWidth(bandWidth),
					(float) bandAnchorPoint2.getY());
			rightBandBundleConnecionPoint = bandAnchorPoint2;

			rightBundleConnectionPointOffsetAnchor = rightBandBundleConnecionPoint;
			float minY = (float) bandAnchorPoint1.getY() - 0.1f;
			float maxY = (float) leftBandBundleConnecionPoint.getY();
			leftBundleConnectionPointOffsetAnchor = calcPointOnLineWithFixedX(
					bandAnchorPoint1, vecNormalX, vecNormalY,
					(float) leftBandBundleConnecionPoint.getX(), minY, maxY,
					minY, maxY);
		}

		List<Pair<Point2D, Point2D>> anchorPoints = new ArrayList<Pair<Point2D, Point2D>>();

		anchorPoints.add(new Pair<Point2D, Point2D>(bandAnchorPoint1,
				bandAnchorPoint2));
		anchorPoints.add(new Pair<Point2D, Point2D>(
				leftBundleConnectionPointOffsetAnchor,
				rightBundleConnectionPointOffsetAnchor));
		anchorPoints.add(new Pair<Point2D, Point2D>(
				leftBandBundleConnecionPoint, rightBandBundleConnecionPoint));

		connectionBandRenderer.renderComplexBand(gl, anchorPoints, false,
				color.getRGB(), (highlightBand) ? 1 : 0.5f);

		Point2D prevBandAnchorPoint = leftBandBundleConnecionPoint;

		List<Pair<Double, DataContainer>> sortedDimensionGroups = new ArrayList<Pair<Double, DataContainer>>(
				commonDataContainers.size());
		for (DataContainer dataContainer : commonDataContainers) {
			sortedDimensionGroups
					.add(new Pair<Double, DataContainer>(node
							.getBottomDataContainerAnchorPoints(
									dataContainer).getFirst().getX(),
							dataContainer));
		}

		Collections.sort(sortedDimensionGroups);

		for (int i = 0; i < sortedDimensionGroups.size(); i++) {
			DataContainer dataContainer = sortedDimensionGroups.get(
					i).getSecond();
			anchorPoints = new ArrayList<Pair<Point2D, Point2D>>();
			Pair<Point2D, Point2D> dimensionGroupAnchorPoints = node
					.getBottomDataContainerAnchorPoints(dataContainer);
			Pair<Point2D, Point2D> dimensionGroupAnchorOffsetPoints = new Pair<Point2D, Point2D>();
			Pair<Point2D, Point2D> nodeBottomAnchorPoints = node
					.getBottomAnchorPoints();
			dimensionGroupAnchorOffsetPoints.setFirst(new Point2D.Float(
					(float) dimensionGroupAnchorPoints.getFirst().getX(),
					(float) nodeBottomAnchorPoints.getFirst().getY() - 0.1f));

			dimensionGroupAnchorOffsetPoints.setSecond(new Point2D.Float(
					(float) dimensionGroupAnchorPoints.getSecond().getX(),
					(float) nodeBottomAnchorPoints.getSecond().getY() - 0.1f));

			int width = bandWidthMap.get(dataContainer);

			Point2D nextBandAnchorPoint = null;

			if (i == commonDataContainers.size() - 1) {
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
					(float) nodeBottomAnchorPoints.getFirst().getY() - 0.17f);

			Point2D bandOffsetPoint2 = new Point2D.Float(
					(float) nextBandAnchorPoint.getX(),
					(float) nodeBottomAnchorPoints.getSecond().getY() - 0.17f);

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

}
