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
package org.caleydo.core.view.opengl.layout2.internal;

import gleem.linalg.Vec4f;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.IMouseLayer;
import org.caleydo.core.view.opengl.layout2.IPopupLayer;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;

/**
 * implementation of {@link IMouseLayer} using a {@link GLElementContainer} by using the layout data for meta data about
 * elements
 *
 * @author Samuel Gratzl
 *
 */
public final class PopupLayer extends GLElementContainer implements IPopupLayer {

	public PopupLayer() {
		super();
		setLayout(GLLayouts.NONE);
	}

	@Override
	public void show(GLElement popup, Vec4f bounds) {
		show(popup, bounds, true, true);
	}

	@Override
	public void hide(GLElement popup) {
		for(GLElement g : this) {
			if (((PopupElement) g).getContent() == popup) {
				remove(g);
				return;
			}
		}
	}

	@Override
	public void show(GLElement popup, Vec4f bounds, boolean closeAble, boolean resizeAble) {
		this.add(new PopupElement(popup, bounds, closeAble, resizeAble));
	}

}
