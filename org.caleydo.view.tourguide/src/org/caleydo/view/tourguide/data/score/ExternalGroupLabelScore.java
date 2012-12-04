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
package org.caleydo.view.tourguide.data.score;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.view.tourguide.data.ScoringElement;
import org.caleydo.view.tourguide.data.serialize.ISerializeableScore;

/**
 * external score which contains a score per group of a stratification, identified by its label
 *
 * @author Samuel Gratzl
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class ExternalGroupLabelScore implements ISerializeableScore {

	private String label;
	private String perspectiveKey;
	private boolean isRank;
	private Map<String, Float> scores = new HashMap<>();

	public ExternalGroupLabelScore() {

	}

	public ExternalGroupLabelScore(String label, String perspectiveKey, boolean isRank,
			Map<String, Float> scores) {
		this.perspectiveKey = perspectiveKey;
		this.label = label;
		this.isRank = isRank;
		this.scores.putAll(scores);
	}

	@Override
	public final EScoreType getScoreType() {
		return isRank ? EScoreType.GROUP_RANK : EScoreType.GROUP_SCORE;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public String getProviderName() {
		return "External";
	}

	@Override
	public float getScore(ScoringElement elem) {
		TablePerspective strat = elem.getStratification();
		if (!perspectiveKey.equals(strat.getDimensionPerspective().getPerspectiveID())
				&& !perspectiveKey.equals(strat.getRecordPerspective().getPerspectiveID()))
			return Float.NaN;

		if (elem.getGroup() == null)
			return Float.NaN;

		Float v = this.scores.get(elem.getGroup().getLabel());
		if (v == null)
			return Float.NaN;
		return v.floatValue();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExternalGroupLabelScore other = (ExternalGroupLabelScore) obj;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		return true;
	}
}
