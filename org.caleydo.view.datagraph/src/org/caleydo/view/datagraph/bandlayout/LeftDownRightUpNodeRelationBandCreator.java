package org.caleydo.view.datagraph.bandlayout;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.virtualarray.ADimensionGroupData;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.view.datagraph.IDataGraphNode;

public class LeftDownRightUpNodeRelationBandCreator extends
		AConnectionBandCreator {

	private final static int SPACING_PIXELS = 2;

	public LeftDownRightUpNodeRelationBandCreator(IDataGraphNode node1,
			IDataGraphNode node2, PixelGLConverter pixelGLConverter) {
		super(node1, node2, pixelGLConverter);
	}

	// @Override
	// public List<List<Pair<Point2D, Point2D>>> calcConnectionBands() {
	// List<BandInfo> bands = new ArrayList<BandInfo>();
	//
	// Point2D position1 = node1.getPosition();
	// Point2D position2 = node2.getPosition();
	// float spacingX = (float) ((position2.getX() - node2.getWidth() / 2.0f) -
	// (position1
	// .getX() + node1.getWidth() / 2.0f));
	// float spacingY = (float) ((position2.getY() - node2.getHeight() / 2.0f) -
	// (position1
	// .getY() + node1.getHeight() / 2.0f));
	//
	// Set<ADimensionGroupData> dimensionGroups1 = node1.getDimensionGroups();
	// Set<ADimensionGroupData> dimensionGroups2 = node2.getDimensionGroups();
	//
	// if (dimensionGroups1 != null && !dimensionGroups1.isEmpty()
	// && dimensionGroups2 != null && !dimensionGroups2.isEmpty()) {
	// float currentBendPosY = (float) position1.getY()
	// - node1.getHeight() / 2.0f - 0.2f;
	// float bandSpacing = pixelGLConverter
	// .getGLHeightForPixelHeight(SPACING_PIXELS);
	// for (ADimensionGroupData dimGroupData1 : dimensionGroups1) {
	// for (ADimensionGroupData dimGroupData2 : dimensionGroups2) {
	// if (dimGroupData1.getID() == dimGroupData2.getID()) {
	//
	// Pair<Point2D, Point2D> dimGroup1AnchorPoints = node1
	// .getBottomDimensionGroupAnchorPoints(dimGroupData1);
	//
	// float bandWidth = (float) (dimGroup1AnchorPoints
	// .getSecond().getX() - dimGroup1AnchorPoints
	// .getFirst().getX());
	// float bandHeight = pixelGLConverter
	// .getGLHeightForPixelHeight(pixelGLConverter
	// .getPixelWidthForGLWidth(bandWidth));
	// Point2D bendAnchorPoint1 = new Point2D.Float(
	// (float) position1.getX() + node1.getWidth()
	// / 2.0f, currentBendPosY);
	// Point2D bendAnchorPoint2 = new Point2D.Float(
	// (float) bendAnchorPoint1.getX(),
	// (float) bendAnchorPoint1.getY() - bandHeight);
	//
	// Pair<Point2D, Point2D> bendAnchorPoints1 = new Pair<Point2D, Point2D>(
	// bendAnchorPoint1, bendAnchorPoint2);
	// Pair<Point2D, Point2D> bendAnchorPoints2 = new Pair<Point2D, Point2D>(
	// bendAnchorPoint2, bendAnchorPoint1);
	//
	// bands.add(new BandInfo(bendAnchorPoints2,
	// dimGroup1AnchorPoints, 0, currentBendPosY
	// - (float) dimGroup1AnchorPoints
	// .getFirst().getY(), true, false));
	//
	// bands.add(new BandInfo(
	// node2.getBottomDimensionGroupAnchorPoints(dimGroupData2),
	// bendAnchorPoints1, -0.2f, 0.2f, false, true));
	//
	// currentBendPosY -= bandHeight + bandSpacing;
	//
	// }
	// }
	// }
	// } else {
	// if (spacingX > spacingY) {
	// bands.add(new BandInfo(node1.getRightAnchorPoints(), node2
	// .getLeftAnchorPoints(), 0.3f * spacingX, -0.3f
	// * spacingX, true, true));
	//
	// } else {
	// bands.add(new BandInfo(node1.getTopAnchorPoints(), node2
	// .getBottomAnchorPoints(), 0.3f * spacingY, -0.3f
	// * spacingY, false, false));
	// }
	// }
	//
	// return bands;
	// }

	@Override
	public List<List<Pair<Point2D, Point2D>>> calcConnectionBands() {
		List<List<Pair<Point2D, Point2D>>> bands = new ArrayList<List<Pair<Point2D, Point2D>>>();

		Point2D position1 = node1.getPosition();
		Point2D position2 = node2.getPosition();
		float spacingX = (float) ((position2.getX() - node2.getWidth() / 2.0f) - (position1
				.getX() + node1.getWidth() / 2.0f));
		float spacingY = (float) ((position2.getY() - node2.getHeight() / 2.0f) - (position1
				.getY() + node1.getHeight() / 2.0f));

		List<ADimensionGroupData> dimensionGroups1 = node1.getDimensionGroups();
		List<ADimensionGroupData> dimensionGroups2 = node2.getDimensionGroups();

		if (dimensionGroups1 != null && !dimensionGroups1.isEmpty()
				&& dimensionGroups2 != null && !dimensionGroups2.isEmpty()) {
			float currentBendPosY = (float) position1.getY()
					- node1.getHeight() / 2.0f - 0.2f;
			float bandSpacing = pixelGLConverter
					.getGLHeightForPixelHeight(SPACING_PIXELS);
			for (ADimensionGroupData dimGroupData1 : dimensionGroups1) {
				for (ADimensionGroupData dimGroupData2 : dimensionGroups2) {
					if (dimGroupData1.getID() == dimGroupData2.getID()) {
						
						List<Pair<Point2D, Point2D>> anchorPoints = new ArrayList<Pair<Point2D, Point2D>>();

						Pair<Point2D, Point2D> dimGroup1AnchorPoints = node1
								.getBottomDimensionGroupAnchorPoints(dimGroupData1);

						float bandWidth = (float) (dimGroup1AnchorPoints
								.getSecond().getX() - dimGroup1AnchorPoints
								.getFirst().getX());
						float bandHeight = pixelGLConverter
								.getGLHeightForPixelHeight(pixelGLConverter
										.getPixelWidthForGLWidth(bandWidth));
						Point2D bendAnchorPoint1 = new Point2D.Float(
								(float) position1.getX() + node1.getWidth()
										/ 2.0f, currentBendPosY);
						Point2D bendAnchorPoint2 = new Point2D.Float(
								(float) bendAnchorPoint1.getX(),
								(float) bendAnchorPoint1.getY() - bandHeight);

						Pair<Point2D, Point2D> bendAnchorPoints1 = new Pair<Point2D, Point2D>(
								bendAnchorPoint1, bendAnchorPoint2);
						Pair<Point2D, Point2D> bendAnchorPoints2 = new Pair<Point2D, Point2D>(
								bendAnchorPoint2, bendAnchorPoint1);
						// Pair<Point2D, Point2D> bendAnchorPoints2 = new
						// Pair<Point2D, Point2D>(
						// bendAnchorPoint1, bendAnchorPoint2);

						anchorPoints
								.add(dimGroup1AnchorPoints);
						anchorPoints.add(bendAnchorPoints1);
						anchorPoints.add(node2.getBottomDimensionGroupAnchorPoints(dimGroupData2));

						// bands.add(new BandInfo(
						// node1.getBottomDimensionGroupAnchorPoints(dimGroupData1),
						// bendAnchorPoints1, -0.2f, -0.2f, false, true));
						//
						// bands.add(new BandInfo(bendAnchorPoints2,
						// dimGroup2AnchorPoints, 0, currentBendPosY
						// - (float) dimGroup2AnchorPoints
						// .getFirst().getY(), true, false));

						currentBendPosY -= bandHeight + bandSpacing;
						bands.add(anchorPoints);
					}
				}
			}

		} else {
			List<Pair<Point2D, Point2D>> anchorPoints = new ArrayList<Pair<Point2D, Point2D>>();
			if (spacingX > spacingY) {
				Pair<Point2D, Point2D> anchorPointsSide1 = node1
						.getRightAnchorPoints();
				Pair<Point2D, Point2D> offsetAnchorPointsSide1 = new Pair<Point2D, Point2D>();
				offsetAnchorPointsSide1.setFirst(new Point2D.Float(
						(float) anchorPointsSide1.getFirst().getX() + 0.3f
								* spacingX, (float) anchorPointsSide1
								.getFirst().getY()));
				offsetAnchorPointsSide1.setSecond(new Point2D.Float(
						(float) anchorPointsSide1.getSecond().getX() + 0.3f
								* spacingX, (float) anchorPointsSide1
								.getSecond().getY()));

				Pair<Point2D, Point2D> anchorPointsSide2 = node2
						.getLeftAnchorPoints();
				Pair<Point2D, Point2D> offsetAnchorPointsSide2 = new Pair<Point2D, Point2D>();
				offsetAnchorPointsSide2.setFirst(new Point2D.Float(
						(float) anchorPointsSide2.getFirst().getX() - 0.3f
								* spacingX, (float) anchorPointsSide2
								.getFirst().getY()));
				offsetAnchorPointsSide2.setSecond(new Point2D.Float(
						(float) anchorPointsSide2.getSecond().getX() - 0.3f
								* spacingX, (float) anchorPointsSide2
								.getSecond().getY()));

				anchorPoints.add(anchorPointsSide1);
				anchorPoints.add(offsetAnchorPointsSide1);
				anchorPoints.add(offsetAnchorPointsSide2);
				anchorPoints.add(anchorPointsSide2);

			} else {
				Pair<Point2D, Point2D> anchorPointsSide1 = node1
						.getTopAnchorPoints();
				Pair<Point2D, Point2D> offsetAnchorPointsSide1 = new Pair<Point2D, Point2D>();
				offsetAnchorPointsSide1.setFirst(new Point2D.Float(
						(float) anchorPointsSide1.getFirst().getX(),
						(float) anchorPointsSide1.getFirst().getY() + 0.3f
								* spacingY));
				offsetAnchorPointsSide1.setSecond(new Point2D.Float(
						(float) anchorPointsSide1.getSecond().getX(),
						(float) anchorPointsSide1.getSecond().getY() + 0.3f
								* spacingY));

				Pair<Point2D, Point2D> anchorPointsSide2 = node2
						.getBottomAnchorPoints();
				Pair<Point2D, Point2D> offsetAnchorPointsSide2 = new Pair<Point2D, Point2D>();
				offsetAnchorPointsSide2.setFirst(new Point2D.Float(
						(float) anchorPointsSide2.getFirst().getX(),
						(float) anchorPointsSide2.getFirst().getY() - 0.3f
								* spacingY));
				offsetAnchorPointsSide2.setSecond(new Point2D.Float(
						(float) anchorPointsSide2.getSecond().getX(),
						(float) anchorPointsSide2.getSecond().getY() - 0.3f
								* spacingY));

				anchorPoints.add(anchorPointsSide1);
				anchorPoints.add(offsetAnchorPointsSide1);
				anchorPoints.add(offsetAnchorPointsSide2);
				anchorPoints.add(anchorPointsSide2);
			}
			bands.add(anchorPoints);
		}

		return bands;
	}

}
