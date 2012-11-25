/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
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

public class RightSideConnector extends ASideConnector {

	public RightSideConnector(IDVINode node, PixelGLConverter pixelGLconverter,
			ConnectionBandRenderer connectionBandRenderer, ViewFrustum viewFrustum,
			IDVINode otherNode) {
		super(node, pixelGLconverter, connectionBandRenderer, viewFrustum, otherNode);

		calcBandConnectionPoint();
	}

	protected void calcBandConnectionPoint() {
		Point2D nodePosition = node.getPosition();
		Point2D otherNodePosition = otherNode.getPosition();
		float spacingX = (float) ((otherNodePosition.getX() - otherNode.getWidth() / 2.0f) - (nodePosition
				.getX() + node.getWidth() / 2.0f));
		float deltaY = (float) (nodePosition.getY() - otherNodePosition.getY());

		nodeAnchorPoints = node.getRightAnchorPoints();

		float ratioY = deltaY / viewFrustum.getHeight();

		float edgeAnchorY = (float) nodePosition.getY() - ratioY * node.getHeight()
				/ 2.0f;
		float edgeAnchorX = (float) (nodeAnchorPoints.getFirst().getX() + Math.min(
				0.2f * spacingX, pixelGLConverter
						.getGLWidthForPixelWidth(MAX_NODE_EDGE_ANCHOR_DISTANCE_PIXELS)));
		bandConnectionPoint = new Point2D.Float(edgeAnchorX, edgeAnchorY);
	}

	@Override
	public Point2D getBandHelperPoint() {
		return new Point2D.Float((float) nodeAnchorPoints.getFirst().getX(),
				(float) bandConnectionPoint.getY());
	}

	@Override
	public void render(GL2 gl, List<Vec3f> bandPoints, boolean isEnd1, Color color) {

		float nodeEdgeAnchorSpacing = (float) bandConnectionPoint.getX()
				- (float) nodeAnchorPoints.getFirst().getX();

		Pair<Point2D, Point2D> nodeOffsetAnchorPoints = new Pair<Point2D, Point2D>();
		nodeOffsetAnchorPoints.setFirst(new Point2D.Float((float) nodeAnchorPoints
				.getFirst().getX() + 0.3f * nodeEdgeAnchorSpacing,
				(float) nodeAnchorPoints.getFirst().getY()));
		nodeOffsetAnchorPoints.setSecond(new Point2D.Float((float) nodeAnchorPoints
				.getSecond().getX() + 0.3f * nodeEdgeAnchorSpacing,
				(float) nodeAnchorPoints.getSecond().getY()));

		calcBandDependentParameters(isEnd1, bandPoints);

		Pair<Point2D, Point2D> bandAnchorPoints = new Pair<Point2D, Point2D>(
				bandAnchorPoint2, bandAnchorPoint1);

		Point2D bandOffsetAnchorPoint1 = calcPointOnLineWithFixedX(bandAnchorPoint1,
				vecXPoint1, vecYPoint1,
				(float) nodeOffsetAnchorPoints.getSecond().getX(),
				(float) nodeOffsetAnchorPoints.getSecond().getY(),
				(float) nodeOffsetAnchorPoints.getFirst().getY(),
				(float) nodeOffsetAnchorPoints.getSecond().getY(),
				(float) nodeOffsetAnchorPoints.getSecond().getY());

		Point2D bandOffsetAnchorPoint2 = calcPointOnLineWithFixedX(bandAnchorPoint2,
				vecXPoint2, vecYPoint2, (float) nodeOffsetAnchorPoints.getFirst().getX(),
				(float) nodeOffsetAnchorPoints.getSecond().getY(),
				(float) nodeOffsetAnchorPoints.getFirst().getY(),
				(float) nodeOffsetAnchorPoints.getFirst().getY(),
				(float) nodeOffsetAnchorPoints.getFirst().getY());

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
