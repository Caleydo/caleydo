/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.dvi.layout.edge.rendering.connectors;

import gleem.linalg.Vec3f;

import java.awt.geom.Point2D;
import java.util.List;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.util.spline.ConnectionBandRenderer;
import org.caleydo.view.dvi.node.IDVINode;

public abstract class ASideConnector extends ANodeConnector {

	protected final static int MAX_NODE_EDGE_ANCHOR_DISTANCE_PIXELS = 20;

	protected Pair<Point2D, Point2D> nodeAnchorPoints;

	float vecXPoint1 = 0;
	float vecYPoint1 = 0;
	float vecXPoint2 = 0;
	float vecYPoint2 = 0;

	public ASideConnector(IDVINode node,
			PixelGLConverter pixelGLconverter,
			ConnectionBandRenderer connectionBandRenderer,
			ViewFrustum viewFrustum, IDVINode otherNode) {
		super(node, pixelGLconverter, connectionBandRenderer, otherNode,
				viewFrustum);
		this.viewFrustum = viewFrustum;
		this.otherNode = otherNode;

	}

	

	protected void calcBandDependentParameters(boolean isEnd1,
			List<Vec3f> bandPoints) {
		calcBandAnchorPoints(isEnd1, bandPoints);

		if (isEnd1) {

			vecXPoint1 = (float) bandAnchorPoint1.getX()
					- bandPoints.get(1).x();
			vecYPoint1 = (float) bandAnchorPoint1.getY()
					- bandPoints.get(1).y();

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
