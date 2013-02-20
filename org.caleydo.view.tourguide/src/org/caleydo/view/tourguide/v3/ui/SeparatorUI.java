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
package org.caleydo.view.tourguide.v3.ui;

import java.awt.Color;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IMouseLayer;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.tourguide.v3.model.ARankColumnModel;

/**
 * @author Samuel Gratzl
 *
 */
public class SeparatorUI extends PickableGLElement {
	private int index;
	private boolean armed = false;

	/**
	 * @param index
	 *            setter, see {@link index}
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		if (armed) {
			g.color(Color.BLACK).fillRect(0, 0, w, h);
		}
	}

	@Override
	protected void onMouseOver(Pick pick) {
		if (!pick.isAnyDragging())
			return;
		IMouseLayer m = context.getMouseLayer();
		if (!m.hasDraggable(ARankColumnModel.class))
			return;
		Pair<GLElement, ARankColumnModel> info = m.getFirstDraggable(ARankColumnModel.class);

		m.setDropable(ARankColumnModel.class, true);
		armed = true;
		repaint();
	}

	@Override
	protected void onMouseOut(Pick pick) {
		if (!armed)
			return;
		IMouseLayer m = context.getMouseLayer();
		m.setDropable(ARankColumnModel.class, false);
		armed = false;
		repaint();
	}

	@Override
	protected void onMouseReleased(Pick pick) {
		if (!armed)
			return;
		IMouseLayer m = context.getMouseLayer();
		m.setDropable(ARankColumnModel.class, false);
		Pair<GLElement, ARankColumnModel> info = m.getFirstDraggable(ARankColumnModel.class);
		m.removeDraggable(info.getFirst());
		context.setCursor(-1);
		armed = false;
		// TODO perform
	}
}
