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
package org.caleydo.view.tourguide.internal.view.ui.pool;

import gleem.linalg.Vec2f;

import org.caleydo.core.util.color.Color;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IMouseLayer;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.vis.rank.model.ARankColumnModel;

/**
 * @author Samuel Gratzl
 *
 */
public class ColumnPoolElem extends APoolElem {
	private final ARankColumnModel model;
	private boolean isDragging;

	public ColumnPoolElem(ARankColumnModel model) {
		this.model = model;
		setLayoutData(model);
		setTooltip(model.getTitle());
	}

	@Override
	protected Color getBackgroundColor() {
		return model.getBgColor();
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		g.color(getBackgroundColor());
		g.fillRoundedRect(0, 0, w, h, 5);
		model.getHeaderRenderer().render(g, w - 6, h - 6, this);
		g.color(armed ? Color.BLACK : Color.GRAY);
		g.drawRoundedRect(0, 0, w, h, 5);
	}

	@Override
	protected void onClicked(Pick pick) {
		if (armed && !pick.isAnyDragging()) {
			pick.setDoDragging(true);
			onDragColumn(pick);
		}
	}

	@Override
	protected void onDoubleClicked(Pick pick) {
		// TODO: add back to tour guide
	}

	@Override
	protected void onMouseReleased(Pick pick) {
		if (pick.isDoDragging())
			onDropColumn(pick);
	}

	private void onDropColumn(Pick pick) {
		IMouseLayer l = context.getMouseLayer();
		if (this.isDragging) {
			if (!l.isDropable(this.model)) {
				l.removeDraggable(this.model);
			}
			this.isDragging = false;
			context.getSWTLayer().setCursor(-1);
			return;
		}
	}

	private void onDragColumn(Pick pick) {
		IMouseLayer l = context.getMouseLayer();
		GLElement elem = new DraggedScoreHeaderItem();
		elem.setSize(getSize().x(), getSize().y());
		Vec2f loc = toRelative(pick.getPickedPoint());
		elem.setLocation(-loc.x(), -loc.y());
		isDragging = true;
		l.addDraggable(elem, this.model);
	}

	class DraggedScoreHeaderItem extends GLElement {
		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			if (ColumnPoolElem.this.getParent() != null)
				ColumnPoolElem.this.renderImpl(g, w, h);
		}
	}

}
