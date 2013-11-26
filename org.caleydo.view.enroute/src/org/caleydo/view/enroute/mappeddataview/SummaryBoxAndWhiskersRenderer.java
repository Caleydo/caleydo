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
public class SummaryBoxAndWhiskersRenderer extends AMedianBasedSummaryRenderer {

	/**
	 * @param contentRenderer
	 */
	public SummaryBoxAndWhiskersRenderer(ContentRenderer contentRenderer) {
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

		gl.glBegin(GL2GL3.GL_QUADS);
		// gl.glColor4fv(color, 0);
		gl.glColor3fv(contentRenderer.dataDomain.getColor().getRGB(), 0);
		gl.glVertex3f(firstQuantrileBoundary, y / 3, z);
		gl.glVertex3d(thirdQuantrileBoundary, y / 3, z);
		gl.glVertex3d(thirdQuantrileBoundary, y / 3 * 2, z);
		gl.glVertex3f(firstQuantrileBoundary, y / 3 * 2, z);
		gl.glEnd();

		float min = normalizedIQRMin * x;
		float max = normalizedIQRMax * x;

		float lineTailHeight = contentRenderer.parentView.getPixelGLConverter().getGLHeightForPixelHeight(3);

		// Median
		gl.glColor3f(1f, 1f, 1f);
		gl.glBegin(GL.GL_LINES);
		gl.glVertex3d(normalizedStats.getMedian() * x, y / 3 * 2, z);
		gl.glVertex3d(normalizedStats.getMedian() * x, y / 3, z);

		// Whiskers
		gl.glColor3f(0, 0, 0);
		gl.glVertex3f(min, y / 2, z);
		gl.glVertex3f(firstQuantrileBoundary, y / 2, z);

		gl.glVertex3f(thirdQuantrileBoundary, y / 2, z);
		gl.glVertex3f(max, y / 2, z);

		// gl.glLineWidth(0.6f);
		//
		gl.glVertex3f(min, y / 2 - lineTailHeight, z);
		gl.glVertex3f(min, y / 2 + lineTailHeight, z);

		gl.glVertex3f(max, y / 2 - lineTailHeight, z);
		gl.glVertex3f(max, y / 2 + lineTailHeight, z);

		gl.glEnd();

		gl.glBegin(GL.GL_LINE_LOOP);
		// gl.glColor4fv(color, 0);
		gl.glColor3f(0f, 0f, 0f);
		gl.glVertex3f(firstQuantrileBoundary, y / 3, z);
		gl.glVertex3d(thirdQuantrileBoundary, y / 3, z);
		gl.glVertex3d(thirdQuantrileBoundary, y / 3 * 2, z);
		gl.glVertex3f(firstQuantrileBoundary, y / 3 * 2, z);
		gl.glEnd();

		gl.glPopName();

	}

}
