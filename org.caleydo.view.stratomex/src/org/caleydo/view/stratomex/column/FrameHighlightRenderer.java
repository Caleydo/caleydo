/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.stratomex.column;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;

import com.google.common.base.Objects;

public class FrameHighlightRenderer extends ALayoutRenderer {

	private static final int OFFSET = 5;

	private float[] color;

	private final boolean topOffset;

	public FrameHighlightRenderer(float[] color, boolean topOffset) {
		this.color = color;
		this.topOffset = topOffset;
	}

	/**
	 * @param color
	 *            setter, see {@link color}
	 */
	public void setColor(float[] color) {
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

		gl.glColor4f(color[0], color[1], color[2], 0.75f);
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
