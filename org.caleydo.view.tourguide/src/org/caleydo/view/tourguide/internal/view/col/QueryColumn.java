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

import static org.caleydo.core.view.opengl.layout.ElementLayouts.createColor;
import static org.caleydo.core.view.opengl.layout.ElementLayouts.createXSpacer;
import static org.caleydo.core.view.opengl.layout.ElementLayouts.wrap;
import static org.caleydo.view.tourguide.internal.TourGuideRenderStyle.COLX_SCORE_WIDTH;
import static org.caleydo.view.tourguide.internal.TourGuideRenderStyle.COL_SPACING;
import static org.caleydo.view.tourguide.internal.TourGuideRenderStyle.DATADOMAIN_TYPE_WIDTH;

import java.util.List;

import org.caleydo.core.data.perspective.variable.ARecordPerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.util.base.ConstantLabelProvider;
import org.caleydo.core.util.format.Formatter;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.Dims;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.Row.HAlign;
import org.caleydo.core.view.opengl.layout.util.PickingRenderer;
import org.caleydo.core.view.opengl.layout.util.TextureRenderer;
import org.caleydo.view.tourguide.api.query.ESorting;
import org.caleydo.view.tourguide.api.query.ScoreQuery;
import org.caleydo.view.tourguide.api.query.ScoringElement;
import org.caleydo.view.tourguide.api.score.CollapseScore;
import org.caleydo.view.tourguide.api.score.EScoreType;
import org.caleydo.view.tourguide.internal.TourGuideRenderStyle;
import org.caleydo.view.tourguide.internal.renderer.DecorationTextureRenderer;
import org.caleydo.view.tourguide.internal.renderer.ScoreBarRenderer;
import org.caleydo.view.tourguide.spi.compute.ICompositeScore;
import org.caleydo.view.tourguide.spi.score.IGroupScore;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.caleydo.view.tourguide.spi.score.IStratificationScore;

/**
 * base class for different score column renderer implementations depending on the type
 *
 * @author Samuel Gratzl
 *
 */
public class QueryColumn extends ATableColumn {
	public static final String SORT_COLUMN = "SORT_COLUMN";

	protected final boolean rank;
	protected final IScore score;
	private final int index;

	private ESorting sort = ESorting.NONE;

	private enum EValueMode {
		VALUE, VALUE_GROUP, VALUE_STRAT, VALUE_STRAT_GROUP;
	}

	private enum EHeaderMode {
		LABEL, STRAT, STRAT_GROUP
	}

	private final EHeaderMode headerMode;
	private final EValueMode valueMode;

	private final int stratWidth;

	private QueryColumn(AGLView view, final IScore scoreID, int i, ESorting sorting, boolean rank,
			EHeaderMode headerMode, EValueMode valueMode) {
		super(view);
		this.score = scoreID;
		this.rank = rank;
		this.sort = sorting;
		this.index = i;
		this.headerMode = headerMode;
		this.valueMode = valueMode;

		this.stratWidth = computeWidths();

		this.init();
	}

	private int computeWidths() {
		int header1Width = getTextWidth(score.getAbbreviation()) + 16;
		if (headerMode == EHeaderMode.STRAT || headerMode == EHeaderMode.STRAT_GROUP) {
			header1Width += DATADOMAIN_TYPE_WIDTH + COL_SPACING;
		}
		header1Width += getTextWidth(score);

		int stratWidth = 0;
		int valueWidth = COLX_SCORE_WIDTH;

		if (valueMode == EValueMode.VALUE_GROUP || valueMode == EValueMode.VALUE_STRAT_GROUP) {
			valueWidth += COL_SPACING;
			int groupWidth = getTextWidth("Group") - (valueMode == EValueMode.VALUE_GROUP ? DATADOMAIN_TYPE_WIDTH : 0);
			assert this.score instanceof ICompositeScore;
			for(IScore s : ((ICompositeScore)this.score))
				groupWidth = Math.max(groupWidth, getTextWidth(resolveGroup(s)));
			valueWidth += groupWidth;
		}
		if (valueMode == EValueMode.VALUE_STRAT || valueMode == EValueMode.VALUE_STRAT_GROUP) {
			valueWidth += COL_SPACING;
			stratWidth = getTextWidth("Stratification") - DATADOMAIN_TYPE_WIDTH;
			assert this.score instanceof ICompositeScore;
			for (IScore s : ((ICompositeScore) this.score)) {
				final ARecordPerspective st = resolveStratification(s);
				if (st == null)
					continue;
				stratWidth = Math.max(stratWidth, getTextWidth(st));
			}
			valueWidth += stratWidth;
		}
		if (valueMode != EValueMode.VALUE)
			valueWidth += DATADOMAIN_TYPE_WIDTH + COL_SPACING;

		setPixelSizeX(Math.max(header1Width, valueWidth));
		return stratWidth;
	}

	@Override
	protected ElementLayout createHeader() {
		Row r = new Row();
		r.addBackgroundRenderer(new DecorationTextureRenderer(null, view.getTextureManager(), Dims.xpixel(10), Dims
				.ypixel(10), HAlign.TOP,
				VAlign.LEFT));

		r.add(createLabel(score.getAbbreviation(), getTextWidth(score.getAbbreviation())));
		ARecordPerspective strat = resolveStratification(score);

		if (headerMode == EHeaderMode.STRAT || headerMode == EHeaderMode.STRAT_GROUP) {
			r.add(createColor(strat.getDataDomain().getColor(), DATADOMAIN_TYPE_WIDTH));
			r.add(colSpacing);
		}
		r.add(createLabel(this.score, -1));
		r.add(wrap(new TextureRenderer(sort.getFileName(), view.getTextureManager()), 16));
		r.addBackgroundRenderer(new PickingRenderer(SORT_COLUMN, index, view));
		return r;
	}

	@Override
	protected ElementLayout createHeader2() {
		Row r = new Row();
		r.setGrabY(true);
		r.setXDynamic(true);
		r.add(createLabel(rank ? "Value" : "Score", COLX_SCORE_WIDTH));
		switch (this.valueMode) {
		case VALUE:
			break;
		case VALUE_GROUP:
			r.add(colSpacing);
			r.add(createLabel("Group", -1));
			break;
		case VALUE_STRAT:
			r.add(colSpacing);
			r.add(createLabel("Stratification", -1));
			break;
		case VALUE_STRAT_GROUP:
			r.add(colSpacing);
			r.add(createLabel("Stratification", stratWidth + DATADOMAIN_TYPE_WIDTH + COL_SPACING));
			r.add(colSpacing);
			r.add(createLabel("Group", -1));
			break;
		}
		return r;
	}

	public void setHasFilter(boolean hasFilter) {
		DecorationTextureRenderer m = (DecorationTextureRenderer) th.getBackgroundRenderer().get(0);
		if (hasFilter)
			m.setImagePath(TourGuideRenderStyle.ICON_FILTER);
		else
			m.setImagePath(null);
	}

	private ElementLayout createValue(ScoringElement elem, int id) {
		Row row = new Row();
		row.setGrabY(true);
		row.setXDynamic(false);
		IScore underlyingScore = this.score;
		if (underlyingScore instanceof CollapseScore)
			underlyingScore = elem.getSelected((CollapseScore) underlyingScore);
		ARecordPerspective strat = resolveStratification(underlyingScore);

		if (rank)
			addRankValue(row, elem);
		else
			addScoreValue(row, elem, strat);

		if (valueMode != EValueMode.VALUE) {
			Group group = resolveGroup(underlyingScore);
			row.add(colSpacing);
			if (strat != null) {
				row.add(createColor(strat.getDataDomain().getColor(), DATADOMAIN_TYPE_WIDTH));
			} else
				row.add(createXSpacer(DATADOMAIN_TYPE_WIDTH));
			row.add(colSpacing);

			switch (this.valueMode) {
			case VALUE_GROUP:
				row.add(createLabel(group, -1));
				break;
			case VALUE_STRAT:
				row.add(createLabel(strat, -1));
				break;
			case VALUE_STRAT_GROUP:
				row.add(createLabel(strat, stratWidth));
				row.add(colSpacing);
				row.add(createLabel(group, -1));
				break;
			default:
				break;
			}
		}

		return row;
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
		Row h = (Row) this.th;
		h.get(h.getElements().size() - 1).setRenderer(
				new TextureRenderer(this.sort.getFileName(), view.getTextureManager()));
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

	private final void addScoreValue(Row row, ScoringElement elem, ARecordPerspective strat) {
		// render the real value
		float value = this.score.getScore(elem);
		if (!Float.isNaN(value)) {
			ElementLayout valueEL = createLabel(new ConstantLabelProvider(Formatter.formatNumber(value)),
					COLX_SCORE_WIDTH);
			valueEL.setGrabY(true);
			valueEL.addBackgroundRenderer(new ScoreBarRenderer(value, strat != null ? strat.getDataDomain().getColor()
					: null));
			row.add(valueEL);
		} else {
			row.add(createXSpacer(COLX_SCORE_WIDTH));
		}
	}

	private final void addRankValue(Row row, ScoringElement elem) {
		// render the real value
		float value = this.score.getScore(elem);
		if (!Float.isNaN(value)) {
			ElementLayout valueEL = createRightLabel(new ConstantLabelProvider(Formatter.formatNumber(value)),
					COLX_SCORE_WIDTH);
			valueEL.setGrabY(true);
			row.add(valueEL);
		} else {
			row.add(createXSpacer(COLX_SCORE_WIDTH));
		}
	}

	private static ARecordPerspective resolveStratification(IScore score) {
		if (score instanceof IStratificationScore)
			return ((IStratificationScore) score).getStratification();
		return null;
	}

	private static Group resolveGroup(IScore score) {
		if (score instanceof IGroupScore)
			return ((IGroupScore) score).getGroup();
		return null;
	}

	public static QueryColumn create(IScore score, int i, ESorting sorting, AGLView view) {
		EValueMode valueMode = EValueMode.VALUE;
		EHeaderMode headerMode = EHeaderMode.LABEL;
		EScoreType scoreType = score.getScoreType();
		if (score instanceof IGroupScore) {// have a group and a common stratification
			valueMode = EValueMode.VALUE;
			headerMode = EHeaderMode.STRAT_GROUP;
		} else if (score instanceof IStratificationScore) {
			boolean hasStrat = ((IStratificationScore) score).getStratification() != null;
			if (hasStrat)
				headerMode = EHeaderMode.STRAT;
			if (scoreType.needsGroup())
				valueMode = hasStrat ? EValueMode.VALUE_GROUP : EValueMode.VALUE_STRAT_GROUP;
			else if (scoreType == EScoreType.STRATIFICATION_RANK || scoreType == EScoreType.STRATIFICATION_SCORE)
				valueMode = hasStrat ? EValueMode.VALUE : EValueMode.VALUE_STRAT;
		}

		return new QueryColumn(view, score, i, sorting, scoreType.isRank(), headerMode, valueMode);
	}
}
