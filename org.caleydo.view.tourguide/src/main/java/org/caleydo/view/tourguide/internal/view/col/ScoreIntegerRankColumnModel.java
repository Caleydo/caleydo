/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.view.col;

import java.text.NumberFormat;
import java.util.Locale;

import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.view.tourguide.api.model.MaxGroupCombiner;
import org.caleydo.view.tourguide.internal.model.IntCombinerAdapter;
import org.caleydo.view.tourguide.spi.score.IGroupScore;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.caleydo.view.tourguide.spi.score.IStratificationScore;
import org.caleydo.vis.lineup.model.IRow;
import org.caleydo.vis.lineup.model.IntegerRankColumnModel;
import org.caleydo.vis.lineup.model.mixin.IMappedColumnMixin;

/**
 * @author Samuel Gratzl
 *
 */
public class ScoreIntegerRankColumnModel extends IntegerRankColumnModel implements IGLRenderer, IScoreMixin {
	private final IScore score;
	private EHeaderMode headerMode;

	private enum EHeaderMode {
		LABEL, STRAT, STRAT_GROUP
	}

	public ScoreIntegerRankColumnModel(IScore score) {
		super(null, new IntCombinerAdapter(new MaxGroupCombiner(score)), score.getColor(), score.getBGColor(),
				NumberFormat.getInstance(Locale.ENGLISH));
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

	public ScoreIntegerRankColumnModel(ScoreIntegerRankColumnModel copy) {
		super(copy);
		this.score = copy.score;
		this.headerMode = copy.headerMode;
		setHeaderRenderer(this);
	}

	@Override
	public ScoreIntegerRankColumnModel clone() {
		return new ScoreIntegerRankColumnModel(this);
	}

	/**
	 * dirty the mapping to indicate the values might have changed and to clear caches
	 */
	@Override
	public void dirty() {
		propertySupport.firePropertyChange(IMappedColumnMixin.PROP_MAPPING, null, score);
	}

	@Override
	public int compare(IRow o1, IRow o2) {
		return -super.compare(o1, o2);
	}

	/**
	 * @return the score, see {@link #score}
	 */
	@Override
	public IScore getScore() {
		return score;
	}
	@Override
	public void render(GLGraphics g, float w, float h, GLElement parent) {
		Perspective strat = resolveStratification(score);
		if (this.isCollapsed()) {
			if (headerMode == EHeaderMode.STRAT || headerMode == EHeaderMode.STRAT_GROUP)
				g.color(strat.getDataDomain().getColor()).fillRect((w - 10) * 0.5f, (h - 10) * 0.5f, 10, 10);
		} else {
			g.drawText(this.score.getLabel(), 1, 1, w - 1, 12, VAlign.CENTER);
		}
	}

	private static Perspective resolveStratification(IScore score) {
		if (score instanceof IStratificationScore)
			return ((IStratificationScore) score).getStratification();
		return null;
	}

	@Override
	public String toString() {
		return score.getDescription();
	}

}
