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
package org.caleydo.view.tourguide.internal.renderer;

import static org.caleydo.core.view.opengl.util.GLPrimitives.fillRect;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.util.color.Colors;
import org.caleydo.core.util.color.IColor;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.util.GLPrimitives;
import org.caleydo.view.tourguide.internal.TourGuideRenderStyle;

public class ScoreBarRenderer extends LayoutRenderer {
	private final float score;

	private final IColor color;

	public ScoreBarRenderer(float score, IColor color) {
		this.score = score;
		this.color = color == null ? TourGuideRenderStyle.DEFAULT_SCORE_COLOR : color;
	}

	@Override
	public void renderContent(GL2 gl) {
		if (!Float.isNaN(score)) {
			float paddingX = oneXPixel(gl) * 2;
			float paddingY = oneYPixel(gl) * 2;
			float barWidth = (x - 2 * paddingX) * Math.min(Math.max(score, 0), 1);
			gl.glColor4fv(color.getRGBA(), 0);
			fillRect(gl, paddingX, paddingY, barWidth, y - 2 * paddingY);
		}
		gl.glColor4fv(Colors.rgba(java.awt.Color.black), 0);
		gl.glPushAttrib(GL.GL_LINE_WIDTH);
		gl.glLineWidth(1);
		GLPrimitives.drawRect(gl, 0, 0, x, y);
		gl.glPopAttrib();
	}

	/**
	 * @return
	 */
	private float oneXPixel(GL2 gl) {
		if (elementLayout.getLayoutManager() == null)
			return 0.001f;
		return elementLayout.getLayoutManager().getPixelGLConverter().getGLHeightForPixelHeight(1);
	}

	private float oneYPixel(GL2 gl) {
		if (elementLayout.getLayoutManager() == null)
			return 0.001f;
		return elementLayout.getLayoutManager().getPixelGLConverter().getGLHeightForPixelHeight(1);
	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return true;
	}
}
