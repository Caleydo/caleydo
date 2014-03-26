/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.view.ui.pool;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.dnd.EDnDType;
import org.caleydo.core.view.opengl.layout2.dnd.IDnDItem;
import org.caleydo.core.view.opengl.layout2.dnd.IDragInfo;
import org.caleydo.core.view.opengl.layout2.dnd.IDropGLTarget;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.tourguide.internal.TourGuideRenderStyle;
import org.caleydo.vis.lineup.model.ARankColumnModel;
import org.caleydo.vis.lineup.model.ColumnDragInfo;
import org.caleydo.vis.lineup.model.RankTableModel;
import org.caleydo.vis.lineup.model.mixin.IHideableColumnMixin;

/**
 * @author Samuel Gratzl
 *
 */
public class PaperBasket extends APoolElem implements IDropGLTarget {
	private final RankTableModel table;

	public PaperBasket(RankTableModel table) {
		this.table = table;
		setTooltip("Drag a pooled element to remove it permanently");
	}

	@Override
	protected void onMouseOver(Pick pick) {
		context.getMouseLayer().addDropTarget(this);
	}

	@Override
	public boolean canSWTDrop(IDnDItem input) {
		IDragInfo info = input.getInfo();
		if (!(info instanceof ColumnDragInfo))
			return false;
		ARankColumnModel model = ((ColumnDragInfo) info).getModel();
		if (!(model instanceof IHideableColumnMixin) || !model.isDestroyAble())
			return false;
		this.armed = true;
		repaint();
		return true;
	}

	@Override
	public void onItemChanged(IDnDItem input) {

	}

	@Override
	public void onDrop(IDnDItem info) {
		ARankColumnModel model = ((ColumnDragInfo) info.getInfo()).getModel();
		table.removeFromPool(model);
		context.getSWTLayer().setCursor(-1);
		armed = false;
		repaint();
	}

	@Override
	protected void onMouseOut(Pick pick) {
		context.getMouseLayer().removeDropTarget(this);
		if (armed) {
			armed = false;
			repaint();
		}
	}

	@Override
	public EDnDType defaultSWTDnDType(IDnDItem item) {
		return EDnDType.MOVE;
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
