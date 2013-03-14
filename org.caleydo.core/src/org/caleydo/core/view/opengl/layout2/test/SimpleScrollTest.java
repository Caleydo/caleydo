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
package org.caleydo.core.view.opengl.layout2.test;

import java.awt.Color;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.GLSandBox;
import org.caleydo.core.view.opengl.layout2.basic.ScrollBar;
import org.caleydo.core.view.opengl.layout2.basic.ScrolledGLElement;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;

/**
 * @author Samuel Gratzl
 *
 */
public class SimpleScrollTest extends GLElementContainer {

	/**
	 *
	 */
	public SimpleScrollTest() {
		setLayout(GLLayouts.LAYERS);
		GLElement g = new Content();
		add(new ScrolledGLElement(g, new ScrollBar(true), new ScrollBar(false), 10));
	}

	private static class Content extends GLElement {
		public Content() {
			setSize(500, 500);
		}
		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			g.color(Color.RED).fillRect(0, 0, w, h);
			g.color(Color.BLUE).fillRect(20, 20, w - 40, h - 40);
			super.renderImpl(g, w, h);
		}

	}

	public static void main(String[] args) {
		GLSandBox.main(args, new SimpleScrollTest());
	}
}
