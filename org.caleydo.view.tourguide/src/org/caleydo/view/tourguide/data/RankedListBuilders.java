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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

import org.caleydo.view.tourguide.data.filter.IScoreFilter;


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

		public List<ScoringElement> build();
	}

	public static IRankedListBuilder create(int maxSize, int numberOfElements, IScoreFilter filter,
			Comparator<ScoringElement> comparator) {
		if (maxSize * 5 < numberOfElements)
			return new InsertionRankedListBuilder(maxSize, comparator, filter);
		else
			return new QuickSortRankedListBuilder(numberOfElements, maxSize, comparator, filter);
	}

	private static class InsertionRankedListBuilder implements IRankedListBuilder {
		private final NavigableSet<ScoringElement> list;
		private final int maxSize;
		private final IScoreFilter filter;

		public InsertionRankedListBuilder(int maxSize, Comparator<ScoringElement> comparator, IScoreFilter filter) {
			this.maxSize = maxSize;
			this.list = new TreeSet<>(comparator);
			this.filter = filter;
		}

		@Override
		public void add(ScoringElement e) {
			if (!filter.apply(e))
				return;
			list.add(e);
			if (list.size() > maxSize)
				list.pollLast();
		}

		@Override
		public List<ScoringElement> build() {
			return new ArrayList<>(list);
		}
	}

	private static class QuickSortRankedListBuilder implements IRankedListBuilder {
		private final List<ScoringElement> list;
		private final int maxSize;
		private final Comparator<ScoringElement> comparator;
		private final IScoreFilter filter;

		public QuickSortRankedListBuilder(int numberOfElements, int maxSize, Comparator<ScoringElement> comparator,
				IScoreFilter filter) {
			this.maxSize = maxSize;
			this.list = new ArrayList<>(numberOfElements);
			this.comparator = comparator;
			this.filter = filter;
		}

		@Override
		public void add(ScoringElement e) {
			if (!filter.apply(e))
				return;
			list.add(e);
		}

		@Override
		public List<ScoringElement> build() {
			Collections.sort(list, comparator);
			return new ArrayList<>(list.subList(0, Math.min(maxSize, list.size())));
		}
	}
}
