/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2.renderer;


import java.net.URL;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.util.text.ETextStyle;

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

	public static IGLRenderer drawText(final String text) {
		return drawText(text, VAlign.LEFT);
	}

	public static IGLRenderer drawText(final String text, final VAlign valign) {
		return drawText(text, valign, GLPadding.ZERO);
	}
	public static IGLRenderer drawText(final String text, final VAlign valign, final GLPadding padding) {
		return drawText(text, valign, padding, ETextStyle.PLAIN);
	}

	public static IGLRenderer drawText(final String text, final VAlign valign, final GLPadding padding,
			final ETextStyle style) {
		return new IGLRenderer() {

			@Override
			public void render(GLGraphics g, float w, float h, GLElement parent) {
				String t = text;
				if (text.indexOf('\n') >= 0) {
					t = text.substring(0, text.indexOf('\n'));
				}
				g.drawText(t, padding.left, padding.top, w - padding.hor(), h - padding.vert(), valign, style);
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

	public static IGLRenderer fillImage(final URL image) {
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

	public static IGLRenderer drawHorLine(Color color, float lineWidth, float offset) {
		return new LineRenderer(color, lineWidth, offset, true);
	}

	public static IGLRenderer drawVertLine(Color color, float lineWidth, float offset) {
		return new LineRenderer(color, lineWidth, offset, true);
	}

	private static class LineRenderer implements IGLRenderer {
		private final Color color;
		private final float lineWidth;
		private final float offset;
		private final boolean hor;

		public LineRenderer(Color color, float lineWidth, float offset, boolean hor) {
			this.color = color;
			this.lineWidth = lineWidth;
			this.offset = offset;
			this.hor = hor;
		}

		@Override
		public void render(GLGraphics g, float w, float h, GLElement parent) {
			g.color(color);
			if (lineWidth != 1)
				g.lineWidth(lineWidth);
			if (hor)
				g.drawLine(offset, h * 0.5f, w - offset, h * 0.5f);
			else
				g.drawLine(w * 0.5f, offset, w * 0.5f, h - offset);
			if (lineWidth != 1)
				g.lineWidth(1);
		}
	}

}
