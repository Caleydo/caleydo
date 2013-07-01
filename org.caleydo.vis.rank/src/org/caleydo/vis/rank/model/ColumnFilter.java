/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.rank.model;

import java.util.ArrayDeque;
import java.util.BitSet;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

import org.caleydo.vis.rank.model.mixin.IFilterColumnMixin;

import com.google.common.collect.Iterators;

/**
 * @author Samuel Gratzl
 *
 */
public class ColumnFilter {

	/**
	 * current filter to select data
	 */
	private BitSet filter;
	private boolean dirtyFilter = true;

	private final RankTableModel table;

	/**
	 * @param rankTableModel
	 */
	public ColumnFilter(RankTableModel table) {
		this.table = table;
	}

	public ColumnFilter(ColumnFilter clone, RankTableModel table) {
		this(table);
		this.filter = (BitSet) clone.filter.clone();
		this.dirtyFilter = clone.dirtyFilter;
	}

	/**
	 * @param rankTableModel
	 * @return
	 */
	public ColumnFilter clone(RankTableModel table) {
		return new ColumnFilter(this, table);
	}

	public boolean checkFilterChanges(ACompositeRankColumnModel from, ARankColumnModel to) {
		if (from instanceof IFilterColumnMixin && ((IFilterColumnMixin) from).isFiltered()) { // filter elements
			dirtyFilter = true;
			fireInvalid();
			return true;
		}
		if (to instanceof IFilterColumnMixin && ((IFilterColumnMixin) to).isFiltered()) { // filter elements
			dirtyFilter = true;
			fireInvalid();
			return true;
		}
		return false;
	}

	/**
	 * performs filtering
	 */
	void filter() {
		if (!dirtyFilter)
			return;
		dirtyFilter = false;
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

		table.dirtyAllOrders();
	}

	private Iterator<IFilterColumnMixin> findAllFiltered() {
		return Iterators.filter(getMyFlatColumns(), IFilterColumnMixin.class);
	}

	private Iterator<ARankColumnModel> getMyFlatColumns() {
		return new FlatIterator(table.getColumns().iterator());
	}

	/**
	 * @return
	 */
	public BitSet getFilter() {
		filter();
		return filter;
	}

	public void dirtyFilter() {
		dirtyFilter = true;
		fireInvalid();
	}

	/**
	 *
	 */
	private void fireInvalid() {
		table.fireFilterInvalid();
	}

	/**
	 * @return
	 */
	public boolean isDirty() {
		return dirtyFilter;
	}

	static class FlatIterator implements Iterator<ARankColumnModel> {
		private Deque<Iterator<ARankColumnModel>> stack = new ArrayDeque<>(3);

		public FlatIterator(Iterator<ARankColumnModel> it) {
			this.stack.push(it);
		}

		@Override
		public boolean hasNext() {
			while (!stack.isEmpty() && !stack.peekLast().hasNext())
				stack.pollLast();
			return !stack.isEmpty();
		}

		@Override
		public ARankColumnModel next() {
			ARankColumnModel m = stack.peekLast().next();
			if (m instanceof ACompositeRankColumnModel) {
				ACompositeRankColumnModel c = (ACompositeRankColumnModel) m;
				stack.push(c.iterator());
			}
			return m;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	/**
	 *
	 */
	public void reset() {
		// TODO Auto-generated method stub

	}
}
