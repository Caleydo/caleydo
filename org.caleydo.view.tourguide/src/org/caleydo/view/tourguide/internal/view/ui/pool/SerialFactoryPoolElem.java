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

import org.caleydo.core.util.color.Color;

import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.tourguide.internal.TourGuideRenderStyle;
import org.caleydo.vis.rank.model.RankTableModel;
import org.caleydo.vis.rank.model.StackedRankColumnModel;

/**
 * @author Samuel Gratzl
 *
 */
public class SerialFactoryPoolElem extends APoolElem {

	private final RankTableModel table;

	public SerialFactoryPoolElem(RankTableModel table) {
		setTooltip("Double-Click to create an empty serial combined column");
		this.table = table;
	}

	@Override
	protected Color getBackgroundColor() {
		return Color.WHITE;
	}

	@Override
	protected void onDoubleClicked(Pick pick) {
		StackedRankColumnModel m = new StackedRankColumnModel();
		m.setWidth(100);
		table.add(m);
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		float x = 0;
		x = 10;
		g.fillImage(TourGuideRenderStyle.ICON_ADD_COLOR, 1, (h - 10) * 0.5f, 10, 10);
		g.drawText("Serial Combiner", x + 2, 3, w - 2 - x, h - 9);
		g.color(armed ? Color.BLACK : Color.DARK_GRAY);
		g.drawRoundedRect(0, 0, w, h, 5);
	}
}
