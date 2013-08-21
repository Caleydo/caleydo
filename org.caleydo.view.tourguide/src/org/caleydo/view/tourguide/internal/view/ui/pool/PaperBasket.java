/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.view.ui.pool;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.tourguide.internal.TourGuideRenderStyle;
import org.caleydo.vis.lineup.model.ARankColumnModel;
import org.caleydo.vis.lineup.model.RankTableModel;
import org.caleydo.vis.lineup.model.mixin.IHideableColumnMixin;

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
			context.getSWTLayer().setCursor(-1);
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
