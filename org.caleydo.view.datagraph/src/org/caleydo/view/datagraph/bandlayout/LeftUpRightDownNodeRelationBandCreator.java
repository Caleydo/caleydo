package org.caleydo.view.datagraph.bandlayout;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.data.virtualarray.ADimensionGroupData;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.view.datagraph.IDataGraphNode;

public class LeftUpRightDownNodeRelationBandCreator extends AEdgeBandRenderer {

	private final static int SPACING_PIXELS = 2;

	public LeftUpRightDownNodeRelationBandCreator(IDataGraphNode node1,
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
		float spacingY = (float) ((position1.getY() - node1.getHeight() / 2.0f) - (position2
				.getY() + node2.getHeight() / 2.0f));

		List<Pair<Point2D, Point2D>> anchorPoints = new ArrayList<Pair<Point2D, Point2D>>();
		if (spacingX > spacingY) {
			Pair<Point2D, Point2D> anchorPointsSide1 = node1
					.getRightAnchorPoints();
			Pair<Point2D, Point2D> offsetAnchorPointsSide1 = new Pair<Point2D, Point2D>();
			offsetAnchorPointsSide1.setFirst(new Point2D.Float(
					(float) anchorPointsSide1.getFirst().getX() + 0.3f
							* spacingX, (float) anchorPointsSide1.getFirst()
							.getY()));
			offsetAnchorPointsSide1.setSecond(new Point2D.Float(
					(float) anchorPointsSide1.getSecond().getX() + 0.3f
							* spacingX, (float) anchorPointsSide1.getSecond()
							.getY()));

			Pair<Point2D, Point2D> anchorPointsSide2 = node2
					.getLeftAnchorPoints();
			Pair<Point2D, Point2D> offsetAnchorPointsSide2 = new Pair<Point2D, Point2D>();
			offsetAnchorPointsSide2.setFirst(new Point2D.Float(
					(float) anchorPointsSide2.getFirst().getX() - 0.3f
							* spacingX, (float) anchorPointsSide2.getFirst()
							.getY()));
			offsetAnchorPointsSide2.setSecond(new Point2D.Float(
					(float) anchorPointsSide2.getSecond().getX() - 0.3f
							* spacingX, (float) anchorPointsSide2.getSecond()
							.getY()));

			anchorPoints.add(anchorPointsSide1);
			anchorPoints.add(offsetAnchorPointsSide1);
			anchorPoints.add(offsetAnchorPointsSide2);
			anchorPoints.add(anchorPointsSide2);

		} else {
			Pair<Point2D, Point2D> anchorPointsSide1 = node1
					.getBottomAnchorPoints();
			Pair<Point2D, Point2D> offsetAnchorPointsSide1 = new Pair<Point2D, Point2D>();
			offsetAnchorPointsSide1.setFirst(new Point2D.Float(
					(float) anchorPointsSide1.getFirst().getX(),
					(float) anchorPointsSide1.getFirst().getY() - 0.3f
							* spacingY));
			offsetAnchorPointsSide1.setSecond(new Point2D.Float(
					(float) anchorPointsSide1.getSecond().getX(),
					(float) anchorPointsSide1.getSecond().getY() - 0.3f
							* spacingY));

			Pair<Point2D, Point2D> anchorPointsSide2 = node2
					.getTopAnchorPoints();
			Pair<Point2D, Point2D> offsetAnchorPointsSide2 = new Pair<Point2D, Point2D>();
			offsetAnchorPointsSide2.setFirst(new Point2D.Float(
					(float) anchorPointsSide2.getFirst().getX(),
					(float) anchorPointsSide2.getFirst().getY() + 0.3f
							* spacingY));
			offsetAnchorPointsSide2.setSecond(new Point2D.Float(
					(float) anchorPointsSide2.getSecond().getX(),
					(float) anchorPointsSide2.getSecond().getY() + 0.3f
							* spacingY));

			anchorPoints.add(anchorPointsSide1);
			anchorPoints.add(offsetAnchorPointsSide1);
			anchorPoints.add(offsetAnchorPointsSide2);
			anchorPoints.add(anchorPointsSide2);
		}
		bands.add(anchorPoints);

	}

	private List<Pair<Point2D, Point2D>> getBend(
			Pair<Point2D, Point2D> horizontalAnchors,
			Pair<Point2D, Point2D> verticalAnchors) {
		List<Pair<Point2D, Point2D>> anchorPoints = new ArrayList<Pair<Point2D, Point2D>>();

		Point2D controlPoint1 = new Point2D.Float((float) horizontalAnchors
				.getFirst().getX(), (float) verticalAnchors.getFirst().getY());
		Point2D controlPoint2 = new Point2D.Float((float) horizontalAnchors
				.getSecond().getX(), (float) verticalAnchors.getSecond().getY());

		Pair<Point2D, Point2D> controlPoints = new Pair<Point2D, Point2D>(
				controlPoint1, controlPoint2);

		anchorPoints.add(horizontalAnchors);
		anchorPoints.add(controlPoints);
		anchorPoints.add(verticalAnchors);

		return anchorPoints;
	}
}
