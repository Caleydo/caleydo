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

import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.ARecordPerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.execution.SafeCallable;
import org.caleydo.view.tourguide.api.query.filter.CompositeScoreFilter;
import org.caleydo.view.tourguide.api.score.CollapseScore;
import org.caleydo.view.tourguide.internal.compute.ComputeScoreJob;
import org.caleydo.view.tourguide.internal.compute.ComputeStratificationJob;
import org.caleydo.view.tourguide.internal.event.ScoreQueryReadyEvent;
import org.caleydo.view.tourguide.internal.query.RankedListBuilders;
import org.caleydo.view.tourguide.internal.query.RankedListBuilders.IRankedListBuilder;
import org.caleydo.view.tourguide.internal.query.ScoreComparator;
import org.caleydo.view.tourguide.internal.score.Scores;
import org.caleydo.view.tourguide.spi.compute.IComputedGroupScore;
import org.caleydo.view.tourguide.spi.compute.IComputedStratificationScore;
import org.caleydo.view.tourguide.spi.query.filter.IDataDomainFilter;
import org.caleydo.view.tourguide.spi.query.filter.IScoreFilter;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

/**
 * container for all the score query elements + a way how to compute the scored scoring elements out of that query
 *
 * @author Samuel Gratzl
 *
 */
public class ScoreQuery implements SafeCallable<List<ScoringElement>>, Cloneable {
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

	// current compute job
	private volatile Job job;

	public ScoreQuery(DataDomainQuery query) {
		this.query = query;
		this.query.addPropertyChangeListener(DataDomainQuery.PROP_SELECTION, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				IndexedPropertyChangeEvent ievt = (IndexedPropertyChangeEvent) evt;
				if (ievt.getOldValue() == null) { // new value
					onAddStratification((IDataDomain) ievt.getNewValue());
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
		this.query.addPropertyChangeListener(DataDomainQuery.PROP_FILTER, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				IndexedPropertyChangeEvent ievt = (IndexedPropertyChangeEvent) evt;
				if (ievt.getNewValue() == null) { // removed filter
					onRemovedFilter((IDataDomainFilter) ievt.getOldValue());
				}
			}
		});
		this.query.addPropertyChangeListener(DataDomainQuery.PROP_MODE, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				onModeChanged((EDataDomainQueryMode) evt.getNewValue());
			}
		});
	}

	@Override
	public ScoreQuery clone() {
		ScoreQuery clone = new ScoreQuery(query.clone());
		clone.selection.addAll(this.selection);
		clone.orderBy.putAll(this.orderBy);
		clone.top = this.top;
		clone.filter = this.filter.clone();
		return clone;
	}

	/**
	 * @return the query, see {@link #query}
	 */
	public DataDomainQuery getQuery() {
		return query;
	}

	/**
	 * executes this query and returns the resulting scoring element list
	 */
	@Override
	public List<ScoringElement> call() {
		final Pair<List<CollapseScore>, Integer> pair = filterCollapseScore(Scores.flatten(selection));
		final List<CollapseScore> collapseScores = pair.getFirst();
		final int factor = pair.getSecond();

		Collection<Pair<ARecordPerspective, TablePerspective>> stratifications = query.call();

		Map<IScore, IScore> selections = new HashMap<IScore, IScore>(collapseScores.size());
		IRankedListBuilder builder;
		if (isGroupQuery()) { // we need to show strat,group pairs
			Function<Pair<ARecordPerspective, TablePerspective>, ARecordPerspective> mapFirst = Pair.mapFirst();
			Multimap<ARecordPerspective, Group> stratNGroups = query.apply(Collections2.transform(stratifications,
					mapFirst));
			final int numberOfElements = stratNGroups.size() * factor;
			builder = RankedListBuilders.create(top, numberOfElements, filter, orderBy);
			buildAll2(builder, 0, collapseScores, selections, stratNGroups, stratifications);
		} else { // just stratifications
			final int numberOfElements = stratifications.size() * factor;
			builder = RankedListBuilders.create(top, numberOfElements, filter, orderBy);
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
			Map<IScore, IScore> selections, Collection<Pair<ARecordPerspective, TablePerspective>> stratifications) {
		if (i == composites.size()) {
			HashMap<IScore, IScore> s = new HashMap<>(selections);
			// last one trigger
			for (Pair<ARecordPerspective, TablePerspective> elem : stratifications) {
				builder.add(new ScoringElement(elem.getFirst(), null, elem.getSecond(), s));
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
			Map<IScore, IScore> selections, Multimap<ARecordPerspective, Group> stratNGroups,
			Collection<Pair<ARecordPerspective, TablePerspective>> stratifications) {
		if (i == composites.size()) {
			// last one trigger
			HashMap<IScore, IScore> s = new HashMap<>(selections);
			for (Pair<ARecordPerspective, TablePerspective> p : stratifications) {
				for (Group g : stratNGroups.get(p.getFirst()))
					builder.add(new ScoringElement(p.getFirst(), g, p.getSecond(), s));
			}
		} else {
			for (IScore child : composites.get(i)) {
				selections.put(composites.get(i), child); // set actual
				// trigger rest
				buildAll2(builder, i + 1, composites, selections, stratNGroups, stratifications);
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

	public void removeFilter(IScoreFilter filter) {
		if (!this.filter.remove(filter))
			return;
		listeners.fireIndexedPropertyChange(PROP_FILTER, -1, filter, null);
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
	public void addSelection(IScore... scores) {
		this.addSelection(Arrays.asList(scores));
	}

	public void addSelection(Collection<IScore> scores) {
		// filter out that don't support the current mode
		scores = Collections2.filter(scores, query.getMode().isSupportedBy());
		if (scores.isEmpty())
			return;

		this.selection.addAll(0, scores);
		IScore last = Iterables.getLast(scores);
		sortByImpl(last, last.getDefaultSorting());
		submitComputation(scores, null);

		listeners.fireIndexedPropertyChange(PROP_SELECTION, selection.size() - 1, null, last);
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

	public EDataDomainQueryMode getMode() {
		return query.getMode();
	}

	protected void onRemovedFilter(IDataDomainFilter oldValue) {
		// TODO find out what groups have changed

	}

	protected void onAddStratification(IDataDomain iDataDomain) {
		// compute all scores on the new stratifications
		submitComputation(this.selection, query.getJustStratifications(iDataDomain));
	}

	protected void onModeChanged(EDataDomainQueryMode mode) {
		//remove all not valid orders
		for (IScore s : Lists.newArrayList(orderBy.keySet()))
			if (!s.supports(mode))
				sortBy(s, ESorting.NONE);

		//remove all not valid scores
		for(IScore s : Lists.newArrayList(selection))
			if (!s.supports(mode))
				removeSelection(s);

		// remove all not valid filters
		for (IScoreFilter sf : Lists.newArrayList(this.filter))
			if (!sf.getReference().supports(mode))
				removeFilter(sf);
	}

	public boolean isJobRunning() {
		return this.job != null;
	}
	/**
	 * triggers the computation of one or more scores on one or more stratifications
	 *
	 * @param score
	 * @param stratifications
	 */
	private void submitComputation(Iterable<IScore> score, Collection<ARecordPerspective> stratifications) {
		Collection<IScore> scores = Scores.flatten(score);

		Job job = null;

		// submit all stratifications computations
		List<IComputedStratificationScore> stratScores = Lists.newArrayList(Iterables.filter(scores,
				IComputedStratificationScore.class));
		List<IComputedGroupScore> groupScores = Lists.newArrayList(Iterables.filter(scores, IComputedGroupScore.class));

		final Function<Pair<ARecordPerspective, TablePerspective>, ARecordPerspective> mapFirst = Pair.mapFirst();

		if (!stratScores.isEmpty() && groupScores.isEmpty()) {
			//just stratifications
			if (stratifications == null) {
				stratifications = Collections2.transform(query.call(), mapFirst);
			}
			job = new ComputeStratificationJob(stratifications, stratScores);
		} else if (!groupScores.isEmpty()) {
			// both or just groups
			if (stratifications == null)
				stratifications = Collections2.transform(query.call(), mapFirst);
			Multimap<ARecordPerspective, Group> data = query.apply(stratifications);
			job = new ComputeScoreJob(data, stratScores, groupScores);
		} else {
			job = null;
		}
		if (job == null)
			this.job = null;
		else {
			job.addJobChangeListener(new JobChangeAdapter() {
				@Override
				public void done(IJobChangeEvent event) {
					ScoreQuery.this.job = null;
					GeneralManager.get().getEventPublisher().triggerEvent(new ScoreQueryReadyEvent(ScoreQuery.this));
				}
			});
			this.job = job;
			job.schedule();
		}
	}
}
