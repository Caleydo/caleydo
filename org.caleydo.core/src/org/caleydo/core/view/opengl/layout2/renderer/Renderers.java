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

import org.caleydo.core.util.color.Colors;
import org.caleydo.core.view.opengl.layout2.Element;
import org.caleydo.core.view.opengl.layout2.GLGraphics;

/**
 * factory class for {@link IRenderer}
 * 
 * @author Samuel Gratzl
 * 
 */
public final class Renderers {
	/**
	 * dummy renderer, which does nothing
	 */
	public static final IRenderer DUMMY = new IRenderer() {
		@Override
		public void render(GLGraphics g, float w, float h, Element parent) {

		}
	};

	/**
	 * renders a full sized transparent rect
	 */
	public static final IRenderer TRANSPARENT_RECT = new IRenderer() {
		@Override
		public void render(GLGraphics g, float w, float h, Element parent) {
			g.color(Colors.TRANSPARENT).fillRect(0, 0, w, h);
		}
	};

	private Renderers() {

	}

	/**
	 * renders a full sized rect with the specified color
	 * 
	 * @param color
	 *            the color to use
	 * @return
	 */
	public static IRenderer fillRect(final Color color) {
		return new IRenderer() {
			@Override
			public void render(GLGraphics g, float w, float h, Element parent) {
				g.color(color).fillRect(0, 0, w, h);
			}
		};
	}

	public static Runnable asRunnable(final IRenderer renderer, final GLGraphics g, final float w, final float h,
			final Element parent) {
		return new Runnable() {
			@Override
			public void run() {
				renderer.render(g, w, h, parent);
			}
		};
	}

	public static IRenderer drawText(final String text) {
		return new IRenderer() {

			@Override
			public void render(GLGraphics g, float w, float h, Element parent) {
				g.drawText(text, 0, 0, w, h);
			}
		};
	}

}
