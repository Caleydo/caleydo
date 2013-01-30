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
package org.caleydo.view.tourguide.api.query;

import java.util.AbstractList;
import java.util.List;
import java.util.RandomAccess;

import org.caleydo.view.tourguide.api.util.SimpleStatistics;
import org.caleydo.view.tourguide.spi.score.IScore;

/**
 * @author Samuel Gratzl
 *
 */
public class RankedList extends AbstractList<ScoringElement> implements RandomAccess {
	private final List<IScore> scores;
	private final List<SimpleStatistics> scoreStatistics;
	private final List<ScoringElement> values;

	public RankedList(List<IScore> scores, List<SimpleStatistics> scoreStatistics, List<ScoringElement> values) {
		this.scores = scores;
		this.scoreStatistics = scoreStatistics;
		this.values = values;
	}

	public int columnSize() {
		return scores.size();
	}

	public SimpleStatistics getColumnStatistics(int index) {
		return scoreStatistics.get(index);
	}

	public IScore getColumn(int index) {
		return scores.get(index);
	}

	@Override
	public ScoringElement get(int index) {
		return values.get(index);
	}

	@Override
	public int size() {
		return values.size();
	}

}
