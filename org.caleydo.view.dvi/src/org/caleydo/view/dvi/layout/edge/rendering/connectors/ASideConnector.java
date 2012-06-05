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
