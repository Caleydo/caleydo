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

import static org.caleydo.core.view.opengl.layout.ElementLayouts.createXSpacer;
import static org.caleydo.core.view.opengl.layout.ElementLayouts.wrap;

import java.util.List;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.util.base.ConstantLabelProvider;
import org.caleydo.core.util.format.Formatter;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.PickingRenderer;
import org.caleydo.core.view.opengl.layout.util.TextureRenderer;
import org.caleydo.view.tourguide.data.ESorting;
import org.caleydo.view.tourguide.data.ScoreQuery;
import org.caleydo.view.tourguide.data.ScoringElement;
import org.caleydo.view.tourguide.data.score.IGroupScore;
import org.caleydo.view.tourguide.data.score.IScore;
import org.caleydo.view.tourguide.data.score.IStratificationScore;
import org.caleydo.view.tourguide.renderer.ScoreBarRenderer;
import org.caleydo.view.tourguide.renderstyle.TourGuideRenderStyle;

/**
 * base class for different score column renderer implementations depending on the type
 *
 * @author Samuel Gratzl
 *
 */
public abstract class AQueryColumn extends ATableColumn {
	public static final String SORT_COLUMN = "SORT_COLUMN";

	protected final IScore score;
	private final int index;


	private ESorting sort = ESorting.NONE;

	protected AQueryColumn(AGLView view, final IScore scoreID, int i, ESorting sorting) {
		super(view);
		this.score = scoreID;
		this.sort = sorting;
		this.index = i;
		this.init();
	}

	@Override
	protected ElementLayout createHeader() {
		Row r = new Row();
		ElementLayout label = createLabel(this.score, -1);
		label.setGrabY(true);
		r.add(label);
		r.add(wrap(new TextureRenderer(sort.getFileName(), view.getTextureManager()), 16));
		r.addBackgroundRenderer(new PickingRenderer(SORT_COLUMN, index, view));
		return r;
	}


	/**
	 * @return the score, see {@link #score}
	 */
	public final IScore getScore() {
		return score;
	}

	public final void setSort(ESorting sort) {
		if (this.sort == sort)
			return;
		this.sort = sort;
		((Row) this.th).get(1).setRenderer(new TextureRenderer(this.sort.getFileName(), view.getTextureManager()));
	}

	public final ESorting nextSorting() {
		setSort(this.sort.next());
		return this.sort;
	}

	@Override
	public void setData(List<ScoringElement> data, ScoreQuery query) {
		this.clearBody();
		for (int i = 0; i < data.size(); ++i) {
			this.addTd(createValue(data.get(i), i), i);
		}
	}

	public ElementLayout createValue(ScoringElement elem, int id) {
		Row row = new Row();
		row.setGrabY(true);
		row.setXDynamic(true);

		addScoreSpecific(row, elem);
		return row;
	}

	protected abstract void addScoreSpecific(Row row, ScoringElement elem);

	protected final void addScoreValue(Row row, ScoringElement elem, TablePerspective strat) {
		// render the real value
		float value = this.score.getScore(elem);
		if (!Float.isNaN(value)) {
			ElementLayout valueEL = createLabel(new ConstantLabelProvider(Formatter.formatNumber(value)),
					TourGuideRenderStyle.COLX_SCORE_WIDTH);
			valueEL.setGrabY(true);
			valueEL.addBackgroundRenderer(new ScoreBarRenderer(value, strat != null ? strat.getDataDomain().getColor()
					: null));
			row.add(valueEL);
		} else {
			row.add(createXSpacer(TourGuideRenderStyle.COLX_SCORE_WIDTH));
		}
	}

	protected final void addRankValue(Row row, ScoringElement elem) {
		// render the real value
		float value = this.score.getScore(elem);
		if (!Float.isNaN(value)) {
			ElementLayout valueEL = createRightLabel(new ConstantLabelProvider(Formatter.formatNumber(value)),
					TourGuideRenderStyle.COLX_SCORE_WIDTH);
			valueEL.setGrabY(true);
			row.add(valueEL);
		} else {
			row.add(createXSpacer(TourGuideRenderStyle.COLX_SCORE_WIDTH));
		}
	}

	protected static TablePerspective resolveStratification(IScore score) {
		if (score instanceof IStratificationScore)
			return ((IStratificationScore) score).getStratification();
		return null;
	}

	protected static Group resolveGroup(IScore score) {
		if (score instanceof IGroupScore)
			return ((IGroupScore) score).getGroup();
		return null;
	}
}
