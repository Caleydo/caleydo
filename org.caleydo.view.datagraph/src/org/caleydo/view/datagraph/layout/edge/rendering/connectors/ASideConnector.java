package org.caleydo.view.datagraph.layout.edge.rendering.connectors;

import gleem.linalg.Vec3f;

import java.awt.geom.Point2D;
import java.util.List;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.util.spline.ConnectionBandRenderer;
import org.caleydo.view.datagraph.node.IDataGraphNode;

public abstract class ASideConnector extends ANodeConnector {

	protected final static int MAX_NODE_EDGE_ANCHOR_DISTANCE_PIXELS = 20;

	protected ViewFrustum viewFrustum;
	protected IDataGraphNode otherNode;
	protected Point2D bandConnectionPoint;
	protected Pair<Point2D, Point2D> nodeAnchorPoints;

	float vecXPoint1 = 0;
	float vecYPoint1 = 0;
	float vecXPoint2 = 0;
	float vecYPoint2 = 0;

	public ASideConnector(IDataGraphNode node, PixelGLConverter pixelGLconverter,
			ConnectionBandRenderer connectionBandRenderer, ViewFrustum viewFrustum,
			IDataGraphNode otherNode) {
		super(node, pixelGLconverter, connectionBandRenderer);
		this.viewFrustum = viewFrustum;
		this.otherNode = otherNode;

	}

	@Override
	public Point2D getBandConnectionPoint() {
		return bandConnectionPoint;
	}

	protected void calcBandDependentParameters(boolean isEnd1, List<Vec3f> bandPoints) {
		calcBandAnchorPoints(isEnd1, bandPoints);

		if (isEnd1) {

			vecXPoint1 = (float) bandAnchorPoint1.getX() - bandPoints.get(1).x();
			vecYPoint1 = (float) bandAnchorPoint1.getY() - bandPoints.get(1).y();

			vecXPoint2 = (float) bandAnchorPoint2.getX()
					- bandPoints.get(bandPoints.size() - 2).x();
			vecYPoint2 = (float) bandAnchorPoint2.getY()
					- bandPoints.get(bandPoints.size() - 2).y();
		} else {

			vecXPoint1 = (float) bandAnchorPoint1.getX()
					- bandPoints.get(bandPoints.size() / 2 - 2).x();
			vecYPoint1 = (float) bandAnchorPoint1.getY()
					- bandPoints.get(bandPoints.size() / 2 - 2).y();

			vecXPoint2 = (float) bandAnchorPoint2.getX()
					- bandPoints.get(bandPoints.size() / 2 + 1).x();
			vecYPoint2 = (float) bandAnchorPoint2.getY()
					- bandPoints.get(bandPoints.size() / 2 + 1).y();
		}
	}

}
