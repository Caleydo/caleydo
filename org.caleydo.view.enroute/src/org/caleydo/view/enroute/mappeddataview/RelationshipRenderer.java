/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.enroute.mappeddataview;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.util.spline.ConnectionBandRenderer;
import org.caleydo.view.enroute.GLEnRoutePathway;

/**
 * Renders connection bands between a block of rows and a node.
 *
 * @author Alexander Lex
 *
 */
public class RelationshipRenderer {

	GLEnRoutePathway view;

	float[] topLeft = new float[3];
	float[] bottomLeft = new float[3];
	float[] topRight = new float[3];
	float[] bottomRight = new float[3];
	float[] color;

	ElementLayout topRightLayout;
	ElementLayout bottomRightLayout;

	private ConnectionBandRenderer connectionBandRenderer = null;

	/**
	 * Constructor
	 *
	 * @param color
	 *            the color of the connection band
	 */
	public RelationshipRenderer(float[] color, GLEnRoutePathway view) {
		this.color = color;
		this.view = view;
	}

	/**
	 * Initializes stuff that needs a gl context
	 *
	 * @param gl
	 */
	private void init(GL2 gl) {
		connectionBandRenderer = new ConnectionBandRenderer();
		connectionBandRenderer.init(gl);
	}

	/** Renders a connection band based on the provided coordinates */
	public void render(GL2 gl) {
		if (connectionBandRenderer == null) {
			init(gl);
		}
		float lineWidthCompensation = view.getPixelGLConverter().getGLHeightForPixelHeight(1);

		topRight[0] = topRightLayout.getTranslateX();
		topRight[1] = topRightLayout.getTranslateY() + topRightLayout.getSizeScaledY() - lineWidthCompensation;

		bottomRight[0] = bottomRightLayout.getTranslateX();
		bottomRight[1] = bottomRightLayout.getTranslateY() + lineWidthCompensation;

		float xOffset = (topRight[0] - topLeft[0]) / 2.0f;

		connectionBandRenderer.renderSingleBand(gl, topLeft, bottomLeft, topRight, bottomRight, false, xOffset, 0,
				color);
	}
}
