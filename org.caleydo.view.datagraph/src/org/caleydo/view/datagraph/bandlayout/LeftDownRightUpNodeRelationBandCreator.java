package org.caleydo.view.datagraph.bandlayout;

import gleem.linalg.Vec3f;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.util.spline.ConnectionBandRenderer;
import org.caleydo.view.datagraph.IDataGraphNode;

public class LeftDownRightUpNodeRelationBandCreator extends AEdgeBandRenderer {

	private final static int SPACING_PIXELS = 2;

	public LeftDownRightUpNodeRelationBandCreator(IDataGraphNode node1,
			IDataGraphNode node2, PixelGLConverter pixelGLConverter,
			ViewFrustum viewFrustum) {
		super(node1, node2, pixelGLConverter, viewFrustum);
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
	public void renderEdgeBand(GL2 gl, IEdgeRoutingStrategy edgeRoutingStrategy) {
		List<List<Pair<Point2D, Point2D>>> bands = new ArrayList<List<Pair<Point2D, Point2D>>>();

		Point2D position1 = node1.getPosition();
		Point2D position2 = node2.getPosition();
		float spacingX = (float) ((position2.getX() - node2.getWidth() / 2.0f) - (position1
				.getX() + node1.getWidth() / 2.0f));
		float spacingY = (float) ((position2.getY() - node2.getHeight() / 2.0f) - (position1
				.getY() + node1.getHeight() / 2.0f));
		float deltaX = (float) (position1.getX() - position2.getX());
		float deltaY = (float) (position1.getY() - position2.getY());

		// List<ADimensionGroupData> dimensionGroups1 =
		// node1.getDimensionGroups();
		// List<ADimensionGroupData> dimensionGroups2 =
		// node2.getDimensionGroups();
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
		// List<Pair<Point2D, Point2D>> anchorPoints = new
		// ArrayList<Pair<Point2D, Point2D>>();
		//
		// Pair<Point2D, Point2D> dimGroup1AnchorPoints = node1
		// .getBottomDimensionGroupAnchorPoints(dimGroupData1);
		// Pair<Point2D, Point2D> dimGroup1AnchorPointsSwapped = new
		// Pair<Point2D, Point2D>(
		// dimGroup1AnchorPoints.getSecond(),dimGroup1AnchorPoints.getFirst());
		// Pair<Point2D, Point2D> dimGroup2AnchorPoints = node2
		// .getBottomDimensionGroupAnchorPoints(dimGroupData2);
		// Pair<Point2D, Point2D> dimGroup2AnchorPointsSwapped = new
		// Pair<Point2D, Point2D>(
		// dimGroup2AnchorPoints.getSecond(),dimGroup2AnchorPoints.getFirst());
		//
		// float vecX1 = (float) dimGroup2AnchorPoints.getFirst()
		// .getX()
		// - (float) dimGroup1AnchorPoints.getFirst()
		// .getX();
		// float vecX2 = (float) dimGroup2AnchorPoints.getSecond()
		// .getX()
		// - (float) dimGroup1AnchorPoints.getSecond()
		// .getX();
		// float vecY1 = (float) dimGroup2AnchorPoints.getFirst()
		// .getY()
		// - (float) dimGroup1AnchorPoints.getFirst()
		// .getY();
		// float vecY2 = (float) dimGroup2AnchorPoints.getSecond()
		// .getY()
		// - (float) dimGroup1AnchorPoints.getSecond()
		// .getY();
		//
		// int pixelVecX1 = pixelGLConverter.getPixelWidthForGLWidth(vecX1);
		// int pixelVecY1 = pixelGLConverter.getPixelHeightForGLHeight(vecY1);
		//
		// if(pixelVecX1 > pixelVecY1) {
		// float ratio = (float)pixelVecY1 / (float)pixelVecX1;
		// pixelVecX1 = pixelVecY1;
		//
		// pixelVecY1 = (int) (pixelVecY1 * ratio);
		// } else {
		// float ratio = (float)pixelVecX1 / (float)pixelVecY1;
		// pixelVecY1 = pixelVecX1;
		//
		// pixelVecX1 = (int) (pixelVecX1 * ratio);
		// }
		//
		// float dirVecY1 = -pixelGLConverter
		// .getGLHeightForPixelHeight(pixelVecX1);
		// float dirVecY2 = -pixelGLConverter
		// .getGLHeightForGLWidth(vecX2);
		// float dirVecX1 = pixelGLConverter
		// .getGLWidthForPixelWidth(pixelVecY1);
		// float dirVecX2 = pixelGLConverter
		// .getGLWidthForGLHeight(vecY2);
		//
		// anchorPoints.add(dimGroup1AnchorPoints);
		//
		// Point2D controlPoint1 = new Point2D.Float(
		// (float) dimGroup1AnchorPoints.getFirst().getX()
		// + dirVecX1,
		// (float) dimGroup1AnchorPoints.getFirst().getY()
		// + dirVecY1);
		// Point2D controlPoint2 = new Point2D.Float(
		// (float) dimGroup1AnchorPoints.getSecond().getX()
		// + dirVecX2,
		// (float) dimGroup1AnchorPoints.getSecond().getY()
		// + dirVecY2);
		//
		// Pair<Point2D, Point2D> controlPoints = new Pair<Point2D, Point2D>(
		// controlPoint1, controlPoint2);
		//
		// anchorPoints.add(controlPoints);
		//
		// controlPoint1 = new Point2D.Float(
		// (float) dimGroup2AnchorPoints.getFirst().getX()
		// + dirVecX1,
		// (float) dimGroup2AnchorPoints.getFirst().getY()
		// + dirVecY1);
		// controlPoint2 = new Point2D.Float(
		// (float) dimGroup2AnchorPoints.getSecond().getX()
		// + dirVecX2,
		// (float) dimGroup2AnchorPoints.getSecond().getY()
		// + dirVecY2);
		//
		// controlPoints = new Pair<Point2D, Point2D>(
		// controlPoint1, controlPoint2);
		//
		// anchorPoints.add(controlPoints);
		//
		// anchorPoints.add(dimGroup2AnchorPoints);
		//
		// // float bandWidth = (float) (dimGroup1AnchorPoints
		// // .getSecond().getX() - dimGroup1AnchorPoints
		// // .getFirst().getX());
		// // float bandHeight = pixelGLConverter
		// // .getGLHeightForPixelHeight(pixelGLConverter
		// // .getPixelWidthForGLWidth(bandWidth));
		// // Point2D bendAnchorPoint1 = new Point2D.Float(
		// // (float) position1.getX() + node1.getWidth()
		// // / 2.0f, currentBendPosY);
		// // Point2D bendAnchorPoint2 = new Point2D.Float(
		// // (float) bendAnchorPoint1.getX(),
		// // (float) bendAnchorPoint1.getY() - bandHeight);
		// //
		// // Pair<Point2D, Point2D> bendAnchorPoints1 = new Pair<Point2D,
		// Point2D>(
		// // bendAnchorPoint1, bendAnchorPoint2);
		// // Pair<Point2D, Point2D> bendAnchorPoints2 = new Pair<Point2D,
		// Point2D>(
		// // bendAnchorPoint2, bendAnchorPoint1);
		// // // Pair<Point2D, Point2D> bendAnchorPoints2 = new
		// // // Pair<Point2D, Point2D>(
		// // // bendAnchorPoint1, bendAnchorPoint2);
		// //
		// // anchorPoints.add(dimGroup1AnchorPoints);
		// // anchorPoints.add(bendAnchorPoints1);
		// // anchorPoints
		// // .add(node2
		// // .getBottomDimensionGroupAnchorPoints(dimGroupData2));
		// //
		// // // bands.add(new BandInfo(
		// // // node1.getBottomDimensionGroupAnchorPoints(dimGroupData1),
		// // // bendAnchorPoints1, -0.2f, -0.2f, false, true));
		// // //
		// // // bands.add(new BandInfo(bendAnchorPoints2,
		// // // dimGroup2AnchorPoints, 0, currentBendPosY
		// // // - (float) dimGroup2AnchorPoints
		// // // .getFirst().getY(), true, false));
		// //
		// // currentBendPosY -= bandHeight + bandSpacing;
		// bands.add(anchorPoints);
		// }
		// }
		// }
		//
		// } else {
		ArrayList<Point2D> edgePoints = new ArrayList<Point2D>();
		List<Pair<Point2D, Point2D>> anchorPoints = new ArrayList<Pair<Point2D, Point2D>>();
		Pair<Point2D, Point2D> anchorPointsSide1;
		Pair<Point2D, Point2D> anchorPointsSide2;
		Pair<Point2D, Point2D> offsetAnchorPointsSide1;

		if (spacingX > spacingY) {

			anchorPointsSide1 = node1.getRightAnchorPoints();
			anchorPointsSide2 = node2.getLeftAnchorPoints();

			float ratioY = deltaY / viewFrustum.getHeight();

			float node1BandAnchorY = (float) position1.getY() - ratioY
					* node1.getHeight() / 2.0f;
			float node1BandAnchorX = (float) (anchorPointsSide1.getFirst()
					.getX() + Math
					.min(0.2f * spacingX,
							pixelGLConverter
									.getGLWidthForPixelWidth(MAX_NODE_EDGE_ANCHOR_DISTANCE_PIXELS)));
			Point2D bandAnchorPoint1 = new Point2D.Float(node1BandAnchorX,
					node1BandAnchorY);

			float node2BandAnchorY = (float) position2.getY() + ratioY
					* node2.getHeight() / 2.0f;
			float node2BandAnchorX = (float) (anchorPointsSide2.getFirst()
					.getX() - Math
					.min(0.2f * spacingX,
							pixelGLConverter
									.getGLWidthForPixelWidth(MAX_NODE_EDGE_ANCHOR_DISTANCE_PIXELS)));
			Point2D bandAnchorPoint2 = new Point2D.Float(node2BandAnchorX,
					node2BandAnchorY);

			edgePoints.add(bandAnchorPoint1);
			edgePoints.add(bandAnchorPoint2);

			edgeRoutingStrategy.createEdge(edgePoints);

			Point2D bandRoutingHelperPoint1 = new Point2D.Float(
					(float) anchorPointsSide1.getFirst().getX(),
					(float) bandAnchorPoint1.getY());
			Point2D bandRoutingHelperPoint2 = new Point2D.Float(
					(float) anchorPointsSide2.getFirst().getX(),
					(float) bandAnchorPoint2.getY());

			edgePoints.add(bandRoutingHelperPoint2);
			edgePoints.add(0, bandRoutingHelperPoint1);

			float nodeEdgeAnchorSpacing1 = (float) bandAnchorPoint1.getX()
					- (float) anchorPointsSide1.getFirst().getX();

			offsetAnchorPointsSide1 = new Pair<Point2D, Point2D>();
			offsetAnchorPointsSide1.setFirst(new Point2D.Float(
					(float) anchorPointsSide1.getFirst().getX() + 0.3f
							* nodeEdgeAnchorSpacing1, (float) anchorPointsSide1
							.getFirst().getY()));
			offsetAnchorPointsSide1.setSecond(new Point2D.Float(
					(float) anchorPointsSide1.getSecond().getX() + 0.3f
							* nodeEdgeAnchorSpacing1, (float) anchorPointsSide1
							.getSecond().getY()));
			//
			// Pair<Point2D, Point2D> offsetAnchorPointsSide2 = new
			// Pair<Point2D, Point2D>();
			// offsetAnchorPointsSide2.setFirst(new Point2D.Float(
			// (float) anchorPointsSide2.getFirst().getX() - 0.3f
			// * spacingX, (float) anchorPointsSide2.getFirst()
			// .getY()));
			// offsetAnchorPointsSide2.setSecond(new Point2D.Float(
			// (float) anchorPointsSide2.getSecond().getX() - 0.3f
			// * spacingX, (float) anchorPointsSide2.getSecond()
			// .getY()));
			//
			// anchorPoints.add(anchorPointsSide1);
			// anchorPoints.add(offsetAnchorPointsSide1);
			// anchorPoints.add(offsetAnchorPointsSide2);
			// anchorPoints.add(anchorPointsSide2);

		} else {
			anchorPointsSide1 = node1.getTopAnchorPoints();

			anchorPointsSide2 = node2.getBottomAnchorPoints();

			float ratioX = deltaX / viewFrustum.getWidth();

			float node1EdgeAnchorX = (float) position1.getX() - ratioX
					* node1.getWidth() / 2.0f;
			float node1EdgeAnchorY = (float) (anchorPointsSide1.getFirst()
					.getY() + Math
					.min(0.2f * spacingY,
							pixelGLConverter
									.getGLHeightForPixelHeight(MAX_NODE_EDGE_ANCHOR_DISTANCE_PIXELS)));
			Point2D edgeAnchorPoint1 = new Point2D.Float(node1EdgeAnchorX,
					node1EdgeAnchorY);

			float node2EdgeAnchorX = (float) position2.getX() + ratioX
					* node2.getWidth() / 2.0f;
			float node2EdgeAnchorY = (float) (anchorPointsSide2.getFirst()
					.getY() - Math
					.min(0.2f * spacingY,
							pixelGLConverter
									.getGLHeightForPixelHeight(MAX_NODE_EDGE_ANCHOR_DISTANCE_PIXELS)));
			Point2D edgeAnchorPoint2 = new Point2D.Float(node2EdgeAnchorX,
					node2EdgeAnchorY);

			edgePoints.add(edgeAnchorPoint1);
			edgePoints.add(edgeAnchorPoint2);

			edgeRoutingStrategy.createEdge(edgePoints);

			Point2D edgeRoutingHelperPoint1 = new Point2D.Float(
					(float) edgeAnchorPoint1.getX(), (float) anchorPointsSide1
							.getFirst().getY());
			Point2D edgeRoutingHelperPoint2 = new Point2D.Float(
					(float) edgeAnchorPoint2.getX(), (float) anchorPointsSide2
							.getFirst().getY());

			// edgePoints.add(bandAnchorPoint2);
			// edgePoints.add(0, bandAnchorPoint1);
			// edgePoints.add(bandAnchorPoint2);
			// edgePoints.add(0, bandAnchorPoint1);
			edgePoints.add(edgeRoutingHelperPoint2);
			edgePoints.add(0, edgeRoutingHelperPoint1);
			// edgePoints.add(bandRoutingHelperPoint2);
			// edgePoints.add(0, bandRoutingHelperPoint1);

			float nodeEdgeAnchorSpacing1 = (float) edgeAnchorPoint1.getY()
					- (float) anchorPointsSide1.getFirst().getY();

			offsetAnchorPointsSide1 = new Pair<Point2D, Point2D>();
			offsetAnchorPointsSide1.setFirst(new Point2D.Float(
					(float) anchorPointsSide1.getFirst().getX(),
					(float) anchorPointsSide1.getFirst().getY() + 0.3f
							* nodeEdgeAnchorSpacing1));
			offsetAnchorPointsSide1.setSecond(new Point2D.Float(
					(float) anchorPointsSide1.getSecond().getX(),
					(float) anchorPointsSide1.getSecond().getY() + 0.3f
							* nodeEdgeAnchorSpacing1));

			// Pair<Point2D, Point2D> offsetAnchorPointsSide1 = new
			// Pair<Point2D, Point2D>();
			// offsetAnchorPointsSide1.setFirst(new Point2D.Float(
			// (float) anchorPointsSide1.getFirst().getX(),
			// (float) anchorPointsSide1.getFirst().getY() + 0.3f
			// * spacingY));
			// offsetAnchorPointsSide1.setSecond(new Point2D.Float(
			// (float) anchorPointsSide1.getSecond().getX(),
			// (float) anchorPointsSide1.getSecond().getY() + 0.3f
			// * spacingY));
			//
			// Pair<Point2D, Point2D> offsetAnchorPointsSide2 = new
			// Pair<Point2D, Point2D>();
			// offsetAnchorPointsSide2.setFirst(new Point2D.Float(
			// (float) anchorPointsSide2.getFirst().getX(),
			// (float) anchorPointsSide2.getFirst().getY() - 0.3f
			// * spacingY));
			// offsetAnchorPointsSide2.setSecond(new Point2D.Float(
			// (float) anchorPointsSide2.getSecond().getX(),
			// (float) anchorPointsSide2.getSecond().getY() - 0.3f
			// * spacingY));
			//
			// anchorPoints.add(anchorPointsSide1);
			// anchorPoints.add(offsetAnchorPointsSide1);
			// anchorPoints.add(offsetAnchorPointsSide2);
			// anchorPoints.add(anchorPointsSide2);
		}

		ConnectionBandRenderer connectionBandRenderer = new ConnectionBandRenderer();

		connectionBandRenderer.init(gl);

		List<Vec3f> bandPoints = new ArrayList<Vec3f>();

		for (int i = 0; i < edgePoints.size() - 3; i++) {
			List<Vec3f> bandPartPoints = connectionBandRenderer
					.calcInterpolatedBand(gl, edgePoints, 20, pixelGLConverter);
			connectionBandRenderer.render(gl, bandPartPoints);
			bandPoints.addAll(bandPoints.size() / 2, bandPartPoints);
		}
		// }

		Point2D bandAnchorPoint1Side1 = new Point2D.Float(
				bandPoints.get(0).x(), bandPoints.get(0).y());
		Point2D bandAnchorPoint2Side1 = new Point2D.Float(bandPoints.get(
				bandPoints.size() - 1).x(), bandPoints.get(
				bandPoints.size() - 1).y());

		Pair<Point2D, Point2D> bandAnchorPoints1 = new Pair<Point2D, Point2D>(
				bandAnchorPoint2Side1, bandAnchorPoint1Side1);

		List<Pair<Point2D, Point2D>> node1BandConnectionPoints = new ArrayList<Pair<Point2D, Point2D>>();
		node1BandConnectionPoints.add(anchorPointsSide1);

		node1BandConnectionPoints.add(offsetAnchorPointsSide1);
		node1BandConnectionPoints.add(bandAnchorPoints1);

		connectionBandRenderer.renderComplexBand(gl, node1BandConnectionPoints,
				false, new float[] { 0, 0, 0 }, 1);

	}
}
