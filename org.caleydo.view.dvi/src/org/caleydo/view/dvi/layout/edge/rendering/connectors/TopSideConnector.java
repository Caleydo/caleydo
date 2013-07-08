/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.dvi.layout.edge.rendering.connectors;

import gleem.linalg.Vec3f;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.util.spline.ConnectionBandRenderer;
import org.caleydo.view.dvi.node.IDVINode;

public class TopSideConnector extends ASideConnector {

	public TopSideConnector(IDVINode node, PixelGLConverter pixelGLconverter,
			ConnectionBandRenderer connectionBandRenderer, ViewFrustum viewFrustum,
			IDVINode otherNode) {
		super(node, pixelGLconverter, connectionBandRenderer, viewFrustum, otherNode);
		calcBandConnectionPoint();
	}

	protected void calcBandConnectionPoint() {

		Point2D nodePosition = node.getPosition();
		Point2D otherNodePosition = otherNode.getPosition();

		float spacingY = (float) ((otherNodePosition.getY() - otherNode.getHeight() / 2.0f) - (nodePosition
				.getY() + node.getHeight() / 2.0f));
		float deltaX = (float) (nodePosition.getX() - otherNodePosition.getX());

		nodeAnchorPoints = node.getTopAnchorPoints();

		float ratioX = deltaX / viewFrustum.getWidth();

		float edgeAnchorX = (float) nodePosition.getX() - ratioX * node.getWidth() / 2.0f;
		float edgeAnchorY = (float) (nodeAnchorPoints.getFirst().getY() + Math.min(
				0.2f * spacingY, pixelGLConverter
						.getGLHeightForPixelHeight(MAX_NODE_EDGE_ANCHOR_DISTANCE_PIXELS)));
		bandConnectionPoint = new Point2D.Float(edgeAnchorX, edgeAnchorY);

	}

	@Override
	public Point2D getBandHelperPoint() {
		return new Point2D.Float((float) bandConnectionPoint.getX(),
				(float) nodeAnchorPoints.getFirst().getY());
	}

	@Override
	public void render(GL2 gl, List<Vec3f> bandPoints, boolean isEnd1, Color color) {

		float nodeEdgeAnchorSpacing = (float) bandConnectionPoint.getY()
				- (float) nodeAnchorPoints.getFirst().getY();

		Pair<Point2D, Point2D> nodeOffsetAnchorPoints = new Pair<Point2D, Point2D>();
		nodeOffsetAnchorPoints.setFirst(new Point2D.Float((float) nodeAnchorPoints
				.getFirst().getX(), (float) nodeAnchorPoints.getFirst().getY() + 0.3f
				* nodeEdgeAnchorSpacing));
		nodeOffsetAnchorPoints.setSecond(new Point2D.Float((float) nodeAnchorPoints
				.getSecond().getX(), (float) nodeAnchorPoints.getSecond().getY() + 0.3f
				* nodeEdgeAnchorSpacing));

		calcBandDependentParameters(isEnd1, bandPoints);

		Pair<Point2D, Point2D> bandAnchorPoints = new Pair<Point2D, Point2D>(
				bandAnchorPoint2, bandAnchorPoint1);

		Point2D bandOffsetAnchorPoint1 = calcPointOnLineWithFixedY(bandAnchorPoint1,
				vecXPoint1, vecYPoint1, (float) nodeOffsetAnchorPoints.getFirst().getY(),
				(float) nodeOffsetAnchorPoints.getFirst().getX(),
				(float) nodeOffsetAnchorPoints.getSecond().getX(),
				(float) nodeOffsetAnchorPoints.getSecond().getX(),
				(float) nodeOffsetAnchorPoints.getSecond().getX());

		Point2D bandOffsetAnchorPoint2 = calcPointOnLineWithFixedY(bandAnchorPoint2,
				vecXPoint2, vecYPoint2,
				(float) nodeOffsetAnchorPoints.getSecond().getY(),
				(float) nodeOffsetAnchorPoints.getFirst().getX(),
				(float) nodeOffsetAnchorPoints.getSecond().getX(),
				(float) nodeOffsetAnchorPoints.getFirst().getX(),
				(float) nodeOffsetAnchorPoints.getFirst().getX());

		Pair<Point2D, Point2D> bandOffsetAnchorPoints = new Pair<Point2D, Point2D>(
				bandOffsetAnchorPoint2, bandOffsetAnchorPoint1);

		List<Pair<Point2D, Point2D>> bandConnectionPoints = new ArrayList<Pair<Point2D, Point2D>>();
		bandConnectionPoints.add(nodeAnchorPoints);
		bandConnectionPoints.add(nodeOffsetAnchorPoints);
		bandConnectionPoints.add(bandOffsetAnchorPoints);
		bandConnectionPoints.add(bandAnchorPoints);

		connectionBandRenderer.renderComplexBand(gl, bandConnectionPoints, false,
				color.getRGB(), (highlightBand) ? 1 : 0.5f);

	}

}
