/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.view.ui.pool;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.tourguide.internal.TourGuideRenderStyle;
import org.caleydo.vis.lineup.model.RankTableModel;
import org.caleydo.vis.lineup.model.StackedRankColumnModel;

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
