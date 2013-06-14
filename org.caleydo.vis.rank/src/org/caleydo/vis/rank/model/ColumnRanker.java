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
package org.caleydo.vis.rank.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.caleydo.vis.rank.model.mixin.IFilterColumnMixin;
import org.caleydo.vis.rank.model.mixin.IFloatRankableColumnMixin;
import org.caleydo.vis.rank.model.mixin.IRankableColumnMixin;

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
		checkOrder();
		return orderBy;
	}

	private Iterator<ARankColumnModel> getMyColumns() {
		return getTable().getColumnsOf(this);
	}


	private Iterator<IFilterColumnMixin> findAllFiltered() {
		return getTable().findAllMyFilteredColumns(this);
	}

	public int size() {
		if (!dirtyOrder && order != null)
			return order.length;
		if (!dirtyFilter && filter != null)
			return filter.cardinality();
		checkOrder();
		return order.length;
	}

	/**
	 * performs filtering
	 */
	private void filter() {
		if (!dirtyFilter)
			return;
		dirtyFilter = false;
		RankTableModel table = getTable();
		BitSet dataMask = table.getDataMask();
		final List<IRow> data = table.getData();
		// System.out.println("filter");
		// start with data mask
		if (dataMask != null)
			filter = (BitSet) dataMask.clone();
		else {
			filter = new BitSet(data.size());
			filter.set(0, data.size());
		}

		for (Iterator<IFilterColumnMixin> it = findAllFiltered(); it.hasNext();) {
			it.next().filter(data, filter);
		}

		dirtyOrder = true;
		order();
	}

	private void checkOrder() {
		if (dirtyFilter) {
			filter();
		} else
			order();
	}

	/**
	 * sorts the current data
	 */
	void order() {
		if (!dirtyOrder && order != null)
			return;
		dirtyOrder = false;
		// System.out.println("sort");
		order = null;
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
		} else if (orderBy instanceof IFloatRankableColumnMixin && !(orderBy instanceof NestedRankColumnModel)) {
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
			boolean n1 = Float.isNaN(value);
			boolean n2 = Float.isNaN(o.value);
			if (n1 != n2)
				return n1 ? 1 : -1;
			int c = -Float.compare(value, o.value);
			// if (c == 0)
			// return Integer.compare(id, o.id);
			return c;
		}
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
		checkOrder();
		return ranks.get(row.getIndex());
	}

	public boolean hasDefinedRank() {
		checkOrder();
		return orderBy != null;
	}


	public IRow get(int rank) {
		if (rank < 0)
			return null;
		checkOrder();
		if (order.length <= rank)
			return null;
		return getTable().getDataItem(order[rank]);
	}

	/**
	 * @return
	 */
	public BitSet getFilter() {
		filter();
		return filter;
	}

	/**
	 * @return
	 */
	public int[] getOrder() {
		checkOrder();
		return order;
	}

	@Override
	public Iterator<IRow> iterator() {
		checkOrder();
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
		if (r == order.length - 1)
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
