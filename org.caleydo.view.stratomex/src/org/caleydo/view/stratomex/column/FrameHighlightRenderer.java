/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.stratomex.column;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.StyledColor;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;

import com.google.common.base.Objects;

public class FrameHighlightRenderer extends ALayoutRenderer {

	private static final int OFFSET = 5;

	private Color color;

	private final boolean topOffset;

	public FrameHighlightRenderer(Color color, boolean topOffset) {
		this.color = color;
		this.topOffset = topOffset;
	}

	/**
	 * @param color
	 *            setter, see {@link color}
	 */
	public void setColor(Color color) {
		if (Objects.equal(this.color, color))
			return;
		this.color = color;
		setDisplayListDirty(true);
	}

	@Override
	public void renderContent(GL2 gl) {
		if (color == null)
			return;
		PixelGLConverter pixelGLConverter = layoutManager.getPixelGLConverter();
		float xoffset = pixelGLConverter.getGLWidthForPixelWidth(OFFSET);
		float yoffset = (topOffset ? 1 : -1) * pixelGLConverter.getGLHeightForPixelHeight(OFFSET);

		gl.glPushAttrib(GL2.GL_LINE_BIT);

		gl.glLineWidth(3);
		gl.glEnable(GL.GL_LINE_SMOOTH);

		gl.glColor4f(color.r, color.g, color.b, 0.75f);

		if (color instanceof StyledColor) {
			((StyledColor) color).setClearManually(gl);
		}

		gl.glBegin(GL.GL_LINE_LOOP);
		{
			gl.glVertex3f(-xoffset, -yoffset, 0);
			gl.glVertex3f(x + xoffset, -yoffset, 0);
			gl.glVertex3f(x + xoffset, y + yoffset, 0);
			gl.glVertex3f(-xoffset, y + yoffset, 0);
		}
		gl.glEnd();
		gl.glPopAttrib();
	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return true;
	}
}
