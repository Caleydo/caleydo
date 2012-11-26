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
package org.caleydo.view.tourguide.renderer;

import static org.caleydo.core.view.opengl.util.GLPrimitives.fillRect;

import javax.media.opengl.GL2;

import org.caleydo.core.util.color.IColor;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;

public class ScoreBarRenderer extends LayoutRenderer {
	private static final float PADDING = 0.0015f; // 0.015f;

	private final float score;

	private final IColor color;

	public ScoreBarRenderer(float score, IColor color) {
		this.score = score;
		this.color = color;
	}

	@Override
	public void renderContent(GL2 gl) {
		if (!Float.isNaN(score)) {
			float padding = 0.0f; // layoutManager == null ? 0.001f :
									// layoutManager.getPixelGLConverter().getPixelHeightForCurrentGLTransform(gl);
			float barWidth = (x - 2 * padding) * score;
			gl.glColor4fv(color.getRGBA(), 0);
			fillRect(gl, padding, padding, barWidth, y - 2 * padding);
		}
	}

	@Override
	protected boolean permitsDisplayLists() {
		return true;
	}
}
