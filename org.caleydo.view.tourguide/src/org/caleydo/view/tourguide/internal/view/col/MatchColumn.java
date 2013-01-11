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

import static org.caleydo.core.view.opengl.layout.ElementLayouts.wrap;
import static org.caleydo.view.tourguide.internal.TourGuideRenderStyle.COL_SPACING;
import static org.caleydo.view.tourguide.internal.TourGuideRenderStyle.DATADOMAIN_TYPE_WIDTH;
import static org.caleydo.view.tourguide.internal.TourGuideRenderStyle.GROUP_WIDTH;
import static org.caleydo.view.tourguide.internal.TourGuideRenderStyle.STRATIFACTION_WIDTH;

import java.util.List;

import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.PickingRenderer;
import org.caleydo.view.tourguide.api.query.ScoreQuery;
import org.caleydo.view.tourguide.api.query.ScoringElement;
import org.caleydo.view.tourguide.internal.TourGuideRenderStyle;
import org.caleydo.view.tourguide.internal.renderer.AdvancedTextureRenderer;
import org.caleydo.view.tourguide.internal.view.ScoreQueryUI;
import org.caleydo.view.tourguide.internal.view.StratomexAdapter;

/**
 * @author Samuel Gratzl
 *
 */
public class MatchColumn extends ATableColumn {
	private boolean wasGroupQuery = false;
	private boolean invalidWidth = true;

	private int stratWidth = STRATIFACTION_WIDTH;
	private int groupWidth = GROUP_WIDTH;

	private final StratomexAdapter stratomex;

	public MatchColumn(AGLView view, StratomexAdapter stratomex) {
		super(view);
		this.stratomex = stratomex;
		this.init();
		updateHeader(false);
	}

	@Override
	public ElementLayout createHeader() {
		return createLabel("Match", -1);
	}

	@Override
	protected ElementLayout createHeader2() {
		return new Row();
	}

	@Override
	public void setData(List<ScoringElement> data, ScoreQuery query) {
		this.clearBody();
		boolean isGroupQuery = query.isGroupQuery() || (!data.isEmpty() && data.get(0).getGroup() != null);
		if ((invalidWidth && !data.isEmpty()) || wasGroupQuery != isGroupQuery) {
			recomputeWidth(data);
			updateHeader(isGroupQuery);
		} else if (wasGroupQuery != isGroupQuery) {
			updateHeader(isGroupQuery);
		}

		for (int i = 0; i < data.size(); ++i) {
			ScoringElement elem = data.get(i);
			Group g = elem.getGroup();

			Row r = new Row();
			r.setXDynamic(true);

			AdvancedTextureRenderer cAdd = new AdvancedTextureRenderer(null, view.getTextureManager());
			if (!this.stratomex.contains(elem.getPerspective()))
				cAdd.setImagePath(TourGuideRenderStyle.ICON_ADD_TO_STRATOMEX);
			ElementLayout l = wrap(cAdd, DATADOMAIN_TYPE_WIDTH);
			PickingRenderer pick = new PickingRenderer(ScoreQueryUI.ADD_TO_STRATOMEX, i, view);
			pick.setColor(elem.getDataDomain().getColor());
			l.addBackgroundRenderer(pick);

			r.add(l);

			r.add(colSpacing);
			r.add(createLabel(elem.getStratification(), stratWidth));
			if (isGroupQuery) {
				r.add(colSpacing);
				r.add(createLabel(g, groupWidth));
			}
			this.addTd(r, i);
		}
	}

	public void updateState(List<ScoringElement> data) {
		for (int i = 0; i < data.size(); ++i) {
			ScoringElement elem = data.get(i);
			Row td = (Row) getTd(i);
			AdvancedTextureRenderer r = (AdvancedTextureRenderer) td.get(0).getRenderer();
			if (this.stratomex.contains(elem.getPerspective())
					&& !this.stratomex.isTemporaryPreviewed(elem.getPerspective()))
				r.setImagePath(null);
			else
				r.setImagePath(TourGuideRenderStyle.ICON_ADD_TO_STRATOMEX);
		}
	}

	private void recomputeWidth(List<ScoringElement> data) {
		int longestStrat = getTextWidth("Stratification") - DATADOMAIN_TYPE_WIDTH;
		int longestGroup = getTextWidth("Group");
		for (int i = 0; i < data.size(); ++i) {
			ScoringElement elem = data.get(i);
			Group g = elem.getGroup();
			longestStrat = Math.max(getTextWidth(elem.getStratification()), longestStrat);
			if (g != null)
				longestGroup = Math.max(getTextWidth(g), longestGroup);
		}
		this.groupWidth = Math.min(250, longestGroup);
		if (longestGroup > 0)
			this.stratWidth = Math.min(250, longestStrat);
		else
			this.stratWidth = Math.min(400, longestStrat);
		invalidWidth = (!this.wasGroupQuery && (!data.isEmpty() && data.get(0).getGroup() == null));
	}

	private void updateHeader(boolean isGroupQuery) {
		Row header = (Row) th2;
		this.wasGroupQuery = isGroupQuery;
		header.clear();
		header.add(createLabel("Stratification", DATADOMAIN_TYPE_WIDTH + COL_SPACING + stratWidth));
		this.setPixelSizeX(DATADOMAIN_TYPE_WIDTH + COL_SPACING + stratWidth);
		if (isGroupQuery) {
			header.add(colSpacing);
			header.add(createLabel("Group", groupWidth));
			this.setPixelSizeX(DATADOMAIN_TYPE_WIDTH + COL_SPACING + stratWidth + COL_SPACING + groupWidth);
		}
		header.setXDynamic(true);
	}
}