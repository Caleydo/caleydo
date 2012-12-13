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
package org.caleydo.view.tourguide.vendingmachine.col;

import static org.caleydo.core.view.opengl.layout.ElementLayouts.createColor;
import static org.caleydo.core.view.opengl.layout.ElementLayouts.createXSpacer;
import static org.caleydo.view.tourguide.renderstyle.TourGuideRenderStyle.COL_SPACING;
import static org.caleydo.view.tourguide.renderstyle.TourGuideRenderStyle.DATADOMAIN_TYPE_WIDTH;
import static org.caleydo.view.tourguide.renderstyle.TourGuideRenderStyle.GROUP_WIDTH;
import static org.caleydo.view.tourguide.renderstyle.TourGuideRenderStyle.STRATIFACTION_WIDTH;

import java.util.List;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.view.tourguide.data.ScoreQuery;
import org.caleydo.view.tourguide.data.ScoringElement;

/**
 * @author Samuel Gratzl
 *
 */
public class MatchColumn extends ATableColumn {
	private boolean wasGroupQuery = false;
	private boolean invalidWidth = true;
	private int stratWidth = STRATIFACTION_WIDTH;
	private int groupWidth = GROUP_WIDTH;

	public MatchColumn(AGLView view) {
		super(view);
		this.init();
		updateHeader(false);
		this.setXDynamic(true);
	}

	@Override
	public ElementLayout createHeader() {
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
			TablePerspective strat = elem.getStratification();
			Group g = elem.getGroup();

			Row r = new Row();
			r.setXDynamic(true);
			r.add(createColor(strat.getDataDomain().getColor(), DATADOMAIN_TYPE_WIDTH));
			r.add(colSpacing);
			r.add(createLabel(strat.getRecordPerspective(), stratWidth));
			if (isGroupQuery) {
				r.add(colSpacing);
				r.add(createLabel(g, groupWidth));
			}
			this.addTd(r, i);
		}
	}

	private void recomputeWidth(List<ScoringElement> data) {
		int longestStrat = "Stratificat".length();
		int longestGroup = "Group ".length();
		for (int i = 0; i < data.size(); ++i) {
			ScoringElement elem = data.get(i);
			TablePerspective strat = elem.getStratification();
			Group g = elem.getGroup();
			longestStrat = Math.max(strat.getRecordPerspective().getLabel().length(), longestStrat);
			if (g != null)
				longestGroup = Math.max(g.getLabel().length(), longestGroup);
		}
		longestStrat = guessWidth(longestStrat);
		longestGroup = guessWidth(longestGroup);
		this.groupWidth = Math.min(250, longestGroup);
		if (longestGroup > 0)
			this.stratWidth = Math.min(250, longestStrat);
		else
			this.stratWidth = Math.min(400, longestStrat);
		invalidWidth = (!this.wasGroupQuery && (!data.isEmpty() && data.get(0).getGroup() == null));
	}

	/**
	 * guesses the length of the text to render in pixels
	 *
	 * @param textLength
	 * @return
	 */
	private static int guessWidth(int textLength) {
		return textLength * 8;
	}

	private void updateHeader(boolean isGroupQuery) {
		Row header = (Row) th;
		this.wasGroupQuery = isGroupQuery;
		header.clear();
		header.add(createXSpacer(DATADOMAIN_TYPE_WIDTH + COL_SPACING));
		header.add(createLabel("Stratification", stratWidth));
		if (isGroupQuery) {
			header.add(colSpacing);
			header.add(createLabel("Group", groupWidth));
		}
		header.setXDynamic(true);
	}
}