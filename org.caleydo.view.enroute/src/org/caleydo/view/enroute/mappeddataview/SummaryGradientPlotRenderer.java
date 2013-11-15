/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.mappeddataview;

import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2GL3;

import org.caleydo.core.data.selection.SelectionType;

/**
 * @author Christian
 *
 */
public class SummaryGradientPlotRenderer extends AMedianBasedSummaryRenderer {

	/**
	 * @param contentRenderer
	 */
	public SummaryGradientPlotRenderer(ContentRenderer contentRenderer) {
		super(contentRenderer);
	}

	@Override
	public void render(GL2 gl, float x, float y, List<SelectionType> selectionTypes) {
		if (contentRenderer.resolvedRowID == null || contentRenderer.isHighlightMode)
			return;

		float[] color = MappedDataRenderer.SUMMARY_BAR_COLOR.getRGBA();
		// float[] color = MappedDataRenderer.SUMMARY_BAR_COLOR.getRGBA();

		gl.glPushName(contentRenderer.parentView.getPickingManager().getPickingID(contentRenderer.parentView.getID(),
				getPickingType(), getPickingID()));

		float firstQuantrileBoundary = (float) (normalizedStats.getQuartile25()) * x;
		float thirdQuantrileBoundary = (float) (normalizedStats.getQuartile75()) * x;

		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

		gl.glBegin(GL2GL3.GL_QUADS);
		gl.glColor4fv(color, 0);
		gl.glVertex3f(firstQuantrileBoundary, y / 3, z);
		gl.glVertex3d(thirdQuantrileBoundary, y / 3, z);
		gl.glVertex3d(thirdQuantrileBoundary, y / 3 * 2, z);
		gl.glVertex3f(firstQuantrileBoundary, y / 3 * 2, z);
		gl.glEnd();

		// Gradients
		float min = normalizedIQRMin * x;
		float max = normalizedIQRMax * x;

		gl.glBegin(GL2GL3.GL_QUADS);
		gl.glColor4fv(color, 0);
		gl.glVertex3d(firstQuantrileBoundary, y / 3, z);
		gl.glVertex3d(firstQuantrileBoundary, y / 3 * 2, z);

		gl.glColor4f(color[0], color[1], color[2], 0f);
		gl.glVertex3f(min, y / 3 * 2, z);
		gl.glVertex3f(min, y / 3, z);

		gl.glColor4fv(color, 0);
		gl.glVertex3d(thirdQuantrileBoundary, y / 3, z);
		gl.glVertex3d(thirdQuantrileBoundary, y / 3 * 2, z);

		gl.glColor4f(color[0], color[1], color[2], 0f);
		gl.glVertex3f(max, y / 3 * 2, z);
		gl.glVertex3f(max, y / 3, z);
		gl.glEnd();

		// Median
		gl.glColor3f(0, 0, 0);
		gl.glBegin(GL.GL_LINES);
		gl.glVertex3d(normalizedStats.getMedian() * x, y / 3 * 2, z);
		gl.glVertex3d(normalizedStats.getMedian() * x, y / 3, z);
		gl.glEnd();

		gl.glPopName();
	}

}
