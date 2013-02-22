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
package org.caleydo.view.tourguide.internal.view.col;

import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.view.tourguide.internal.view.PerspectiveRow;
import org.caleydo.view.tourguide.internal.view.StratomexAdapter;
import org.caleydo.view.tourguide.v3.model.StringRankColumnModel;

/**
 * @author Samuel Gratzl
 *
 */
public class PerspectiveRankColumnModel extends StringRankColumnModel {
	private final StratomexAdapter stratomex;

	public PerspectiveRankColumnModel(StratomexAdapter stratomex) {
		super(GLRenderers.drawText("Match", VAlign.CENTER), StringRankColumnModel.DFEAULT);
		this.stratomex = stratomex;
	}

	@Override
	public boolean isDestroyAble() {
		return false;
	}

	@Override
	public void render(GLGraphics g, float w, float h, GLElement parent) {
		float hint = Math.min(h - 2, 12);
		if (hint <= 0)
			return;
		PerspectiveRow r = parent.getLayoutDataAs(PerspectiveRow.class, null);
		g.color(r.getDataDomain().getColor()).fillRect(1, (h - hint) * 0.5f, hint, hint);
		if (h < 5 || w < 20)
			return;
		float x = hint + 2;
		float hi = Math.min(h, 18);
		g.drawText(r.getLabel(), x, 1 + (h - hi) * 0.5f, w - 2 - x, hi - 2);

		// TODO add to Stratomex Button
	}

	// AdvancedTextureRenderer cAdd = new AdvancedTextureRenderer(null, view.getTextureManager());
	// if (!this.stratomex.contains(elem.getPerspective()))
	// cAdd.setImagePath(TourGuideRenderStyle.ICON_ADD_TO_STRATOMEX);
	// PickingRenderer pick = new PickingRenderer(ScoreQueryUI.ADD_TO_STRATOMEX, i, view);
	// pick.setColor(elem.getDataDomain().getColor());
	// l.addBackgroundRenderer(pick);
	//
	// r.add(l);
	//
	// r.add(colSpacing);
	// if (isGroupQuery) {
	// r.add(createLabel(elem.getStratification(), stratWidth));
	// r.add(colSpacing);
	// r.add(createLabel(g, -1));
	// } else {
	// r.add(createLabel(elem.getStratification(), -1));
	// }
	// this.addTd(r, i);
	// }
	// }
	//
	// public void updateState(List<ScoringElement> data) {
	// for (int i = 0; i < data.size(); ++i) {
	// ScoringElement elem = data.get(i);
	// Row td = (Row) getTd(i);
	// AdvancedTextureRenderer r = (AdvancedTextureRenderer) td.get(0).getRenderer();
	// if (this.stratomex.contains(elem.getPerspective())
	// && !this.stratomex.isTemporaryPreviewed(elem.getPerspective()))
	// r.setImagePath(null);
	// else
	// r.setImagePath(TourGuideRenderStyle.ICON_ADD_TO_STRATOMEX);
	// }
	// }
	//
	// private void updateHeader(boolean isGroupQuery) {
	// Row header = (Row) th2;
	// this.wasGroupQuery = isGroupQuery;
	// header.clear();
	// if (isGroupQuery) {
	// header.add(colSpacing);
	// header.add(createLabel("Group", -1));
	// this.setPixelSizeX(DATADOMAIN_TYPE_WIDTH + COL_SPACING + stratWidth + COL_SPACING + groupWidth);
	// } else {
	// header.add(createLabel("Stratification", -1));
	// this.setPixelSizeX(DATADOMAIN_TYPE_WIDTH + COL_SPACING + stratWidth);
	// }
	// header.setXDynamic(true);
	// }
}