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
package org.caleydo.core.view.opengl.layout2.renderer;

import java.awt.Color;

import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;

/**
 * factory class for {@link IGLRenderer}
 *
 * @author Samuel Gratzl
 *
 */
public final class GLRenderers {
	/**
	 * dummy renderer, which does nothing
	 */
	public static final IGLRenderer DUMMY = new IGLRenderer() {
		@Override
		public void render(GLGraphics g, float w, float h, GLElement parent) {

		}
	};

	/**
	 * renders a full sized transparent rect
	 */
	public static final IGLRenderer RECT = fillRect(null);

	private GLRenderers() {

	}

	public static IGLRenderer drawRect(Color color) {
		return new SimpleRenderer(EWhat.DRAW_RECT, color);
	}

	public static IGLRenderer drawRoundedRect(Color color) {
		return new SimpleRenderer(EWhat.DRAW_ROUNDED_RECT, color);
	}
	/**
	 * renders a full sized rect with the specified color
	 *
	 * @param color
	 *            the color to use
	 * @return
	 */
	public static IGLRenderer fillRect(Color color) {
		return new SimpleRenderer(EWhat.FILL_RECT, color);
	}

	/**
	 * @param color
	 * @return
	 */
	public static IGLRenderer fillRoundedRect(Color color) {
		return new SimpleRenderer(EWhat.FILL_ROUNDED_RECT, color);
	}

	public static Runnable asRunnable(final IGLRenderer renderer, final GLGraphics g, final float w, final float h,
			final GLElement parent) {
		return new Runnable() {
			@Override
			public void run() {
				renderer.render(g, w, h, parent);
			}
		};
	}

	public static IGLRenderer drawText(final String text) {
		return drawText(text, VAlign.LEFT);
	}

	public static IGLRenderer drawText(final String text, final VAlign valign) {
		return drawText(text, valign, GLPadding.ZERO);
	}

	public static IGLRenderer drawText(final String text, final VAlign valign, final GLPadding padding) {
		return new IGLRenderer() {

			@Override
			public void render(GLGraphics g, float w, float h, GLElement parent) {
				g.drawText(text, padding.left, padding.top, w - padding.hor(), h - padding.vert(), valign);
			}

			@Override
			public String toString() {
				return text;
			}
		};
	}

	public static IGLRenderer fillImage(final String image) {
		return new IGLRenderer() {

			@Override
			public void render(GLGraphics g, float w, float h, GLElement parent) {
				g.fillImage(image, 0, 0, w, h);
			}
		};
	}

	private enum EWhat {
		FILL_RECT, DRAW_RECT, DRAW_DIAGONAL_LINE, DRAW_ROUNDED_RECT, FILL_ROUNDED_RECT
	}

	private static class SimpleRenderer implements IGLRenderer {
		private final EWhat what;
		private final Color color;

		public SimpleRenderer(EWhat what, Color color) {
			this.what = what;
			this.color = color;
		}

		@Override
		public void render(GLGraphics g, float w, float h, GLElement parent) {
			if (color != null)
				g.color(color);
			switch (what) {
			case DRAW_DIAGONAL_LINE:
				g.drawDiagonalLine(0, 0, w, h);
				break;
			case DRAW_ROUNDED_RECT:
				g.drawRoundedRect(0, 0, w, h, Math.min(w, h) * 0.25f);
				break;
			case DRAW_RECT:
				g.drawRect(0, 0, w, h);
				break;
			case FILL_ROUNDED_RECT:
				g.fillRoundedRect(0, 0, w, h, Math.min(w, h) * 0.25f);
				break;
			case FILL_RECT:
				g.fillRect(0, 0, w, h);
				break;
			}
		}
	}

}
