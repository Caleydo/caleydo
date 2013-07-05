/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout.util;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.layout.ALayoutRenderer;

public class LineSeparatorRenderer extends ALayoutRenderer {

	private boolean isVertical;
	private float lineWidth = 1;

	public LineSeparatorRenderer(boolean isVertical) {
		this.isVertical = isVertical;
	}

	/**
	 * @param lineWidth
	 *            setter, see {@link lineWidth}
	 */
	public void setLineWidth(float lineWidth) {
		if (lineWidth == this.lineWidth)
			return;
		this.lineWidth = lineWidth;
		setDisplayListDirty(true);
	}

	@Override
	protected void renderContent(GL2 gl) {
		gl.glColor3f(0.3f, 0.3f, 0.3f);
		gl.glPushAttrib(GL2.GL_LINE_BIT);
		gl.glLineWidth(lineWidth);
		gl.glBegin(GL.GL_LINES);

		if (isVertical) {
			gl.glVertex3f(x / 2.0f, 0, 0);
			gl.glVertex3f(x / 2.0f, y, 0);

		} else {
			gl.glVertex3f(0, y / 2.0f, 0);
			gl.glVertex3f(x, y / 2.0f, 0);
		}
		gl.glEnd();
		gl.glPopAttrib();

	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return true;
	}
}
