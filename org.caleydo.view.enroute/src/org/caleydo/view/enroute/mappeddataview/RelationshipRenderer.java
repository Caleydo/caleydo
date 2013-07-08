/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
