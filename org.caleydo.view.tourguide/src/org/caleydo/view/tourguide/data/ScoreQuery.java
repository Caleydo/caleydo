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
import org.caleydo.view.tourguide.data.score.EScoreType;
import org.caleydo.view.tourguide.data.score.IBatchComputedGroupScore;
import org.caleydo.view.tourguide.data.score.IComputedGroupScore;
import org.caleydo.view.tourguide.data.score.IComputedStratificationScore;
import org.caleydo.view.tourguide.data.score.IScore;
import org.caleydo.view.tourguide.data.score.ProductScore;

import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

/**
 * @author Samuel Gratzl
 *
 */
public class ScoreQuery implements SafeCallable<List<ScoringElement>> {
	public static final String PROP_ORDER_BY = "orderBy";
	public static final String PROP_SELECTION = "selection";
	private static final int MAX_SORTING = 1;

	private final PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	private List<IScore> selection = new ArrayList<>();
	private CompositeScoreFilter filter = new CompositeScoreFilter();
	private ScoreComparator orderBy = new ScoreComparator();
	private int top = 35;

	private final DataDomainQuery query;

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
		this.query.addPropertyChangeListener(DataDomainQuery.PROP_FILTEr, new PropertyChangeListener() {
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
	 * returns whether there are outstanding computations
	 *
	 * @return
	 */
	public boolean isBusy() {
		for (Iterator<Future<?>> it = toCompute.iterator(); it.hasNext();) {
			if (it.next().isDone())
				it.remove();
			else
				return true;
		}
		return false;
	}

	public void waitTillComplete() {
		for (Iterator<Future<?>> it = toCompute.iterator(); it.hasNext();) {
			try {
				it.next().get();
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			it.remove();
		}
	}

	@Override
	public List<ScoringElement> call() {
		final Pair<List<ProductScore>, Integer> pair = filterProductScores(Scores.flatten(selection));
		final List<ProductScore> productScores = pair.getFirst();
		final int factor = pair.getSecond();

		Collection<TablePerspective> stratifications = query.call();

		Map<IScore, IScore> selections = new HashMap<IScore, IScore>(productScores.size());
		IRankedListBuilder builder;
		if (hasGroupScore(selection)) {
			Multimap<TablePerspective, Group> stratNGroups = query.apply(stratifications);
			builder = RankedListBuilders.create(top, stratNGroups.size() * factor, filter, orderBy);
			buildAll2(builder, 0, productScores, selections, stratNGroups);
		} else { // just stratifications
			builder = RankedListBuilders.create(top, stratifications.size() * factor, filter, orderBy);
			buildAll(builder, 0, productScores, selections, stratifications);
		}
		return builder.build();
	}

	private static boolean hasGroupScore(Collection<IScore> scores) {
		return Iterables.any(Scores.flatten(scores), isGroupScore);
	}

	private Pair<List<ProductScore>, Integer> filterProductScores(Collection<IScore> scores) {
		int factor = 1;
		List<ProductScore> productScores = new ArrayList<>(scores.size());
		for (IScore s : scores) {
			if (s instanceof ProductScore) {
				ProductScore p = (ProductScore) s;
				factor *= p.size();
				productScores.add(p);
			}
		}
		return Pair.make(productScores, factor);
	}

	private static final Predicate<IScore> isGroupScore = new Predicate<IScore>() {
		@Override
		public boolean apply(IScore in) {
			return in.getScoreType() == EScoreType.GROUP_SCORE;
		}
	};

	private static void buildAll(IRankedListBuilder builder, int i, List<ProductScore> composites,
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

	private static void buildAll2(IRankedListBuilder builder, int i, List<ProductScore> composites,
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

	public void sortBy(IScore elem, ESorting sorting) {
		ScoreComparator old = new ScoreComparator(orderBy);
		if (sorting == ESorting.NONE)
			orderBy.remove(elem);
		else {
			if (orderBy.size() >= MAX_SORTING && !orderBy.containsKey(elem)) {
				orderBy.remove(Iterables.getLast(orderBy.keySet()));
			}
			orderBy.put(elem, sorting);
		}
		listeners.firePropertyChange(PROP_ORDER_BY, old, orderBy);
	}

	public void addSelection(IScore score) {
		selection.add(score);
		listeners.fireIndexedPropertyChange(PROP_SELECTION, selection.size() - 1, null, score);

		submitComputation(Collections.singleton(score), null);
	}

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

		// submit all gropu computations
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

	public void removeSelection(IScore score) {
		int i = selection.indexOf(score);
		if (i < 0)
			return;
		selection.remove(score);
		listeners.fireIndexedPropertyChange(PROP_SELECTION, i, score, null);
	}

	protected void onRemovedFilter(IDataDomainFilter oldValue) {
		// find out what groups have changed

	}

	protected void onAddStratification(ATableBasedDataDomain dataDomain) {
		// compute all scores on the new stratifications
		submitComputation(this.selection, query.getStratifications(dataDomain));
	}
}
