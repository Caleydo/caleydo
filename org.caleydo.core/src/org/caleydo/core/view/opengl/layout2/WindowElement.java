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
package org.caleydo.core.view.opengl.layout2;

import org.caleydo.core.view.opengl.layout2.layout.Layouts;

/**
 * can act as a root element with a dedicated mouse layer
 *
 * @author Samuel Gratzl
 *
 */
public final class WindowElement extends ElementContainer {
	private final Element root;
	private final ElementContainer mouseLayer;

	public WindowElement(Element root) {
		super(Layouts.LAYERS);
		this.root = root;
		this.add(root);
		this.mouseLayer = new ElementContainer(Layouts.NONE);
		this.add(mouseLayer);
	}

	/**
	 * @return the root, see {@link #root}
	 */
	public Element getRoot() {
		return root;
	}

	/**
	 * @return the mouseLayer, see {@link #mouseLayer}
	 */
	public ElementContainer getMouseLayer() {
		return mouseLayer;
	}

	@Override
	protected void renderPickChild(Element child, GLGraphics g) {
		if (child == mouseLayer) // mouse Layer in the front
			g.incZ(1.0f);
		super.renderPickChild(child, g);
		if (child == mouseLayer)
			g.incZ(-1.0f);
	}

	@Override
	protected void renderChild(Element child, GLGraphics g) {
		if (child == mouseLayer) // mouse Layer in the front
			g.incZ(1.0f);
		super.renderChild(child, g);
		if (child == mouseLayer)
			g.incZ(-1.0f);
	}
}
