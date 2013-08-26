/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.caleydo.vis.lineup.model.mixin.IDoubleRankableColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IFilterColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IManualComparatorMixin;
import org.caleydo.vis.lineup.model.mixin.IRankableColumnMixin;

import com.jogamp.common.util.IntIntHashMap;

/**
 * @author Samuel Gratzl
 *
 */
public class ColumnRanker implements Iterable<IRow> {
	private final PropertyChangeSupport propertySupport = new PropertyChangeSupport(this);
	public static final String PROP_INVALID = "invalid";
	public static final String PROP_ORDER = "order";

	/**
	 * current filter to select data true ... visible false ... invsibile
	 */
	private BitSet filter;
	/**
	 * which entries of the 0-filter map still influence the ranking true ... filter = false + should still influence
	 * the rank
	 */
	private BitSet filteredOutInfluenceRanking;
	private boolean dirtyFilter = true;

	/**
	 * a set containing only the visible entries without reordering FIXME not implemented
	 **/
	private BitSet visible;
	private boolean dirtyVisible = true;

	/**
	 * current order
	 *
	 * <pre>
	 * order[i] = data index
	 * </pre>
	 */
	private int[] order;
	private IntIntHashMap ranks = new IntIntHashMap();
	private final IntIntHashMap exaequoOffsets = new IntIntHashMap();
	private boolean dirtyOrder = true;

	private IRankableColumnMixin orderBy;
	private boolean orderByFixed;

	/**
	 * the table can be null, use {@link #getTable()}
	 */
	private final RankTableModel table;
	/**
	 * can be null just for getting the {@link RankTableModel}
	 */
	private final OrderColumn model;

	private ColumnRanker(RankTableModel table, OrderColumn model) {
		this.table = table;
		this.model = model;
		this.ranks.setKeyNotFoundValue(-1);
	}

	public ColumnRanker(RankTableModel table) {
		this(table, null);
	}

	public ColumnRanker(OrderColumn model) {
		this(null, model);
	}

	public ColumnRanker(ColumnRanker clone, RankTableModel table, OrderColumn model) {
		this(table, model);
		this.ranks.setKeyNotFoundValue(-1);
		this.filter = clone.filter;
		this.dirtyFilter = clone.dirtyFilter;
		this.visible = clone.visible;
		this.dirtyVisible = clone.dirtyVisible;
		this.order = clone.order;
		this.exaequoOffsets.putAll(clone.exaequoOffsets);
		this.dirtyOrder = clone.dirtyOrder;
	}


	public ColumnRanker clone(OrderColumn model) {
		return new ColumnRanker(this, null, model);
	}

	public ColumnRanker clone(RankTableModel table) {
		return new ColumnRanker(this, table, null);
	}

	public RankTableModel getTable() {
		if (table != null)
			return table;
		return model.getTable();
	}

	/**
	 * are the current changes, e.g. moving triggers changes in the filtering or ordering?
	 *
	 * @param invalid
	 * @param col
	 */
	public void checkOrderChanges(ARankColumnModel from, ARankColumnModel to) {
		// check recursivly
		if (from instanceof ACompositeRankColumnModel) {
			for (ARankColumnModel child : ((ACompositeRankColumnModel) from))
				checkOrderChanges(child, null);
		}
		if (to instanceof ACompositeRankColumnModel) {
			for (ARankColumnModel child : ((ACompositeRankColumnModel) to))
				checkOrderChanges(null, child);
		}

		if (from instanceof IFilterColumnMixin && ((IFilterColumnMixin) from).isFiltered()) { // filter elements
			dirtyFilter = true;
			if (orderBy == from) {
				orderBy = null;
				orderByFixed = false;
			}
			fireInvalid();
			return;
		}
		if (to instanceof IFilterColumnMixin && ((IFilterColumnMixin) to).isFiltered()) { // filter elements
			dirtyFilter = true;
			if (orderBy == from) {
				orderBy = null;
				orderByFixed = false;
			}
			fireInvalid();
			return;
		}
		if (from != null && from == orderBy) {
			dirtyOrder = true;
			orderByFixed = false;
			orderBy = null;
			fireInvalid();
			return;
		}
		if (findFirstRankable() != orderBy) {
			dirtyOrder = true;
			orderByFixed = false;
			fireInvalid();
			return;
		}
	}

	private IRankableColumnMixin findFirstRankable() {
		if (orderByFixed && orderBy != null)
			return orderBy;
		for (Iterator<ARankColumnModel> it = getMyColumns(); it.hasNext();) {
			ARankColumnModel col = it.next();
			if (col instanceof IRankableColumnMixin)
				return (IRankableColumnMixin) col;
		}
		return null;
	}

	public void orderBy(IRankableColumnMixin column) {
		this.orderBy = column;
		this.orderByFixed = column != null;
		dirtyOrder = true;
		fireInvalid();
	}

	/**
	 * @return the orderBy, see {@link #orderBy}
	 */
	public IRankableColumnMixin getOrderBy() {
		getOrder();
		return orderBy;
	}

	private Iterator<ARankColumnModel> getMyColumns() {
		return getTable().getColumnsOf(this);
	}


	private Iterator<IFilterColumnMixin> findAllFiltered() {
		return getTable().findAllMyFilteredColumns(this);
	}

	public synchronized int size() {
		if (!dirtyOrder && order != null)
			return order.length;
		if (!dirtyFilter && filter != null)
			return filter.cardinality();
		return getOrder().length;
	}

	/**
	 * performs filtering
	 */
	private synchronized void filter() {
		if (!dirtyFilter)
			return;
		dirtyFilter = false;
		RankTableModel table = getTable();
		BitSet dataMask = table.getDataMask();
		final List<IRow> data = table.getData();
		// System.out.println("filter");
		// start with data mask
		BitSet new_;
		if (dataMask != null)
			new_ = (BitSet) dataMask.clone();
		else {
			new_ = new BitSet(data.size());
			new_.set(0, data.size());
		}
		// set of items should be filtered out but still influence the ranking
		BitSet new_InfluenceRanking = new BitSet(data.size());
		new_InfluenceRanking.set(0, data.size());

		for (Iterator<IFilterColumnMixin> it = findAllFiltered(); it.hasNext();) {
			it.next().filter(data, new_, new_InfluenceRanking);
		}
		new_InfluenceRanking.andNot(new_); // mask out all the visible entries

		dirtyOrder = true;
		filter = new_;
		filteredOutInfluenceRanking = new_InfluenceRanking;
		order();
	}

	/**
	 * sorts the current data
	 */
	synchronized void order() {
		if (!dirtyOrder && order != null)
			return;
		dirtyOrder = false;
		// System.out.println("sort");
		// order = null;
		int[] deltas = null;
		boolean anyDelta = false;

		// by what
		orderBy = findFirstRankable();

		exaequoOffsets.clear();

		//BitSet filter = getTable().getDefaultFilter().getFilter();

		final List<IRow> data = getTable().getData();
		if (orderBy == null) {
			List<IRow> targetOrderItems = new ArrayList<>(data.size());
			for (int i = 0; i < data.size(); ++i) {
				if (includeInRanking(i))
					continue;
				targetOrderItems.add(data.get(i));
			}
			final int size = orderedSize(targetOrderItems);
			int[] newOrder = new int[size];
			deltas = new int[newOrder.length];
			IntIntHashMap newRanks = new IntIntHashMap(newOrder.length);
			newRanks.setKeyNotFoundValue(-1);
			int j = 0;
			for (int i = 0; i < newOrder.length; ++i) {
				while (filteredOutInfluenceRanking.get(targetOrderItems.get(j).getIndex()))
					j++; // skip all filtered elements
				IRow r = targetOrderItems.get(j++);
				final int rank = j - 1;
				final int ri = r.getIndex();
				newOrder[i] = ri;
				exaequoOffsets.put(rank, -rank);
				if (ranks.get(ri) < 0) {// was not visible
					anyDelta = true;
					deltas[i] = Integer.MIN_VALUE;
				} else {
					int delta = rank - ranks.get(ri);
					deltas[i] = delta;
					anyDelta = anyDelta || delta != 0;
				}
				newRanks.put(ri, rank);
			}
			order = newOrder;
			ranks = newRanks;
		} else if (orderBy instanceof IDoubleRankableColumnMixin && !(orderBy instanceof IManualComparatorMixin)) {
			IDoubleRankableColumnMixin orderByF = (IDoubleRankableColumnMixin) orderBy;
			List<IntDouble> targetOrderItems = new ArrayList<>(data.size());
			for (int i = 0; i < data.size(); ++i) {
				if (includeInRanking(i)) {
					targetOrderItems.add(new IntDouble(i, orderByF.applyPrimitive(data.get(i))));
				}
			}
			final int size = targetOrderItems.size() - filteredOutInfluenceRanking.cardinality();

			Collections.sort(targetOrderItems);

			int[] newOrder = new int[size];
			deltas = new int[newOrder.length];
			IntIntHashMap newRanks = new IntIntHashMap(newOrder.length);
			newRanks.setKeyNotFoundValue(-1);

			int offset = 0;
			double last = Double.NaN;
			int j = 0;
			for (int i = 0; i < newOrder.length; ++i) {
				for (IntDouble vi = targetOrderItems.get(j); filteredOutInfluenceRanking.get(vi.id); vi = targetOrderItems
						.get(++j)) {
					// skip all filtered elements but handle exaequo
					if (last == vi.value)
						offset++;
					else
						offset = 0;
					last = vi.value;
				}
				IntDouble pair = targetOrderItems.get(j++);
				final int ri = pair.id;
				final int rank = j - 1;
				if (last == pair.value) {
					offset++;
					exaequoOffsets.put(rank, offset);
				} else {
					offset = 0;
				}
				last = pair.value;
				newOrder[i] = pair.id;
				if (ranks.get(ri) < 0) {// was not visible
					anyDelta = true;
					deltas[i] = Integer.MIN_VALUE;
				} else {
					int delta = rank - ranks.get(ri);
					deltas[i] = delta;
					anyDelta = anyDelta || delta != 0;
				}
				newRanks.put(ri, rank);
			}
			order = newOrder;
			ranks = newRanks;
		} else {
			List<IRow> targetOrderItems = new ArrayList<>(data.size());
			for (int i = 0; i < data.size(); ++i) {
				if (includeInRanking(i)) {
					targetOrderItems.add(data.get(i));
				}
			}
			Collections.sort(targetOrderItems, orderBy);
			final int size = orderedSize(targetOrderItems);

			int[] newOrder = new int[size];
			deltas = new int[newOrder.length];
			IntIntHashMap newRanks = new IntIntHashMap(newOrder.length);
			newRanks.setKeyNotFoundValue(-1);

			int offset = 0;
			IRow last = null;
			int j = 0;
			for (int i = 0; i < newOrder.length; ++i) {
				for (IRow r = targetOrderItems.get(j); filteredOutInfluenceRanking.get(r.getIndex()); r = targetOrderItems
						.get(++j)) {
					// skip all filtered elements but handle exaequo
					if (last != null && orderBy.compare(last, r) == 0)
						offset++;
					else
						offset = 0;
					last = r;
				}
				IRow r = targetOrderItems.get(j++);
				final int ri = r.getIndex();
				final int rank = j - 1;
				newOrder[i] = r.getIndex();
				if (last != null && orderBy.compare(last, r) == 0) {
					offset++;
					exaequoOffsets.put(rank, offset);
				} else {
					offset = 0;
				}
				last = r;
				if (ranks.get(ri) < 0) {// was not visible
					anyDelta = true;
					deltas[i] = Integer.MIN_VALUE;
				} else {
					int delta = rank - ranks.get(ri);
					deltas[i] = delta;
					anyDelta = anyDelta || delta != 0;
				}
				newRanks.put(ri, rank);
			}
			order = newOrder;
			ranks = newRanks;
		}
		if (anyDelta)
			propertySupport.firePropertyChange(PROP_ORDER, deltas, order);
	}

	private int orderedSize(List<IRow> targetOrderItems) {
		return targetOrderItems.size() - filteredOutInfluenceRanking.cardinality();
	}

	private boolean includeInRanking(int i) {
		return filter.get(i) || filteredOutInfluenceRanking.get(i);
	}

	private static final class IntDouble implements Comparable<IntDouble> {
		private final int id;
		private final double value;

		public IntDouble(int id, double value) {
			this.id = id;
			this.value = value;
		}

		@Override
		public int compareTo(IntDouble o) {
			return -nanCompare(value, o.value, false);
		}
	}

	public static int nanCompare(double a, double b, boolean isNaNLarge) {
		boolean n1 = Double.isNaN(a);
		boolean n2 = Double.isNaN(b);
		if (n1 != n2)
			return (n1 ? 1 : -1) * (isNaNLarge ? 1 : -1);
		return Double.compare(a, b);
	}

	public int getVisualRank(IRow row) {
		int r = ranks.get(row.getIndex());
		if (r < 0)
			return -1;
		if (exaequoOffsets.containsKey(r)) {
			return r - exaequoOffsets.get(r) + 1;
		}
		return r + 1;
	}

	public int getRank(IRow row) {
		if (row == null)
			return -1;
		getOrder();
		return ranks.get(row.getIndex());
	}

	public boolean hasDefinedRank() {
		getOrder();
		return orderBy != null;
	}


	public IRow get(int rank) {
		if (rank < 0)
			return null;
		int[] order2 = getOrder();
		if (order2.length <= rank)
			return null;
		return getTable().getDataItem(order2[rank]);
	}

	/**
	 * @return
	 */
	public synchronized BitSet getFilter() {
		filter();
		return filter;
	}

	/**
	 * @return
	 */
	public synchronized int[] getOrder() {
		if (dirtyFilter) {
			filter();
		} else
			order();
		return order;
	}

	@Override
	public Iterator<IRow> iterator() {
		final int[] order = getOrder();
		final RankTableModel table = getTable();
		return new Iterator<IRow>() {
			int cursor = 0;

			@Override
			public boolean hasNext() {
				return cursor < order.length;
			}

			@Override
			public IRow next() {
				return table.getDataItem(order[cursor++]);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	public void dirtyFilter() {
		dirtyFilter = true;
		fireInvalid();
	}

	public void dirtyOrder() {
		dirtyOrder = true;
		fireInvalid();
	}

	/**
	 *
	 */
	private void fireInvalid() {
		getTable().fireRankingInvalidOf(this);
		propertySupport.firePropertyChange(PROP_INVALID, false, true);
	}

	void fireInvalidEvent() {
		propertySupport.firePropertyChange(PROP_INVALID, false, true);
	}

	/**
	 * @return
	 */
	public int getSelectedRank() {
		return getRank(getTable().getSelectedRow());
	}

	public IRow selectFirst() {
		return get(0);
	}

	public IRow selectNext(IRow row) {
		int r = getRank(row);
		if (r == getOrder().length - 1)
			return row;
		return get(r + 1);
	}

	public IRow selectPrevious(IRow row) {
		return get(getRank(row) - 1);
	}

	public final void addPropertyChangeListener(PropertyChangeListener listener) {
		propertySupport.addPropertyChangeListener(listener);
	}

	public final void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertySupport.addPropertyChangeListener(propertyName, listener);
	}

	public final void removePropertyChangeListener(PropertyChangeListener listener) {
		propertySupport.removePropertyChangeListener(listener);
	}

	public final void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertySupport.removePropertyChangeListener(propertyName, listener);
	}

	@Override
	public String toString() {
		if (model != null)
			return model.getTitle();
		return "defaultOne";
	}

}
