/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2.renderer;

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
	public static IGLRenderer createBorder(Color color) {
		return new BorderRenderer(color);
	}

	private static class BorderRenderer implements IGLRenderer {
		private final Color color;

		public BorderRenderer(Color color) {
			this.color = color;
		}

		@Override
		public void render(GLGraphics g, float w, float h, GLElement parent) {
			g.color(color);
			g.drawRect(0, 0, w, h);
			g.lineWidth(2);
			g.color(color.darker());
			g.drawRect(-1, -1, w + 2, h + 2);
			g.lineWidth(1);
		}
	}
}
