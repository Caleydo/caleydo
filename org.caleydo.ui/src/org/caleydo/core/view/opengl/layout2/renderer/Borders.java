/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2.renderer;

import java.util.Objects;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;

/**
 * factory class for different borders
 *
 * @author Samuel Gratzl
 *
 */
public class Borders {
	/**
	 * create a border similar to DVI
	 */
	public static IBorderGLRenderer createBorder(Color color) {
		return new BorderRenderer(color);
	}

	public static interface IBorderGLRenderer extends IGLRenderer {
		Color getColor();

		IBorderGLRenderer setColor(Color color);

		IBorderGLRenderer setWidth(float width);

		float getWidth();
	}

	private static class BorderRenderer implements IBorderGLRenderer {
		private Color color;
		private float width;

		public BorderRenderer(Color color) {
			this.color = color;
			this.width = 2.f;
		}

		/**
		 * @return the color, see {@link #color}
		 */
		@Override
		public Color getColor() {
			return color;
		}

		/**
		 * @return the width, see {@link #width}
		 */
		@Override
		public float getWidth() {
			return width;
		}

		/**
		 * @param width
		 *            setter, see {@link width}
		 */
		@Override
		public IBorderGLRenderer setWidth(float width) {
			this.width = width;
			return this;
		}

		/**
		 * @param color
		 *            setter, see {@link color}
		 */
		@Override
		public BorderRenderer setColor(Color color) {
			if (Objects.equals(this.color, color))
				return this;
			this.color = color;
			return this;
		}

		@Override
		public void render(GLGraphics g, float w, float h, GLElement parent) {
			if (color == null)
				return;
			g.color(color);
			g.drawRect(0, 0, w, h);
			g.lineWidth(width);
			g.color(color.darker());
			g.drawRect(-width * 0.5f, -width * 0.5f, w + width, h + width);
			g.lineWidth(1);
		}
	}
}
