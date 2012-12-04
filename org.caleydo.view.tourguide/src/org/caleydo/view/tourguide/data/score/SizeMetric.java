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

import org.caleydo.view.tourguide.data.ScoringElement;

/**
 * simple metric returning the number of records i.e. size of the stratification / cluster
 *
 * @author Samuel Gratzl
 *
 */
public class SizeMetric implements IScore {
	@Override
	public String getLabel() {
		return "Size";
	}

	@Override
	public String getProviderName() {
		return null;
	}

	@Override
	public EScoreType getScoreType() {
		return EScoreType.RANK;
	}

	@Override
	public float getScore(ScoringElement elem) {
		if (elem.getGroup() == null) {
			return elem.getStratification().getNrRecords();
		} else
			return elem.getGroup().getSize();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof SizeMetric;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
