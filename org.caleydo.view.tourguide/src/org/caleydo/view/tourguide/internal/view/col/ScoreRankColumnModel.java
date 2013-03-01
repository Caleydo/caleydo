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

import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.view.tourguide.spi.score.IGroupScore;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.caleydo.view.tourguide.spi.score.IStratificationScore;
import org.caleydo.vis.rank.model.FloatRankColumnModel;
import org.caleydo.vis.rank.ui.IColumnRenderInfo;

/**
 * @author Samuel Gratzl
 *
 */
public class ScoreRankColumnModel extends FloatRankColumnModel implements IGLRenderer {
	private final IScore score;
	private EHeaderMode headerMode;

	private enum EHeaderMode {
		LABEL, STRAT, STRAT_GROUP
	}

	public ScoreRankColumnModel(IScore score) {
		super(score, null, score.getColor(), score.getBGColor(), score.createMapping());
		this.score = score;
		this.headerMode = EHeaderMode.LABEL;
		if (score instanceof IGroupScore) {// have a group and a common stratification
			this.headerMode = EHeaderMode.STRAT_GROUP;
		} else if (score instanceof IStratificationScore) {
			boolean hasStrat = ((IStratificationScore) score).getStratification() != null;
			if (hasStrat)
				this.headerMode = EHeaderMode.STRAT;
		}
		setHeaderRenderer(this);
	}

	/**
	 * @return the score, see {@link #score}
	 */
	public IScore getScore() {
		return score;
	}
	@Override
	public void render(GLGraphics g, float w, float h, GLElement parent) {
		Perspective strat = resolveStratification(score);
		float x = 0;
		if (headerMode == EHeaderMode.STRAT || headerMode == EHeaderMode.STRAT_GROUP) {
			g.color(strat.getDataDomain().getColor()).fillRect(1, 1, 10, 10);
			x += 12;
		}
		if (((IColumnRenderInfo) parent.getParent()).isCollapsed())
			return;
		g.drawText(this.score, x, 1, w - x, 12);
	}

	private static Perspective resolveStratification(IScore score) {
		if (score instanceof IStratificationScore)
			return ((IStratificationScore) score).getStratification();
		return null;
	}

	private static Group resolveGroup(IScore score) {
		if (score instanceof IGroupScore)
			return ((IGroupScore) score).getGroup();
		return null;
	}

	// private ElementLayout createValue(ScoringElement elem, int id) {
//		Row row = new Row();
//		row.setGrabY(true);
//		row.setXDynamic(false);
//		IScore underlyingScore = this.score;
//		if (underlyingScore instanceof CollapseScore)
//			underlyingScore = elem.getSelected((CollapseScore) underlyingScore);
//		Perspective strat = resolveStratification(underlyingScore);
//
//		if (rank)
//			addRankValue(row, elem);
//		else
//			addScoreValue(row, elem, strat);
//
//		if (valueMode != EValueMode.VALUE) {
//			Group group = resolveGroup(underlyingScore);
//			row.add(colSpacing);
//			if (strat != null) {
//				row.add(createColor(strat.getDataDomain().getColor(), DATADOMAIN_TYPE_WIDTH));
//			} else
//				row.add(createXSpacer(DATADOMAIN_TYPE_WIDTH));
//			row.add(colSpacing);
//
//			switch (this.valueMode) {
//			case VALUE_GROUP:
//				row.add(createLabel(group, -1));
//				break;
//			case VALUE_STRAT:
//				row.add(createLabel(strat, -1));
//				break;
//			case VALUE_STRAT_GROUP:
//				row.add(createLabel(strat, stratWidth));
//				row.add(colSpacing);
//				row.add(createLabel(group, -1));
//				break;
//			default:
//				break;
//			}
//		}
//
//		return row;
//	}

	@Override
	public String toString() {
		return score.getDescription();
	}

}
