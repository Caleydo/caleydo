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

import org.caleydo.vis.lineup.model.mixin.IFilterColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IFloatRankableColumnMixin;
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
	 * current filter to select data
	 */
	private BitSet filter;
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

		for (Iterator<IFilterColumnMixin> it = findAllFiltered(); it.hasNext();) {
			it.next().filter(data, new_);
		}

		dirtyOrder = true;
		filter = new_;
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
				if (!filter.get(i))
					continue;
				targetOrderItems.add(data.get(i));
			}
			int[] newOrder = new int[targetOrderItems.size()];
			deltas = new int[newOrder.length];
			IntIntHashMap newRanks = new IntIntHashMap(newOrder.length);
			newRanks.setKeyNotFoundValue(-1);
			for (int i = 0; i < newOrder.length; ++i) {
				IRow r = targetOrderItems.get(i);
				final int ri = r.getIndex();
				newOrder[i] = ri;
				exaequoOffsets.put(i, -i);
				if (ranks.get(ri) < 0) {// was not visible
					anyDelta = true;
					deltas[i] = Integer.MIN_VALUE;
				} else {
					int delta = i - ranks.get(ri);
					deltas[i] = delta;
					anyDelta = anyDelta || delta != 0;
				}
				newRanks.put(ri, i);
			}
			order = newOrder;
			ranks = newRanks;
		} else if (orderBy instanceof IFloatRankableColumnMixin && !(orderBy instanceof IManualComparatorMixin)) {
			IFloatRankableColumnMixin orderByF = (IFloatRankableColumnMixin) orderBy;
			List<IntFloat> targetOrderItems = new ArrayList<>(data.size());
			for (int i = 0; i < data.size(); ++i) {
				if (filter.get(i)) {
					targetOrderItems.add(new IntFloat(i, orderByF.applyPrimitive(data.get(i))));
				}
			}
			Collections.sort(targetOrderItems);

			int[] newOrder = new int[targetOrderItems.size()];
			deltas = new int[newOrder.length];
			IntIntHashMap newRanks = new IntIntHashMap(newOrder.length);
			newRanks.setKeyNotFoundValue(-1);

			int offset = 0;
			float last = Float.NaN;
			for (int i = 0; i < targetOrderItems.size(); ++i) {
				IntFloat pair = targetOrderItems.get(i);
				final int ri = pair.id;
				newOrder[i] = pair.id;
				if (last == pair.value) {
					offset++;
					exaequoOffsets.put(i, offset);
				} else {
					offset = 0;
				}
				if (ranks.get(ri) < 0) {// was not visible
					anyDelta = true;
					deltas[i] = Integer.MIN_VALUE;
				} else {
					int delta = i - ranks.get(ri);
					deltas[i] = delta;
					anyDelta = anyDelta || delta != 0;
				}
				newRanks.put(ri, i);
				last = pair.value;
			}
			order = newOrder;
			ranks = newRanks;
		} else {
			List<IRow> targetOrderItems = new ArrayList<>(data.size());
			for (int i = 0; i < data.size(); ++i) {
				if (filter.get(i)) {
					targetOrderItems.add(data.get(i));
				}
			}
			Collections.sort(targetOrderItems, orderBy);

			int[] newOrder = new int[targetOrderItems.size()];
			deltas = new int[newOrder.length];
			IntIntHashMap newRanks = new IntIntHashMap(newOrder.length);
			newRanks.setKeyNotFoundValue(-1);

			int offset = 0;
			IRow last = null;
			for (int i = 0; i < targetOrderItems.size(); ++i) {
				IRow pair = targetOrderItems.get(i);
				final int ri = pair.getIndex();
				newOrder[i] = pair.getIndex();
				if (last != null && orderBy.compare(last, pair) == 0) {
					offset++;
					exaequoOffsets.put(i, offset);
				} else {
					offset = 0;
				}
				if (ranks.get(ri) < 0) {// was not visible
					anyDelta = true;
					deltas[i] = Integer.MIN_VALUE;
				} else {
					int delta = i - ranks.get(ri);
					deltas[i] = delta;
					anyDelta = anyDelta || delta != 0;
				}
				newRanks.put(ri, i);
				last = pair;
			}
			order = newOrder;
			ranks = newRanks;
		}
		if (anyDelta)
			propertySupport.firePropertyChange(PROP_ORDER, deltas, order);
	}

	private static final class IntFloat implements Comparable<IntFloat> {
		private final int id;
		private final float value;

		public IntFloat(int id, float value) {
			this.id = id;
			this.value = value;
		}

		@Override
		public int compareTo(IntFloat o) {
			return -nanCompare(value, o.value, false);
		}
	}

	public static int nanCompare(float a, float b, boolean isNaNLarge) {
		boolean n1 = Float.isNaN(a);
		boolean n2 = Float.isNaN(b);
		if (n1 != n2)
			return (n1 ? 1 : -1) * (isNaNLarge ? 1 : -1);
		return Float.compare(a, b);
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
