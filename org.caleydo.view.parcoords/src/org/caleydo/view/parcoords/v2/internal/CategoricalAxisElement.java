/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.parcoords.v2.internal;

import static org.caleydo.view.parcoords.PCRenderStyle.AXIS_MARKER_WIDTH;
import static org.caleydo.view.parcoords.PCRenderStyle.Y_AXIS_COLOR;

import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.view.opengl.layout2.GLGraphics;

/**
 * @author Samuel Gratzl
 *
 */
public class CategoricalAxisElement extends AAxisElement {
	private final CategoricalClassDescription<?> desc;

	public CategoricalAxisElement(int id, String label, CategoricalClassDescription<?> desc) {
		super(id, label);
		this.desc = desc;
	}

	@Override
	protected AAxisElement createClone() {
		return new CategoricalAxisElement(id, label, desc);
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
		int size = desc.sizeWithoutUnknown();
		float delta = h / size;
		for (int i = 1; i < size; ++i) {
			float at = delta * i;
			g.drawLine(-AXIS_MARKER_WIDTH, at, +AXIS_MARKER_WIDTH, at);
		}
	}
}
