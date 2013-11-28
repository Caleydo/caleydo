/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.mappeddataview;

import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.data.selection.SelectionType;

/**
 * @author Christian
 *
 */
public class SummaryRendererDataCenterDecorator extends ADataRenderer {

	protected ADataRenderer decoratee;
	protected float min;
	protected float max;
	protected float dataCenter;
	protected float normalizedDataCenter;

	/**
	 * @param contentRenderer
	 */
	public SummaryRendererDataCenterDecorator(ADataRenderer decoratee, float min, float max, float dataCenter) {
		super(decoratee.contentRenderer);
		this.decoratee = decoratee;
		this.min = min;
		this.max = max;
		this.dataCenter = dataCenter;
		normalizedDataCenter = getNormalizedValue(dataCenter);
	}

	protected float getNormalizedValue(float rawValue) {
		float value = (rawValue - min) / (max - min);
		if (value > 1)
			return 1;
		if (value < 0)
			return 0;
		return value;
	}

	@Override
	public void render(GL2 gl, float x, float y, List<SelectionType> selectionTypes) {
		if (!decoratee.contentRenderer.isHighlightMode) {
			gl.glColor3f(0, 0, 0);
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(normalizedDataCenter * x, 0, z);
			gl.glVertex3f(normalizedDataCenter * x, y, z);
			gl.glEnd();
		}
		decoratee.render(gl, x, y, selectionTypes);
	}

}
