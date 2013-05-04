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

import java.awt.Color;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.tourguide.internal.TourGuideRenderStyle;
import org.caleydo.vis.rank.model.ARankColumnModel;
import org.caleydo.vis.rank.model.RankTableModel;
import org.caleydo.vis.rank.model.mixin.IHideableColumnMixin;

/**
 * @author Samuel Gratzl
 *
 */
public class PaperBasket extends APoolElem {
	private final RankTableModel table;

	public PaperBasket(RankTableModel table) {
		this.table = table;
		setTooltip("Drag a pooled element to remove it permanently");
	}

	@Override
	protected void onMouseOver(Pick pick) {
		if (!pick.isAnyDragging() || !context.getMouseLayer().hasDraggable(IHideableColumnMixin.class))
			return;
		Pair<GLElement, IHideableColumnMixin> pair = context.getMouseLayer().getFirstDraggable(
				IHideableColumnMixin.class);
		if (!pair.getSecond().isDestroyAble())
			return;
		this.armed = true;
		context.getMouseLayer().setDropable(IHideableColumnMixin.class, true);
		repaint();
	}

	@Override
	protected void onMouseOut(Pick pick) {
		if (armed) {
			context.getMouseLayer().setDropable(IHideableColumnMixin.class, false);
			armed = false;
			repaint();
		}
	}

	@Override
	protected void onMouseReleased(Pick pick) {
		if (armed) {
			Pair<GLElement, ARankColumnModel> draggable = context.getMouseLayer().getFirstDraggable(
					ARankColumnModel.class);
			context.getMouseLayer().removeDraggable(draggable.getFirst());
			table.removeFromPool(draggable.getSecond());
			context.setCursor(-1);
			armed = false;
			repaint();
		}
	}

	@Override
	protected Color getBackgroundColor() {
		return new Color(192, 192, 192);
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		g.color(armed ? Color.DARK_GRAY : getBackgroundColor());
		g.fillRoundedRect(0, 0, w, h, 5);
		g.fillImage(TourGuideRenderStyle.ICON_BASKET, (w - 12) * .5f, (h - 12) * .5f, 12, 12);
		g.color(Color.GRAY);
		g.drawRoundedRect(0, 0, w, h, 5);
	}
}
