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
package org.caleydo.vis.rank.ui.column;

import static org.caleydo.vis.rank.ui.RenderStyle.HIST_HEIGHT;
import static org.caleydo.vis.rank.ui.RenderStyle.LABEL_HEIGHT;

import java.util.List;

import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.vis.rank.model.FrozenRankColumnModel;
import org.caleydo.vis.rank.ui.RenderStyle;
/**
 * @author Samuel Gratzl
 *
 */
public class FrozenColumnHeaderUI extends ACompositeTableColumnHeaderUI<FrozenRankColumnModel> {
	public FrozenColumnHeaderUI(FrozenRankColumnModel model, boolean interactive) {
		super(model, interactive);
		super.add(0, new FrozenSummaryHeaderUI(model, interactive));
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		g.color(model.getBgColor()).fillRect(0, HIST_HEIGHT + LABEL_HEIGHT, w, h - HIST_HEIGHT - LABEL_HEIGHT);

		super.renderImpl(g, w, h);
	}

	@Override
	protected float getLeftPadding() {
		return RenderStyle.FROZEN_BAND_WIDTH;
	}

	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
		IGLLayoutElement summary = children.get(0);
		summary.setBounds(0, 0, w, HIST_HEIGHT + LABEL_HEIGHT);
		super.doLayout(children, w, h);
	}
}

