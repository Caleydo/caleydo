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
package org.caleydo.view.tourguide.v3.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.view.tourguide.v3.layout.RowHeightLayouts;
import org.caleydo.view.tourguide.v3.model.mixin.IFilterColumnMixin;
import org.caleydo.view.tourguide.v3.model.mixin.IMappedColumnMixin;
import org.caleydo.view.tourguide.v3.model.mixin.IRankableColumnMixin;

import com.google.common.collect.Iterators;

/**
 * @author Samuel Gratzl
 *
 */
public class RankTableModel implements Iterable<IRow>, IRankColumnParent {
	public static final String PROP_SELECTED_ROW = "selectedRow";
	public static final String PROP_ORDER = "order";
	public static final String PROP_DATA = "data";
	public static final String PROP_COLUMNS = "columns";
	public static final String PROP_POOL = "pool";
	public static final String PROP_INVALID = "invalid";

	private final PropertyChangeSupport propertySupport = new PropertyChangeSupport(this);

	private List<ARankColumnModel> columns = new ArrayList<>();
	private List<ARankColumnModel> pool = new ArrayList<>(2);

	private final PropertyChangeListener resort = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			dirtyOrder = true;
			fireInvalid();
		}
	};
	private final PropertyChangeListener refilter = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			dirtyFilter = true;
			fireInvalid();
		}
	};


	private final List<IRow> data = new ArrayList<>();
	private BitSet dataMask;

	private int selectedRank = -1;
	private IRow selectedRow = null;

	private boolean dirtyFilter = true;
	private BitSet filter;

	private boolean dirtyOrder = true;
	private IRankableColumnMixin orderBy;
	private int[] order;

	public void addData(Collection<IRow> rows) {
		int s = this.data.size();
		for (IRow r : rows)
			r.setIndex(s++);
		this.data.addAll(rows);
		propertySupport.fireIndexedPropertyChange(PROP_DATA, s, null, rows);
		filter();
	}

	protected void fireInvalid() {
		propertySupport.firePropertyChange(PROP_INVALID, false, true);
	}

	public void setDataMask(BitSet dataMask) {
		this.dataMask = dataMask;
	}

	public void addColumn(ARankColumnModel col) {
		setup(col);
		add(col);
	}

	public void addColumnTo(ACompositeRankColumnModel parent, ARankColumnModel col) {
		setup(col);
		parent.add(col);
	}

	private void add(ARankColumnModel col) {
		add(columns.size(), col);
	}

	private void add(int index, ARankColumnModel col) {
		col.setParent(this);
		this.columns.add(index, col); // intelligent positioning
		propertySupport.fireIndexedPropertyChange(PROP_COLUMNS, index, null, col);
		checkOrderChanges(index, col);
	}

	private void checkOrderChanges(int index, ARankColumnModel col) {
		if (col instanceof IFilterColumnMixin && ((IFilterColumnMixin) col).isFiltered()) {
			dirtyFilter = true;
			fireInvalid();
			return;
		}

	}

	@Override
	public final void move(ARankColumnModel model, int to) {
		int from = this.columns.indexOf(model);
		if (model.getParent() == this && from >= 0) { // move within the same parent
			if (from == to)
				return;
			columns.add(to, model);
			columns.remove(from < to ? from : from + 1);
			propertySupport.fireIndexedPropertyChange(PROP_COLUMNS, to, from, model);
			checkOrderChanges(to, model);
		} else {
			model.getParent().detach(model);
			add(to, model);
		}
	}

	@Override
	public boolean isMoveAble(ARankColumnModel model, int index) {
		return model.getParent().isHideAble(model);
	}

	@Override
	public void replace(ARankColumnModel from, ARankColumnModel to) {
		int i = this.columns.indexOf(from);
		columns.set(i, to);
		to.setParent(this);
		propertySupport.fireIndexedPropertyChange(PROP_COLUMNS, i, from, to);
		from.setParent(null);
		checkOrderChanges(i, to);
	}

	@Override
	public void detach(ARankColumnModel model) {
		remove(model);
		removeFromPool(model);
		checkOrderChanges(-1, model);
	}

	/**
	 * @return
	 */
	public ACompositeRankColumnModel createCombined() {
		ACompositeRankColumnModel new_ = new MaxCompositeRankColumnModel(RowHeightLayouts.HINTS);
		setup(new_);
		return new_;
	}

	public boolean isCombineAble(ARankColumnModel model, ARankColumnModel with) {
		if (model == with)
			return false;
		if (!MaxCompositeRankColumnModel.canBeChild(model) || !MaxCompositeRankColumnModel.canBeChild(with))
			return false;
		if (model.getParent() == with || with.getParent() == model) // already children
			return false;
		if (!with.getParent().isHideAble(with)) // b must be hide able
			return false;
		return true;
	}

	private void setup(ARankColumnModel col) {
		if (col instanceof StackedRankColumnModel)
			col.addPropertyChangeListener(ARankColumnModel.PROP_WEIGHT, resort);
		col.addPropertyChangeListener(IMappedColumnMixin.PROP_MAPPING, refilter);
		col.addPropertyChangeListener(IFilterColumnMixin.PROP_FILTER, refilter);
		if (col instanceof ACompositeRankColumnModel) {
			for (ARankColumnModel child : ((ACompositeRankColumnModel) col))
				setup(child);
		}
	}

	private void takeDown(ARankColumnModel col) {
		col.removePropertyChangeListener(ARankColumnModel.PROP_WEIGHT, resort);
		col.removePropertyChangeListener(IMappedColumnMixin.PROP_MAPPING, refilter);
		col.removePropertyChangeListener(IFilterColumnMixin.PROP_FILTER, refilter);
		if (col instanceof ACompositeRankColumnModel) {
			for (ARankColumnModel child : ((ACompositeRankColumnModel) col))
				takeDown(child);
		}
	}

	private void remove(ARankColumnModel model) {
		int index = columns.indexOf(model);
		if (index < 0)
			return;
		columns.remove(model);
		propertySupport.fireIndexedPropertyChange(PROP_COLUMNS, index, model, null);
		model.setParent(null);
		checkOrderChanges(index, model);
	}

	public boolean destroy(ARankColumnModel col) {
		removeFromPool(col);
		takeDown(col);
		return true;
	}

	/**
	 * @param model
	 */
	void addToPool(ARankColumnModel model) {
		int bak = pool.size();
		this.pool.add(model);
		model.setParent(this);
		ARankColumnModel.uncollapse(model);
		propertySupport.fireIndexedPropertyChange(PROP_POOL, bak, null, model);
		checkOrderChanges(-1, model);
	}

	void removeFromPool(ARankColumnModel model) {
		int index = pool.indexOf(model);
		if (index < 0)
			return;
		pool.remove(index);
		propertySupport.fireIndexedPropertyChange(PROP_POOL, index, model, null);
		model.setParent(null);
	}

	@Override
	public boolean hide(ARankColumnModel model) {
		remove(model);
		addToPool(model);
		return true;
	}

	@Override
	public boolean isDestroyAble(ARankColumnModel model) {
		return pool.contains(model); // just elements in the pool
	}

	@Override
	public final boolean isCollapseAble(ARankColumnModel model) {
		return true;
	}

	@Override
	public boolean isHideAble(ARankColumnModel model) {
		return true;
	}

	@Override
	public RankTableModel getTable() {
		return this;
	}

	@Override
	public void explode(ACompositeRankColumnModel model) {
		int index = this.columns.indexOf(model);
		List<ARankColumnModel> children = model.getChildren();
		for (ARankColumnModel child : children)
			child.setParent(this);
		getTable().destroy(model);
		this.columns.set(index, children.get(0));
		propertySupport.fireIndexedPropertyChange(PROP_COLUMNS, index, model, children.get(0));
		if (children.size() > 1) {
			this.columns.addAll(index + 1, children.subList(1, children.size()));
			propertySupport.fireIndexedPropertyChange(PROP_COLUMNS, index + 1, null,
					children.subList(1, children.size()));
		}
		checkOrderChanges(index, children.get(0));
	}



	private IRankableColumnMixin findFirstRankable() {
		for (ARankColumnModel col : this.columns) {
			if (col instanceof IRankableColumnMixin)
				return (IRankableColumnMixin) col;
		}
		return null;
	}

	public StackedRankColumnModel findFirstStacked() {
		for (ARankColumnModel col : this.columns) {
			if (col instanceof StackedRankColumnModel)
				return (StackedRankColumnModel) col;
		}
		return null;
	}

	/**
	 * @return the columns, see {@link #columns}
	 */
	public List<ARankColumnModel> getColumns() {
		return Collections.unmodifiableList(columns);
	}

	/**
	 * @return the pool, see {@link #pool}
	 */
	public List<ARankColumnModel> getPool() {
		return Collections.unmodifiableList(pool);
	}

	private Iterator<ARankColumnModel> findAllColumns() {
		Collection<ARankColumnModel> c = new ArrayList<>();
		findAllColumns(c, this.columns);
		return c.iterator();
	}

	private void findAllColumns(Collection<ARankColumnModel> c, Iterable<ARankColumnModel> cols) {
		for (ARankColumnModel col : cols) {
			if (col instanceof ACompositeRankColumnModel) {
				findAllColumns(c, (ACompositeRankColumnModel) col);
			} else
				c.add(col);
		}
	}

	private Iterator<IFilterColumnMixin> findAllFiltered() {
		return Iterators.filter(findAllColumns(), IFilterColumnMixin.class);
	}


	public int size() {
		checkOrder();
		return order.length;
	}

	private void filter() {
		if (!dirtyFilter)
			return;
		dirtyFilter = false;
		System.out.println("filter");
		if (dataMask != null)
			filter = (BitSet) dataMask.clone();
		else {
			filter = new BitSet(data.size());
			filter.set(0, data.size());
		}

		for (Iterator<IFilterColumnMixin> it = findAllFiltered(); it.hasNext();) {
			filter.and(it.next().getSelectedRows(data));
		}

		if (selectedRow != null && !filter.get(selectedRow.getIndex()))
			setSelectedRow(-1);

		dirtyOrder = true;
		order();
	}

	private void checkOrder() {
		if (dirtyFilter) {
			filter();
		} else
			order();
	}

	private void order() {
		if (!dirtyOrder)
			return;
		dirtyOrder = false;
		System.out.println("sort");
		int[] bak = order;

		orderBy = findFirstRankable();
		if (orderBy == null) {
			int rank = 0;
			for (int i = 0; i < data.size(); ++i) {
				if (!filter.get(i)) {
					data.get(i).setRank(-1);
				} else {
					data.get(i).setRank(rank++);
				}
			}
			order = new int[rank];
			for(int i = 0; i < order.length; ++i)
				order[i] = i;
		} else {
			List<Pair<Integer,Float>> tmp = new ArrayList<>(data.size());
			for (int i = 0; i < data.size(); ++i) {
				if (!filter.get(i)) {
					data.get(i).setRank(-1);
				} else {
					tmp.add(Pair.make(i, -orderBy.getValue(data.get(i))));
				}
			}
			Collections.sort(tmp, Pair.<Float> compareSecond());

			order = new int[tmp.size()];
			for (int i = 0; i < tmp.size(); ++i) {
				order[i] = tmp.get(i).getFirst();
				data.get(order[i]).setRank(i);
			}
		}
		if (!Arrays.equals(bak, order))
			propertySupport.firePropertyChange(PROP_ORDER, bak, order);
	}

	/**
	 * @param selectedRow
	 *            setter, see {@link selectedRow}
	 */
	public void setSelectedRow(int selectedRank) {
		checkOrder();
		if (selectedRank >= order.length)
			selectedRank = order.length - 1;
		else if (selectedRank < 0) {
			selectedRank = -1;
		}
		if (this.selectedRank == selectedRank)
			return;
		this.selectedRank = selectedRank;
		propertySupport.firePropertyChange(PROP_SELECTED_ROW, this.selectedRow, this.selectedRow = get(selectedRank));
	}

	public IRow get(int index) {
		if (index < 0)
			return null;
		checkOrder();
		return data.get(order[index]);
	}

	public void selectNextRow() {
		setSelectedRow(selectedRow.getRank() + 1);
	}

	public void selectPreviousRow() {
		setSelectedRow(selectedRow.getRank() - 1);
	}

	/**
	 * @return the selectedRow, see {@link #selectedRow}
	 */
	public IRow getSelectedRow() {
		return selectedRow;
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

	public Collection<IRow> getData() {
		return Collections.unmodifiableCollection(this.data);
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
		return new Iterator<IRow>() {
			int cursor = 0;

			@Override
			public boolean hasNext() {
				return cursor < order.length;
			}

			@Override
			public IRow next() {
				return data.get(order[cursor++]);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
}

