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
package org.caleydo.core.view.opengl.layout2.basic;

import gleem.linalg.Vec2f;

import org.caleydo.core.view.opengl.picking.Pick;

/**
 * scrollbar implementation for the advanced picking managers
 * 
 * @author Samuel Gratzl
 * 
 */
public class ScrollBar extends AScrollBar {

	public ScrollBar(boolean isHorizontal) {
		super(isHorizontal);
	}

	@Override
	public void pick(Pick pick) {
		if (pick.isAnyDragging() && !pick.isDoDragging())
			return;
		if (callback == null)
			return;
		switch (pick.getPickingMode()) {
		case MOUSE_OVER:
			hovered = true;
			break;
		case CLICKED:
			Vec2f relative = callback.toRelative(pick.getPickedPoint());
			float t = callback.getTotal(this);
			float v = this.total / t;
			if (isHorizontal)
				v *= relative.x();
			else
				v *= relative.y();
			if (v < offset || v > (offset + view)) {
				v = Math.min(total - view, Math.max(0, v));
				callback.onScrollBarMoved(this, v);
			} else
				pick.setDoDragging(true);
			callback.repaint();
			break;
		case DRAGGED:
			if (!pick.isDoDragging())
				return;
			float vd = this.total / callback.getTotal(this);
			if (isHorizontal)
				vd *= pick.getDx();
			else
				vd *= pick.getDy();
			callback.onScrollBarMoved(this, offset + vd);
			break;
		case MOUSE_RELEASED:
			break;
		case MOUSE_OUT:
			hovered = false;
			callback.repaint();
			break;
		default:
			break;
		}
	}

}