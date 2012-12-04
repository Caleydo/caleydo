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
package org.caleydo.view.tourguide.vendingmachine;

import static org.caleydo.core.view.opengl.layout.ElementLayouts.createXSpacer;
import static org.caleydo.core.view.opengl.layout.ElementLayouts.wrap;
import static org.caleydo.view.tourguide.renderstyle.TourGuideRenderStyle.COL_SPACING;
import static org.caleydo.view.tourguide.renderstyle.TourGuideRenderStyle.LABEL_PADDING;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.util.base.ConstantLabelProvider;
import org.caleydo.core.util.base.ILabelProvider;
import org.caleydo.core.util.format.Formatter;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.PickingRenderer;
import org.caleydo.core.view.opengl.layout.util.Renderers;
import org.caleydo.core.view.opengl.layout.util.TextureRenderer;
import org.caleydo.view.tourguide.data.ESorting;
import org.caleydo.view.tourguide.data.ScoringElement;
import org.caleydo.view.tourguide.data.score.AGroupScore;
import org.caleydo.view.tourguide.data.score.AStratificationScore;
import org.caleydo.view.tourguide.data.score.IScore;
import org.caleydo.view.tourguide.renderer.ScoreBarRenderer;
import org.caleydo.view.tourguide.renderstyle.TourGuideRenderStyle;

/**
 * base class for different score column renderer implementations depending on the type
 *
 * @author Samuel Gratzl
 *
 */
public abstract class AScoreColumn extends Row {
	public static final String SORT_COLUMN = "SORT_COLUMN";
	public static final String SELECT_ROW_COLUMN = "SELECT_ROW_COLUMN";

	protected final ElementLayout colSpacing = createXSpacer(COL_SPACING);

	private ESorting sort = ESorting.NONE;
	protected final IScore score;
	protected final AGLView view;

	protected AScoreColumn(final IScore scoreID, int i, ESorting sorting, AGLView view) {
		this.score = scoreID;
		this.sort = sorting;
		this.view = view;
		ElementLayout label = createLabel(scoreID, -1);
		label.setGrabY(true);
		add(label);
		add(wrap(new TextureRenderer(sort.getFileName(), view.getTextureManager()), 16));
		addBackgroundRenderer(new PickingRenderer(SORT_COLUMN, i, view));
	}

	protected final ElementLayout createLabel(ILabelProvider label, int width) {
		return wrap(Renderers.createLabel(label, view.getTextRenderer()).padding(LABEL_PADDING).build(), width);
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
		get(1).setRenderer(new TextureRenderer(this.sort.getFileName(), view.getTextureManager()));
	}

	public final ESorting nextSorting() {
		setSort(this.sort.next());
		return this.sort;
	}

	public final ElementLayout createValue(ScoringElement elem, int id) {
		Row row = new Row();
		row.setGrabY(true);
		row.setXDynamic(true);

		addScoreSpecific(row, elem);
		row.addBackgroundRenderer(new PickingRenderer(SELECT_ROW_COLUMN, id, this.view));
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
			ElementLayout valueEL = createLabel(new ConstantLabelProvider(Formatter.formatNumber(value)),
					TourGuideRenderStyle.COLX_SCORE_WIDTH);
			valueEL.setGrabY(true);
			row.add(valueEL);
		} else {
			row.add(createXSpacer(TourGuideRenderStyle.COLX_SCORE_WIDTH));
		}
	}

	protected static TablePerspective resolveStratification(IScore score) {
		if (score instanceof AStratificationScore)
			return ((AStratificationScore) score).getReference();
		if (score instanceof AGroupScore)
			return ((AGroupScore) score).getStratification();
		return null;
	}

	protected static Group resolveGroup(IScore score) {
		if (score instanceof AGroupScore)
			return ((AGroupScore) score).getGroup();
		return null;
	}
}
