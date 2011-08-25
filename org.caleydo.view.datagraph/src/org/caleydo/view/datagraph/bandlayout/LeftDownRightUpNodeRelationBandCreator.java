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

		ArrayList<Point2D> edgePoints = new ArrayList<Point2D>();
		Pair<Point2D, Point2D> anchorPointsSide1;
		Pair<Point2D, Point2D> anchorPointsSide2;
		Pair<Point2D, Point2D> offsetAnchorPointsSide1;
		Pair<Point2D, Point2D> bandOffsetAnchorPoints1;
		Pair<Point2D, Point2D> bandAnchorPoints1;
		Pair<Point2D, Point2D> offsetAnchorPointsSide2;
		Pair<Point2D, Point2D> bandOffsetAnchorPoints2;
		Pair<Point2D, Point2D> bandAnchorPoints2;

		ConnectionBandRenderer connectionBandRenderer = new ConnectionBandRenderer();

		connectionBandRenderer.init(gl);

		if (spacingX > spacingY) {

			anchorPointsSide1 = node1.getRightAnchorPoints();
			anchorPointsSide2 = node2.getLeftAnchorPoints();

			float ratioY = deltaY / viewFrustum.getHeight();

			float node1EdgeAnchorY = (float) position1.getY() - ratioY
					* node1.getHeight() / 2.0f;
			float node1EdgeAnchorX = (float) (anchorPointsSide1.getFirst()
					.getX() + Math
					.min(0.2f * spacingX,
							pixelGLConverter
									.getGLWidthForPixelWidth(MAX_NODE_EDGE_ANCHOR_DISTANCE_PIXELS)));
			Point2D edgeAnchorPoint1 = new Point2D.Float(node1EdgeAnchorX,
					node1EdgeAnchorY);

			float node2EdgeAnchorY = (float) position2.getY() + ratioY
					* node2.getHeight() / 2.0f;
			float node2EdgeAnchorX = (float) (anchorPointsSide2.getFirst()
					.getX() - Math
					.min(0.2f * spacingX,
							pixelGLConverter
									.getGLWidthForPixelWidth(MAX_NODE_EDGE_ANCHOR_DISTANCE_PIXELS)));
			Point2D edgeAnchorPoint2 = new Point2D.Float(node2EdgeAnchorX,
					node2EdgeAnchorY);

			edgePoints.add(edgeAnchorPoint1);
			edgePoints.add(edgeAnchorPoint2);

			edgeRoutingStrategy.createEdge(edgePoints);

			Point2D bandRoutingHelperPoint1 = new Point2D.Float(
					(float) anchorPointsSide1.getFirst().getX(),
					(float) edgeAnchorPoint1.getY());
			Point2D bandRoutingHelperPoint2 = new Point2D.Float(
					(float) anchorPointsSide2.getFirst().getX(),
					(float) edgeAnchorPoint2.getY());

			edgePoints.add(bandRoutingHelperPoint2);
			edgePoints.add(0, bandRoutingHelperPoint1);

			float nodeEdgeAnchorSpacing1 = (float) edgeAnchorPoint1.getX()
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

			float nodeEdgeAnchorSpacing2 = (float) Math.abs(edgeAnchorPoint2
					.getX() - (float) anchorPointsSide2.getFirst().getX());

			offsetAnchorPointsSide2 = new Pair<Point2D, Point2D>();
			offsetAnchorPointsSide2.setFirst(new Point2D.Float(
					(float) anchorPointsSide2.getFirst().getX() - 0.3f
							* nodeEdgeAnchorSpacing2, (float) anchorPointsSide2
							.getFirst().getY()));
			offsetAnchorPointsSide2.setSecond(new Point2D.Float(
					(float) anchorPointsSide2.getSecond().getX() - 0.3f
							* nodeEdgeAnchorSpacing2, (float) anchorPointsSide2
							.getSecond().getY()));

			List<Vec3f> bandPoints = new ArrayList<Vec3f>();

			for (int i = 0; i < edgePoints.size() - 3; i++) {
				List<Vec3f> bandPartPoints = connectionBandRenderer
						.calcInterpolatedBand(gl, edgePoints, 20,
								pixelGLConverter);
				connectionBandRenderer.render(gl, bandPartPoints);
				bandPoints.addAll(bandPoints.size() / 2, bandPartPoints);
			}
			// }

			Point2D bandAnchorPoint1Side1 = new Point2D.Float(bandPoints.get(0)
					.x(), bandPoints.get(0).y());
			Point2D bandAnchorPoint2Side1 = new Point2D.Float(bandPoints.get(
					bandPoints.size() - 1).x(), bandPoints.get(
					bandPoints.size() - 1).y());

			bandAnchorPoints1 = new Pair<Point2D, Point2D>(
					bandAnchorPoint2Side1, bandAnchorPoint1Side1);

			float vecXPoint1Side1 = (float) bandAnchorPoint1Side1.getX()
					- bandPoints.get(1).x();
			float vecYPoint1Side1 = (float) bandAnchorPoint1Side1.getY()
					- bandPoints.get(1).y();

			float vecXPoint2Side1 = (float) bandAnchorPoint2Side1.getX()
					- bandPoints.get(bandPoints.size() - 2).x();
			float vecYPoint2Side1 = (float) bandAnchorPoint2Side1.getY()
					- bandPoints.get(bandPoints.size() - 2).y();

			float lambda1 = 0;
			if (vecXPoint1Side1 != 0)
				lambda1 = vecYPoint1Side1 / vecXPoint1Side1;
			float lambda2 = 0;
			if (vecXPoint2Side1 != 0)
				lambda2 = vecYPoint2Side1 / vecXPoint2Side1;

			float bandOffsetAnchorPoint1Side1Y = (float) bandAnchorPoint1Side1
					.getY()
					- ((float) bandAnchorPoint1Side1.getX() - (float) offsetAnchorPointsSide1
							.getFirst().getX()) * lambda1;
			float bandOffsetAnchorPoint2Side1Y = (float) bandAnchorPoint2Side1
					.getY()
					- ((float) bandAnchorPoint2Side1.getX() - (float) offsetAnchorPointsSide1
							.getSecond().getX()) * lambda2;

			bandOffsetAnchorPoints1 = new Pair<Point2D, Point2D>();
			bandOffsetAnchorPoints1.setSecond(new Point2D.Float(
					(float) offsetAnchorPointsSide1.getFirst().getX(),
					bandOffsetAnchorPoint1Side1Y));

			bandOffsetAnchorPoints1.setFirst(new Point2D.Float(
					(float) offsetAnchorPointsSide1.getSecond().getX(),
					bandOffsetAnchorPoint2Side1Y));

			Point2D bandAnchorPoint1Side2 = new Point2D.Float(bandPoints.get(
					bandPoints.size() / 2 - 1).x(), bandPoints.get(
					bandPoints.size() / 2 - 1).y());
			Point2D bandAnchorPoint2Side2 = new Point2D.Float(bandPoints.get(
					bandPoints.size() / 2).x(), bandPoints.get(
					bandPoints.size() / 2).y());

			bandAnchorPoints2 = new Pair<Point2D, Point2D>(
					bandAnchorPoint2Side2, bandAnchorPoint1Side2);

			float vecXPoint1Side2 = (float) bandAnchorPoint1Side2.getX()
					- bandPoints.get(bandPoints.size() / 2 - 2).x();
			float vecYPoint1Side2 = (float) bandAnchorPoint1Side2.getY()
					- bandPoints.get(bandPoints.size() / 2 - 2).y();

			float vecXPoint2Side2 = (float) bandAnchorPoint2Side2.getX()
					- bandPoints.get(bandPoints.size() / 2 + 1).x();
			float vecYPoint2Side2 = (float) bandAnchorPoint2Side2.getY()
					- bandPoints.get(bandPoints.size() / 2 + 1).y();

			lambda1 = 0;
			if (vecXPoint1Side2 != 0)
				lambda1 = vecYPoint1Side2 / vecXPoint1Side2;
			lambda2 = 0;
			if (vecXPoint2Side2 != 0)
				lambda2 = vecYPoint2Side2 / vecXPoint2Side2;

			float bandOffsetAnchorPoint1Side2Y = (float) bandAnchorPoint1Side2
					.getY()
					- ((float) bandAnchorPoint1Side2.getX() - (float) offsetAnchorPointsSide2
							.getFirst().getX()) * lambda1;
			float bandOffsetAnchorPoint2Side2Y = (float) bandAnchorPoint2Side2
					.getY()
					- ((float) bandAnchorPoint2Side2.getX() - (float) offsetAnchorPointsSide2
							.getSecond().getX()) * lambda2;

			bandOffsetAnchorPoints2 = new Pair<Point2D, Point2D>();
			bandOffsetAnchorPoints2.setSecond(new Point2D.Float(
					(float) offsetAnchorPointsSide2.getFirst().getX(),
					bandOffsetAnchorPoint1Side2Y));

			bandOffsetAnchorPoints2.setFirst(new Point2D.Float(
					(float) offsetAnchorPointsSide2.getSecond().getX(),
					bandOffsetAnchorPoint2Side2Y));

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

			edgePoints.add(edgeRoutingHelperPoint2);
			edgePoints.add(0, edgeRoutingHelperPoint1);

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

			float nodeEdgeAnchorSpacing2 = (float) Math.abs(edgeAnchorPoint2
					.getY() - (float) anchorPointsSide2.getFirst().getY());

			offsetAnchorPointsSide2 = new Pair<Point2D, Point2D>();
			offsetAnchorPointsSide2.setFirst(new Point2D.Float(
					(float) anchorPointsSide2.getFirst().getX(),
					(float) anchorPointsSide2.getFirst().getY() - 0.3f
							* nodeEdgeAnchorSpacing2));
			offsetAnchorPointsSide2.setSecond(new Point2D.Float(
					(float) anchorPointsSide2.getSecond().getX(),
					(float) anchorPointsSide2.getSecond().getY() - 0.3f
							* nodeEdgeAnchorSpacing2));

			List<Vec3f> bandPoints = new ArrayList<Vec3f>();

			for (int i = 0; i < edgePoints.size() - 3; i++) {
				List<Vec3f> bandPartPoints = connectionBandRenderer
						.calcInterpolatedBand(gl, edgePoints, 20,
								pixelGLConverter);
				connectionBandRenderer.render(gl, bandPartPoints);
				bandPoints.addAll(bandPoints.size() / 2, bandPartPoints);
			}
			// }

			Point2D bandAnchorPoint1Side1 = new Point2D.Float(bandPoints.get(0)
					.x(), bandPoints.get(0).y());
			Point2D bandAnchorPoint2Side1 = new Point2D.Float(bandPoints.get(
					bandPoints.size() - 1).x(), bandPoints.get(
					bandPoints.size() - 1).y());

			bandAnchorPoints1 = new Pair<Point2D, Point2D>(
					bandAnchorPoint2Side1, bandAnchorPoint1Side1);

			float vecXPoint1Side1 = (float) bandAnchorPoint1Side1.getX()
					- bandPoints.get(1).x();
			float vecYPoint1Side1 = (float) bandAnchorPoint1Side1.getY()
					- bandPoints.get(1).y();

			float vecXPoint2Side1 = (float) bandAnchorPoint2Side1.getX()
					- bandPoints.get(bandPoints.size() - 2).x();
			float vecYPoint2Side1 = (float) bandAnchorPoint2Side1.getY()
					- bandPoints.get(bandPoints.size() - 2).y();

			float lambda1 = 0;
			if (vecXPoint1Side1 != 0)
				lambda1 = vecYPoint1Side1 / vecXPoint1Side1;
			float lambda2 = 0;
			if (vecXPoint2Side1 != 0)
				lambda2 = vecYPoint2Side1 / vecXPoint2Side1;

			float bandOffsetAnchorPoint1Side1X = (float) bandAnchorPoint1Side1
					.getX()
					- (lambda1 == 0 ? 0
							: ((float) bandAnchorPoint1Side1.getY() - (float) offsetAnchorPointsSide1
									.getFirst().getY()) / lambda1);
			float bandOffsetAnchorPoint2Side1X = (float) bandAnchorPoint2Side1
					.getX()
					- (lambda2 == 0 ? 0
							: ((float) bandAnchorPoint2Side1.getY() - (float) offsetAnchorPointsSide1
									.getSecond().getY()) / lambda2);

			bandOffsetAnchorPoints1 = new Pair<Point2D, Point2D>();
			bandOffsetAnchorPoints1.setSecond(new Point2D.Float(
					bandOffsetAnchorPoint1Side1X,
					(float) offsetAnchorPointsSide1.getFirst().getY()));

			bandOffsetAnchorPoints1.setFirst(new Point2D.Float(
					bandOffsetAnchorPoint2Side1X,
					(float) offsetAnchorPointsSide1.getSecond().getY()));

			Point2D bandAnchorPoint1Side2 = new Point2D.Float(bandPoints.get(
					bandPoints.size() / 2 - 1).x(), bandPoints.get(
					bandPoints.size() / 2 - 1).y());
			Point2D bandAnchorPoint2Side2 = new Point2D.Float(bandPoints.get(
					bandPoints.size() / 2).x(), bandPoints.get(
					bandPoints.size() / 2).y());

			bandAnchorPoints2 = new Pair<Point2D, Point2D>(
					bandAnchorPoint2Side2, bandAnchorPoint1Side2);

			float vecXPoint1Side2 = (float) bandAnchorPoint1Side2.getX()
					- bandPoints.get(bandPoints.size() / 2 - 2).x();
			float vecYPoint1Side2 = (float) bandAnchorPoint1Side2.getY()
					- bandPoints.get(bandPoints.size() / 2 - 2).y();

			float vecXPoint2Side2 = (float) bandAnchorPoint2Side2.getX()
					- bandPoints.get(bandPoints.size() / 2 + 1).x();
			float vecYPoint2Side2 = (float) bandAnchorPoint2Side2.getY()
					- bandPoints.get(bandPoints.size() / 2 + 1).y();

			lambda1 = 0;
			if (vecXPoint1Side2 != 0)
				lambda1 = vecYPoint1Side2 / vecXPoint1Side2;
			lambda2 = 0;
			if (vecXPoint2Side2 != 0)
				lambda2 = vecYPoint2Side2 / vecXPoint2Side2;

			float bandOffsetAnchorPoint1Side2X = (float) bandAnchorPoint1Side2
					.getX()
					- (lambda1 == 0 ? 0
							: ((float) bandAnchorPoint1Side2.getY() - (float) offsetAnchorPointsSide2
									.getFirst().getY()) / lambda1);
			float bandOffsetAnchorPoint2Side2X = (float) bandAnchorPoint2Side2
					.getX()
					- (lambda2 == 0 ? 0
							: ((float) bandAnchorPoint2Side2.getY() - (float) offsetAnchorPointsSide2
									.getSecond().getY()) / lambda2);

			bandOffsetAnchorPoints2 = new Pair<Point2D, Point2D>();
			bandOffsetAnchorPoints2.setSecond(new Point2D.Float(
					bandOffsetAnchorPoint1Side2X,
					(float) offsetAnchorPointsSide2.getFirst().getY()));

			bandOffsetAnchorPoints2.setFirst(new Point2D.Float(
					bandOffsetAnchorPoint2Side2X,
					(float) offsetAnchorPointsSide2.getSecond().getY()));

		}

		// GLHelperFunctions.drawPointAt(gl, bandOffsetAnchorPoint1Side1X,
		// (float)offsetAnchorPointsSide1
		// .getFirst().getY(), 0);
		//
		// GLHelperFunctions.drawPointAt(gl, bandOffsetAnchorPoint2Side1X,
		// (float)offsetAnchorPointsSide1
		// .getFirst().getY(), 0);

		List<Pair<Point2D, Point2D>> node1BandConnectionPoints = new ArrayList<Pair<Point2D, Point2D>>();
		node1BandConnectionPoints.add(anchorPointsSide1);
		node1BandConnectionPoints.add(offsetAnchorPointsSide1);
		node1BandConnectionPoints.add(bandOffsetAnchorPoints1);
		node1BandConnectionPoints.add(bandAnchorPoints1);

		List<Pair<Point2D, Point2D>> node2BandConnectionPoints = new ArrayList<Pair<Point2D, Point2D>>();
		node2BandConnectionPoints.add(anchorPointsSide2);
		node2BandConnectionPoints.add(offsetAnchorPointsSide2);
		node2BandConnectionPoints.add(bandOffsetAnchorPoints2);
		node2BandConnectionPoints.add(bandAnchorPoints2);

		connectionBandRenderer.renderComplexBand(gl, node1BandConnectionPoints,
				false, new float[] { 0, 0, 0 }, 1);

		connectionBandRenderer.renderComplexBand(gl, node2BandConnectionPoints,
				false, new float[] { 0, 0, 0 }, 1);

	}
}
