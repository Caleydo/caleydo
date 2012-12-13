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

import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.execution.SafeCallable;
import org.caleydo.view.tourguide.data.RankedListBuilders.IRankedListBuilder;
import org.caleydo.view.tourguide.data.compute.ComputeBatchGroupScore;
import org.caleydo.view.tourguide.data.compute.ComputeGroupScore;
import org.caleydo.view.tourguide.data.compute.ComputeStratificationScore;
import org.caleydo.view.tourguide.data.compute.ScoreComputer;
import org.caleydo.view.tourguide.data.filter.CompositeScoreFilter;
import org.caleydo.view.tourguide.data.filter.IDataDomainFilter;
import org.caleydo.view.tourguide.data.filter.IScoreFilter;
import org.caleydo.view.tourguide.data.score.CollapseScore;
import org.caleydo.view.tourguide.data.score.IBatchComputedGroupScore;
import org.caleydo.view.tourguide.data.score.IComputedGroupScore;
import org.caleydo.view.tourguide.data.score.IComputedStratificationScore;
import org.caleydo.view.tourguide.data.score.IScore;

import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

/**
 * container for all the score query elements + a way how to compute the scored scoring elements out of that query
 *
 * @author Samuel Gratzl
 *
 */
public class ScoreQuery implements SafeCallable<List<ScoringElement>> {
	public static final String PROP_ORDER_BY = "orderBy";
	public static final String PROP_SELECTION = "selection";
	public static final String PROP_TOP = "top";
	public static final String PROP_FILTER = "filter";

	// number of concurrent sortings
	private static final int MAX_SORTING = 1;

	private final PropertyChangeSupport listeners = new PropertyChangeSupport(this);

	/**
	 * the currently selected columns / scores
	 */
	private List<IScore> selection = new ArrayList<>();
	/**
	 * the currently applied filters to the scoring elements, e.g. min score
	 */
	private CompositeScoreFilter filter = new CompositeScoreFilter();
	/**
	 * the currentl order of the elements
	 */
	private ScoreComparator orderBy = new ScoreComparator();
	/**
	 * how many elements should be shown at max, the less the better memory behavior
	 */
	private int top = 35;

	private final DataDomainQuery query;

	/**
	 * queue holding the tasks we are submitted to computed but not yet finished
	 */
	private final Deque<Future<?>> toCompute = new LinkedList<>();

	public ScoreQuery(DataDomainQuery query) {
		this.query = query;
		this.query.addPropertyChangeListener(DataDomainQuery.PROP_SELECTION, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				IndexedPropertyChangeEvent ievt = (IndexedPropertyChangeEvent) evt;
				if (ievt.getOldValue() == null) { // new value
					onAddStratification((ATableBasedDataDomain) ievt.getNewValue());
				}
			}
		});
		this.query.addPropertyChangeListener(DataDomainQuery.PROP_FILTER, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				IndexedPropertyChangeEvent ievt = (IndexedPropertyChangeEvent) evt;
				if (ievt.getNewValue() == null) { // removed filter
					onRemovedFilter((IDataDomainFilter) ievt.getOldValue());
				}
			}
		});
	}

	/**
	 * @return the query, see {@link #query}
	 */
	public DataDomainQuery getQuery() {
		return query;
	}

	/**
	 * returns whether there are outstanding computations
	 *
	 * @return
	 */
	public boolean isBusy() {
		return getNextUndone() != null;
	}

	private synchronized Future<?> getNextUndone() {
		for (Iterator<Future<?>> it = toCompute.iterator(); it.hasNext();) {
			Future<?> f = it.next();
			if (f.isDone())
				it.remove();
			else
				return f;
		}
		return null;
	}

	/**
	 * blocks the current thread till all computations are done
	 */
	public void waitTillComplete() {
		Future<?> next;
		while ((next = getNextUndone()) != null) {
			try {
				next.get();
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * executes this query and returns the resulting scoring element list
	 */
	@Override
	public List<ScoringElement> call() {
		final Pair<List<CollapseScore>, Integer> pair = filterCollapseScore(Scores.flatten(selection));
		final List<CollapseScore> collapseScores = pair.getFirst();
		final int factor = pair.getSecond();

		Collection<TablePerspective> stratifications = query.call();

		Map<IScore, IScore> selections = new HashMap<IScore, IScore>(collapseScores.size());
		IRankedListBuilder builder;
		if (isGroupQuery()) { // we need to show strat,group pairs
			Multimap<TablePerspective, Group> stratNGroups = query.apply(stratifications);
			builder = RankedListBuilders.create(top, stratNGroups.size() * factor, filter, orderBy);
			buildAll2(builder, 0, collapseScores, selections, stratNGroups);
		} else { // just stratifications
			builder = RankedListBuilders.create(top, stratifications.size() * factor, filter, orderBy);
			buildAll(builder, 0, collapseScores, selections, stratifications);
		}
		return builder.build();
	}

	public boolean isGroupQuery() {
		return Iterables.any(Scores.flatten(selection), isGroupScore);
	}

	/**
	 * find all product scores and compute the multiplication factor of the rows
	 *
	 * @param scores
	 * @return
	 */
	private Pair<List<CollapseScore>, Integer> filterCollapseScore(Collection<IScore> scores) {
		int factor = 1;
		List<CollapseScore> result = new ArrayList<>(scores.size());
		for (IScore s : scores) {
			if (s instanceof CollapseScore) {
				CollapseScore p = (CollapseScore) s;
				factor *= p.size();
				result.add(p);
			}
		}
		return Pair.make(result, factor);
	}

	private static final Predicate<IScore> isGroupScore = new Predicate<IScore>() {
		@Override
		public boolean apply(IScore in) {
			return in.getScoreType().needsGroup();
		}
	};

	private static void buildAll(IRankedListBuilder builder, int i, List<CollapseScore> composites,
			Map<IScore, IScore> selections,
			Collection<TablePerspective> stratifications) {
		if (i == composites.size()) {
			HashMap<IScore, IScore> s = new HashMap<>(selections);
			// last one trigger
			for (TablePerspective elem : stratifications) {
				builder.add(new ScoringElement(elem, s));
			}
		} else {
			for (IScore child : composites.get(i)) {
				selections.put(composites.get(i), child); // set actual
				// trigger rest
				buildAll(builder, i + 1, composites, selections, stratifications);
			}
		}
	}

	private static void buildAll2(IRankedListBuilder builder, int i, List<CollapseScore> composites,
			Map<IScore, IScore> selections, Multimap<TablePerspective, Group> stratNGroups) {
		if (i == composites.size()) {
			// last one trigger
			HashMap<IScore, IScore> s = new HashMap<>(selections);
			for (Map.Entry<TablePerspective, Group> elem : stratNGroups.entries())
				builder.add(new ScoringElement(elem.getKey(), elem.getValue(), s));
		} else {
			for (IScore child : composites.get(i)) {
				selections.put(composites.get(i), child); // set actual
				// trigger rest
				buildAll2(builder, i + 1, composites, selections, stratNGroups);
			}
		}
	}

	/**
	 * @return the selection
	 */
	public List<IScore> getSelection() {
		return Collections.unmodifiableList(selection);
	}

	/**
	 * @return the filter, see {@link #filter}
	 */
	public Collection<IScoreFilter> getFilter() {
		return Collections.unmodifiableSet(filter);
	}

	public void setFilters(Collection<IScoreFilter> f) {
		this.filter.clear();
		this.filter.addAll(f);
		listeners.firePropertyChange(PROP_FILTER, null, this.filter);
	}

	public void addFilter(IScoreFilter filter) {
		if (this.filter.contains(filter))
			return;
		this.filter.add(filter);
		listeners.fireIndexedPropertyChange(PROP_FILTER, this.filter.size() - 1, null, filter);
	}

	public void updatedFilter(IScoreFilter filter) {
		if (!this.filter.contains(filter))
			return;
		listeners.fireIndexedPropertyChange(PROP_FILTER, -1, filter, filter);
	}

	/**
	 * @param listener
	 * @see java.beans.PropertyChangeSupport#addPropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(listener);
	}

	/**
	 * @param listener
	 * @see java.beans.PropertyChangeSupport#removePropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		listeners.removePropertyChangeListener(listener);
	}

	/**
	 * @param propertyName
	 * @param listener
	 * @see java.beans.PropertyChangeSupport#addPropertyChangeListener(java.lang.String,
	 *      java.beans.PropertyChangeListener)
	 */
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(propertyName, listener);
	}

	/**
	 * @param propertyName
	 * @param listener
	 * @see java.beans.PropertyChangeSupport#removePropertyChangeListener(java.lang.String,
	 *      java.beans.PropertyChangeListener)
	 */
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		listeners.removePropertyChangeListener(propertyName, listener);
	}

	/**
	 * @param column
	 * @return
	 */
	public ESorting getSorting(IScore column) {
		ESorting s = orderBy.get(column);
		return s == null ? ESorting.NONE : s;
	}

	/**
	 * @return whether the query is ranked aka sorted in any kind
	 */
	public boolean isSorted() {
		return !this.orderBy.isEmpty();
	}

	/**
	 * sort the query by the given element using the given order
	 *
	 * @param elem
	 *            what
	 * @param sorting
	 *            how
	 */
	public void sortBy(IScore elem, ESorting sorting) {
		ScoreComparator old = sortByImpl(elem, sorting);
		listeners.firePropertyChange(PROP_ORDER_BY, old, orderBy);
	}

	private ScoreComparator sortByImpl(IScore elem, ESorting sorting) {
		ScoreComparator old = new ScoreComparator(orderBy);
		if (sorting == ESorting.NONE)
			orderBy.remove(elem);
		else {
			if (orderBy.size() >= MAX_SORTING && !orderBy.containsKey(elem)) {
				orderBy.remove(Iterables.getLast(orderBy.keySet()));
			}
			orderBy.put(elem, sorting);
		}
		return old;
	}

	/**
	 * add another column to the query
	 *
	 * @param score
	 */
	public void addSelection(IScore score) {
		selection.add(score);
		sortByImpl(score, score.getScoreType().isRank() ? ESorting.ASC : ESorting.DESC);
		submitComputation(Collections.singleton(score), null);


		listeners.fireIndexedPropertyChange(PROP_SELECTION, selection.size() - 1, null, score);
	}

	public void removeSelection(IScore score) {
		int i = selection.indexOf(score);
		if (i < 0)
			return;
		selection.remove(score);

		// remove all related filters
		for (Iterator<IScoreFilter> it = this.filter.iterator(); it.hasNext();) {
			if (it.next().getReference() == score)
				it.remove();
		}

		listeners.fireIndexedPropertyChange(PROP_SELECTION, i, score, null);

	}

	public int getTop() {
		return top;
	}

	/**
	 * @param top
	 *            the top to set
	 */
	public void setTop(int top) {
		if (this.top == top)
			return;
		listeners.firePropertyChange(PROP_TOP, this.top, this.top = top);
	}

	protected void onRemovedFilter(IDataDomainFilter oldValue) {
		// TODO find out what groups have changed
	}

	protected void onAddStratification(ATableBasedDataDomain dataDomain) {
		// compute all scores on the new stratifications
		submitComputation(this.selection, query.getStratifications(dataDomain));
	}

	/**
	 * triggers the computation of one or more scores on one or more stratifications
	 *
	 * @param score
	 * @param stratifications
	 */
	private void submitComputation(Iterable<IScore> score, Collection<TablePerspective> stratifications) {
		Collection<IScore> scores = Scores.flatten(score);

		// submit all stratifications computations
		Iterable<IComputedStratificationScore> stratScores = Iterables.filter(scores, IComputedStratificationScore.class);
		if (!Iterables.isEmpty(stratScores)) {
			if (stratifications == null) // if null use the all
				stratifications = query.call();
			for (IComputedStratificationScore s : stratScores)
				toCompute.add(ScoreComputer.submit(new ComputeStratificationScore(s, stratifications)));
		}

		// submit all group computations
		Iterable<IComputedGroupScore> groupScores = Iterables.filter(scores, IComputedGroupScore.class);
		if (!Iterables.isEmpty(groupScores)) {
			if (stratifications == null)
				stratifications = query.call();
			Multimap<TablePerspective, Group> groups = query.apply(stratifications);

			// split into batch and normal
			Multimap<Class<? extends IBatchComputedGroupScore>, IBatchComputedGroupScore> batches = ArrayListMultimap
					.create();
			for (IComputedGroupScore s : groupScores) {
				if (s instanceof IBatchComputedGroupScore) {
					IBatchComputedGroupScore c = (IBatchComputedGroupScore) s;
					batches.put(c.getClass(), c);
				} else {
					toCompute.add(ScoreComputer.submit(new ComputeGroupScore(s, groups)));
				}
			}
			// compute batches
			for (Class<? extends IBatchComputedGroupScore> batchType : batches.keySet()) {
				Collection<IBatchComputedGroupScore> b = batches.get(batchType);
				assert !b.isEmpty();
				toCompute.add(ScoreComputer.submit(new ComputeBatchGroupScore(b, groups)));
			}
		}
	}
}
