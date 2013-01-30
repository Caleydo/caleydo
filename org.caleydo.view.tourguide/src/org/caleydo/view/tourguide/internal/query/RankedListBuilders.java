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
package org.caleydo.view.tourguide.internal.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

import org.caleydo.view.tourguide.api.query.RankedList;
import org.caleydo.view.tourguide.api.query.ScoringElement;
import org.caleydo.view.tourguide.api.util.SimpleStatistics;
import org.caleydo.view.tourguide.spi.query.filter.IScoreFilter;
import org.caleydo.view.tourguide.spi.score.IScore;
/**
 * utility factory class which wraps the algorithm to create the final sorted list with a fixed size
 *
 * There are two strategies depending on the total amount of elements:
 *
 * a: collect in a big array and to a collections sort afterwards
 *
 * b: collect incrementally using insort techniques
 *
 * @author Samuel Gratzl
 *
 */

public class RankedListBuilders {
	public interface IRankedListBuilder {
		public void add(ScoringElement e);

		public RankedList build();
	}

	public static IRankedListBuilder create(int maxSize, int numberOfElements, IScoreFilter filter,
			Comparator<ScoringElement> comparator, List<IScore> columns) {
		if (maxSize * 5 < numberOfElements) // TODO better strategy
			return new InsertionRankedListBuilder(maxSize, comparator, filter, columns);
		else
			return new QuickSortRankedListBuilder(numberOfElements, maxSize, comparator, filter, columns);
	}

	private static abstract class ARankedListBuilder implements IRankedListBuilder {
		private final List<IScore> columns;
		private final List<SimpleStatistics.Builder> stats;
		private final IScoreFilter filter;

		public ARankedListBuilder(IScoreFilter filter, List<IScore> columns) {
			this.filter = filter;
			this.columns = new ArrayList<>(columns);
			this.stats = new ArrayList<>(columns.size());
			for(int i = 0; i < columns.size(); ++i)
				stats.add(new SimpleStatistics.Builder());
		}

		protected boolean preAdd(ScoringElement e) {
			if (!filter.apply(e))
				return false;
			for(int i = 0; i < columns.size(); ++i)
				stats.get(i).add(columns.get(i).getScore(e));
			return true;
		}

		protected abstract List<ScoringElement> buildValues();

		@Override
		public RankedList build() {
			List<SimpleStatistics> s = new ArrayList<>(columns.size());
			for(SimpleStatistics.Builder b : stats)
				s.add(b.build());
			return new RankedList(columns, s, buildValues());
		}
	}

	private static class InsertionRankedListBuilder extends ARankedListBuilder {
		private final NavigableSet<ScoringElement> list;
		private final int maxSize;

		public InsertionRankedListBuilder(int maxSize, Comparator<ScoringElement> comparator, IScoreFilter filter, List<IScore> columns) {
			super(filter, columns);
			this.maxSize = maxSize;
			this.list = new TreeSet<>(comparator);
		}

		@Override
		public void add(ScoringElement e) {
			if (!preAdd(e))
				return;
			list.add(e);
			if (list.size() > maxSize)
				list.pollLast();
		}

		@Override
		protected List<ScoringElement> buildValues() {
			return new ArrayList<>(list);
		}
	}

	private static class QuickSortRankedListBuilder extends ARankedListBuilder {
		private final List<ScoringElement> list;
		private final int maxSize;
		private final Comparator<ScoringElement> comparator;

		public QuickSortRankedListBuilder(int numberOfElements, int maxSize, Comparator<ScoringElement> comparator,
				IScoreFilter filter, List<IScore> columns) {
			super(filter, columns);
			this.maxSize = maxSize;
			this.list = new ArrayList<>(numberOfElements);
			this.comparator = comparator;
		}

		@Override
		public void add(ScoringElement e) {
			if (!preAdd(e))
				return;
			list.add(e);
		}

		@Override
		protected List<ScoringElement> buildValues() {
			Collections.sort(list, comparator);
			return new ArrayList<>(list.subList(0, Math.min(maxSize, list.size())));
		}
	}
}
