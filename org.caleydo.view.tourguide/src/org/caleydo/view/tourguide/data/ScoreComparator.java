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
package org.caleydo.view.tourguide.data;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.caleydo.view.tourguide.data.score.IScore;

/**
 * @author Samuel Gratzl
 *
 */
public class ScoreComparator extends LinkedHashMap<IScore, ESorting> implements Comparator<ScoringElement> {
	private static final long serialVersionUID = 4297583469444630189L;

	public ScoreComparator() {
	}

	public ScoreComparator(ScoreComparator other) {
		super(other);
	}

	@Override
	public int compare(ScoringElement o1, ScoringElement o2) {
		for (Map.Entry<IScore, ESorting> entry : entrySet()) {
			IScore s = entry.getKey();
			int c = entry.getValue().apply(compare(s.getScore(o1), s.getScore(o2)));
			if (c != 0)
				return c;
		}
		int c = o1.getDataDomain().getLabel().compareTo(o2.getDataDomain().getLabel());
		if (c != 0)
			return c;
		return o1.getLabel().compareTo(o2.getLabel());
	}

	private int compare(float s1, float s2) {
		boolean n1 = Float.isNaN(s1);
		boolean n2 = Float.isNaN(s2);
		if (n1 ^ n2)
			return n1 ? -1: 1;
		return Float.compare(s1, s2);
	}
}
