/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.dvi.layout.edge.rendering.connectors;

import gleem.linalg.Vec3f;

import java.awt.geom.Point2D;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.util.spline.ConnectionBandRenderer;
import org.caleydo.view.dvi.node.IDVINode;

public abstract class ANodeConnector {

	protected IDVINode node;
	protected PixelGLConverter pixelGLConverter;
	protected ConnectionBandRenderer connectionBandRenderer;
	protected Point2D bandAnchorPoint1;
	protected Point2D bandAnchorPoint2;
	protected boolean highlightBand;
	protected ViewFrustum viewFrustum;
	protected IDVINode otherNode;
	protected Point2D bandConnectionPoint;

	public ANodeConnector(IDVINode node,
			PixelGLConverter pixelGLconverter,
			ConnectionBandRenderer connectionBandRenderer,
			IDVINode otherNode, ViewFrustum viewFrustum) {
		this.node = node;
		this.pixelGLConverter = pixelGLconverter;
		this.connectionBandRenderer = connectionBandRenderer;
		this.otherNode = otherNode;
		this.viewFrustum = viewFrustum;
	}

	public Point2D getBandConnectionPoint() {
		return bandConnectionPoint;
	}

	public abstract Point2D getBandHelperPoint();

	public abstract void render(GL2 gl, List<Vec3f> bandPoints, boolean isEnd1,
			Color color);

	protected Point2D calcPointOnLineWithFixedX(Point2D pointOnLine,
			float vecX, float vecY, float fixedX, float minY, float maxY,
			float exceedingMinLimitValY, float exceedingMaxLimitValY) {

		float lambda = 0;
		if (vecX != 0)
			lambda = vecY / vecX;

		float pointY = (float) pointOnLine.getY()
				- ((float) pointOnLine.getX() - fixedX) * lambda;

		if (pointY < minY) {
			pointY = exceedingMinLimitValY;
		}
		if (pointY > maxY) {
			pointY = exceedingMaxLimitValY;
		}

		return new Point2D.Float(fixedX, pointY);
	}

	protected Point2D calcPointOnLineWithFixedY(Point2D pointOnLine,
			float vecX, float vecY, float fixedY, float minX, float maxX,
			float exceedingMinLimitValX, float exceedingMaxLimitValX) {

		float lambda = 0;
		if (vecX != 0)
			lambda = vecY / vecX;

		float pointX = (float) pointOnLine.getX()
				- (lambda == 0 ? 0 : ((float) pointOnLine.getY() - fixedY)
						/ lambda);

		if (pointX < minX) {
			pointX = exceedingMinLimitValX;
		}
		if (pointX > maxX) {
			pointX = exceedingMaxLimitValX;
		}

		return new Point2D.Float(pointX, fixedY);
	}

	protected void calcBandAnchorPoints(boolean isEnd1, List<Vec3f> bandPoints) {
		if (isEnd1) {
			bandAnchorPoint1 = new Point2D.Float(bandPoints.get(0).x(),
					bandPoints.get(0).y());
			bandAnchorPoint2 = new Point2D.Float(bandPoints.get(
					bandPoints.size() - 1).x(), bandPoints.get(
					bandPoints.size() - 1).y());
		} else {

			bandAnchorPoint1 = new Point2D.Float(bandPoints.get(
					bandPoints.size() / 2 - 1).x(), bandPoints.get(
					bandPoints.size() / 2 - 1).y());
			bandAnchorPoint2 = new Point2D.Float(bandPoints.get(
					bandPoints.size() / 2).x(), bandPoints.get(
					bandPoints.size() / 2).y());
		}

	}

	public IDVINode getNode() {
		return node;
	}

	public boolean isHighlightBand() {
		return highlightBand;
	}

	public void setHighlightBand(boolean highlightBand) {
		this.highlightBand = highlightBand;
	}

}
