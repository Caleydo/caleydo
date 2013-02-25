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

import static org.caleydo.core.view.opengl.layout2.layout.GLLayouts.defaultValue;
import gleem.linalg.Vec4f;

import java.util.List;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.IMouseLayer;
import org.caleydo.core.view.opengl.layout2.IPopupLayer;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;

/**
 * implementation of {@link IMouseLayer} using a {@link GLElementContainer} by using the layout data for meta data about
 * elements
 *
 * @author Samuel Gratzl
 *
 */
public final class PopupLayer extends GLElementContainer implements IPopupLayer, IGLLayout {

	public PopupLayer() {
		super();
		setLayout(this);
	}

	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
		for (IGLLayoutElement child : children) {
			child.setBounds(defaultValue(child.getSetX(), 0), defaultValue(child.getSetY(), 0),
					defaultValue(child.getSetWidth(), w), defaultValue(child.getSetHeight(), h));
		}
	}

	@Override
	public void show(GLElement popup, Vec4f bounds) {
		show(popup, bounds, FLAG_ALL);
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
	public void show(GLElement popup, Vec4f bounds, int flags) {
		this.add(new PopupElement(popup, bounds, flags));
	}

}
