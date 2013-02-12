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

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;

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
	public static final IGLRenderer RECT = new IGLRenderer() {
		@Override
		public void render(GLGraphics g, float w, float h, GLElement parent) {
			g.fillRect(0, 0, w, h);
		}
	};

	private GLRenderers() {

	}

	/**
	 * renders a full sized rect with the specified color
	 *
	 * @param color
	 *            the color to use
	 * @return
	 */
	public static IGLRenderer fillRect(final Color color) {
		return new IGLRenderer() {
			@Override
			public void render(GLGraphics g, float w, float h, GLElement parent) {
				g.color(color).fillRect(0, 0, w, h);
			}
		};
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
		return new IGLRenderer() {

			@Override
			public void render(GLGraphics g, float w, float h, GLElement parent) {
				g.drawText(text, 0, 0, w, h);
			}
		};
	}

}
