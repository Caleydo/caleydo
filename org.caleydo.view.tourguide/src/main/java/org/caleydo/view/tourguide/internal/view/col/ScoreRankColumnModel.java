/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.view.col;

import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.view.tourguide.api.model.MaxGroupCombiner;
import org.caleydo.view.tourguide.spi.score.IGroupScore;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.caleydo.view.tourguide.spi.score.IStratificationScore;
import org.caleydo.vis.lineup.data.DoubleInferrers;
import org.caleydo.vis.lineup.model.DoubleRankColumnModel;

/**
 * @author Samuel Gratzl
 *
 */
public class ScoreRankColumnModel extends DoubleRankColumnModel implements IGLRenderer, IScoreMixin {
	private final IScore score;
	private EHeaderMode headerMode;

	private enum EHeaderMode {
		LABEL, STRAT, STRAT_GROUP
	}

	public ScoreRankColumnModel(IScore score) {
		super(new MaxGroupCombiner(score), null, score.getColor(), score.getBGColor(), score.createMapping(),
				DoubleInferrers.fix(Float.NaN));
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
		setFilter(true, false, false, false);
	}

	public ScoreRankColumnModel(ScoreRankColumnModel copy) {
		super(copy);
		this.score = copy.score;
		this.headerMode = copy.headerMode;
		setHeaderRenderer(this);
	}

	@Override
	public ScoreRankColumnModel clone() {
		return new ScoreRankColumnModel(this);
	}

	/**
	 * dirty the mapping to indicate the values might have changed and to clear caches
	 */
	@Override
	public void dirty() {
		propertySupport.firePropertyChange(PROP_MAPPING, null, data);
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
			g.drawText(this.score.getLabel(), 1, 1, w - 1, h - 1, VAlign.CENTER);
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
