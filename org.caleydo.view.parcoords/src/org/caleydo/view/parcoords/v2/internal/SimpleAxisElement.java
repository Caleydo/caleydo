/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.parcoords.v2.internal;

import static org.caleydo.view.parcoords.PCRenderStyle.AXIS_MARKER_WIDTH;
import static org.caleydo.view.parcoords.PCRenderStyle.NUMBER_AXIS_MARKERS;
import static org.caleydo.view.parcoords.PCRenderStyle.Y_AXIS_COLOR;

import org.caleydo.core.view.opengl.layout2.GLGraphics;

/**
 * @author Samuel Gratzl
 *
 */
public class SimpleAxisElement extends AAxisElement {

	public SimpleAxisElement(int id, String label) {
		super(id, label);
	}

	@Override
	protected AAxisElement createClone() {
		return new SimpleAxisElement(id, label);
	}


	/**
	 * @param g
	 * @param w
	 * @param h
	 */
	@Override
	protected void renderMarkers(GLGraphics g, float w, float h) {
		// markers on axis
		g.color(Y_AXIS_COLOR);
		float delta = h / (NUMBER_AXIS_MARKERS + 1);
		for (int i = 1; i < NUMBER_AXIS_MARKERS; ++i) {
			float at = delta * i;
			g.drawLine(-AXIS_MARKER_WIDTH, at, +AXIS_MARKER_WIDTH, at);
		}
	}
}
