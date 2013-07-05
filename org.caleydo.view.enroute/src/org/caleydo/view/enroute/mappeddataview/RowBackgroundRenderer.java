/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.enroute.mappeddataview;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.layout.ALayoutRenderer;

/**
 * Renders the background of a row
 * 
 * @author Alexander Lex
 * 
 */
public class RowBackgroundRenderer extends ALayoutRenderer {

	private float[] backgroundColor;

	// private float[] frameColor = { 0, 0, 0, 1 };

	/**
	 * 
	 */
	public RowBackgroundRenderer(float[] backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	@Override
	public void renderContent(GL2 gl) {

		float backgroundZ = 0;
		float frameZ = 0.3f;

		gl.glColor4fv(backgroundColor, 0);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f(0, 0, backgroundZ);
		gl.glVertex3f(0, y, backgroundZ);
		gl.glColor3f(backgroundColor[0] + 0.1f, backgroundColor[0] + 0.1f,
				backgroundColor[0] + 0.1f);
		gl.glVertex3f(x, y, backgroundZ);
		gl.glVertex3f(x, 0, backgroundZ);
		gl.glEnd();

		gl.glLineWidth(1);
		gl.glColor4fv(MappedDataRenderer.FRAME_COLOR, 0);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(0, 0, frameZ);
		gl.glVertex3f(0, y, frameZ);
		gl.glVertex3f(x, y, frameZ);
		gl.glVertex3f(x, 0, frameZ);
		gl.glEnd();

	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return true;
	}

}
