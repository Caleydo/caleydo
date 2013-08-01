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

		void setColor(Color color);
	}

	private static class BorderRenderer implements IBorderGLRenderer {
		private Color color;

		public BorderRenderer(Color color) {
			this.color = color;
		}

		/**
		 * @return the color, see {@link #color}
		 */
		@Override
		public Color getColor() {
			return color;
		}

		/**
		 * @param color
		 *            setter, see {@link color}
		 */
		@Override
		public void setColor(Color color) {
			if (Objects.equals(this.color, color))
				return;
			this.color = color;
		}

		@Override
		public void render(GLGraphics g, float w, float h, GLElement parent) {
			if (color == null)
				return;
			g.color(color);
			g.drawRect(0, 0, w, h);
			g.lineWidth(2);
			g.color(color.darker());
			g.drawRect(-1, -1, w + 2, h + 2);
			g.lineWidth(1);
		}
	}
}
