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
package org.caleydo.view.tourguide.api.score;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.caleydo.core.data.perspective.variable.ARecordPerspective;
import org.caleydo.core.util.base.DefaultLabelProvider;
import org.caleydo.view.tourguide.api.query.ESorting;
import org.caleydo.view.tourguide.api.query.ScoringElement;
import org.caleydo.view.tourguide.spi.score.IScore;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class AComputedStratificationScore extends DefaultLabelProvider implements IScore {
	protected final Map<String, Float> scores = new ConcurrentHashMap<>();

	public AComputedStratificationScore(String label) {
		super(label);
	}

	@Override
	public ESorting getDefaultSorting() {
		return ESorting.DESC;
	}

	public boolean contains(ARecordPerspective elem) {
		// have in cache or the same
		return scores.containsKey(elem.getPerspectiveID());
	}

	public void put(ARecordPerspective elem, float value) {
		scores.put(elem.getPerspectiveID(), value);
	}

	@Override
	public float getScore(ScoringElement elem) {
		ARecordPerspective p = elem.getStratification();
		Float f = scores.get(p.getPerspectiveID());
		return f == null ? Float.NaN : f.floatValue();
	}

	@Override
	public final EScoreType getScoreType() {
		return EScoreType.STRATIFICATION_SCORE;
	}
}