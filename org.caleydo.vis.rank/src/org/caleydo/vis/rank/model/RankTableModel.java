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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

import org.caleydo.vis.rank.config.IRankTableConfig;
import org.caleydo.vis.rank.model.mixin.IFilterColumnMixin;
import org.caleydo.vis.rank.model.mixin.IMappedColumnMixin;
import org.caleydo.vis.rank.model.mixin.IRankColumnModel;
import org.caleydo.vis.rank.model.mixin.IRankableColumnMixin;

import com.google.common.collect.Iterables;

/**
 * basic model abstraction of a ranked list
 *
 * @author Samuel Gratzl
 *
 */
public class RankTableModel implements IRankColumnParent {
	public static final String PROP_SELECTED_ROW = "selectedRow";
	public static final String PROP_COLUMNS = "columns";
	public static final String PROP_POOL = "pool";
	public static final String PROP_DATA = "data";
	public static final String PROP_DATA_MASK = "datamask";

	private final PropertyChangeSupport propertySupport = new PropertyChangeSupport(this);

	/**
	 * current visible columns
	 */
	private List<ARankColumnModel> columns = new ArrayList<>();
	/**
	 * current hidden columns
	 */
	private List<ARankColumnModel> pool = new ArrayList<>(2);

	private final PropertyChangeListener resort = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			findCorrespondingRanker((IRankColumnModel) evt.getSource()).dirtyOrder();
		}
	};
	private final PropertyChangeListener refilter = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			findCorrespondingRanker((IRankColumnModel) evt.getSource()).dirtyFilter();
		}
	};

	/**
	 * settings
	 */
	private final IRankTableConfig config;
	/**
	 * the data of this table, not data can only be ADDED not removed, if you want to disable the use the
	 * {@link #dataMask}
	 */
	private final List<IRow> data = new ArrayList<>();
	/**
	 * mask selecting a subset of the data
	 */
	private BitSet dataMask;

	private IRow selectedRow = null;

	/**
	 * ranker used by default
	 */
	private final ColumnRanker defaultRanker;

	/**
	 *
	 */
	public RankTableModel(IRankTableConfig config) {
		this.config = config;
		this.defaultRanker = new ColumnRanker(this);
	}

	public RankTableModel(RankTableModel copy) {
		this.config = copy.config;
		this.selectedRow = copy.selectedRow;
		this.dataMask = copy.dataMask;
		this.data.addAll(copy.data);
		this.defaultRanker = copy.defaultRanker.clone(this);
		for(ARankColumnModel c : copy.pool)
			this.pool.add(c.clone());
		for(ARankColumnModel c : copy.columns)
			this.columns.add(c.clone());
	}

	@Override
	public RankTableModel clone() {
		return new RankTableModel(this);
	}

	/**
	 * removes all columns and clears the data
	 */
	public void reset() {
		for (ARankColumnModel c : this.columns)
			takeDown(c);
		this.columns.clear();
		for (ARankColumnModel c : this.pool)
			takeDown(c);
		this.pool.clear();
		this.dataMask = null;
		this.data.clear();
		this.selectedRow = null;
	}

	/**
	 * adds a collection of new data items to this table
	 *
	 * @param rows
	 */
	public void addData(Collection<? extends IRow> rows) {
		if (rows == null || rows.isEmpty())
			return;
		int s = this.data.size();
		for (IRow r : rows)
			r.setIndex(s++);
		this.data.addAll(rows);
		propertySupport.fireIndexedPropertyChange(PROP_DATA, s, null, rows);
		for (ColumnRanker order : findAllColumnRankers()) {
			order.dirtyFilter();
		}
	}

	public IRow getSelectedRow() {
		return selectedRow;
	}

	/**
	 * @param selectedRow
	 *            setter, see {@link selectedRow}
	 */
	public void setSelectedRow(IRow selectedRow) {
		propertySupport.firePropertyChange(PROP_SELECTED_ROW, this.selectedRow, this.selectedRow = selectedRow);
	}

	public void selectNextRow() {
		if (selectedRow == null)
			setSelectedRow(defaultRanker.selectFirst());
		else
			setSelectedRow(defaultRanker.selectNext(selectedRow));
	}

	public void selectPreviousRow() {
		if (selectedRow == null)
			return;
		setSelectedRow(defaultRanker.selectPrevious(selectedRow));
	}

	/**
	 * sets the data mask to filter the {@link #data}
	 *
	 * @param dataMask
	 */
	public void setDataMask(BitSet dataMask) {
		if (Objects.equals(dataMask, this.dataMask))
			return;
		boolean change = true;
		if (this.dataMask != null && dataMask != null) {
			this.dataMask.xor(dataMask);
			if (getDataSize() < this.dataMask.size())
				this.dataMask.clear(getDataSize(), this.dataMask.size());
			change = !this.dataMask.isEmpty(); // same data subset
		}
		propertySupport.firePropertyChange(PROP_DATA_MASK, dataMask, this.dataMask = (BitSet) dataMask.clone());
		if (change) {
			for (ColumnRanker r : findAllColumnRankers())
				r.dirtyFilter();
		}
	}

	/**
	 * @return the dataMask, see {@link #dataMask}
	 */
	public BitSet getDataMask() {
		return dataMask;
	}

	public void add(ARankColumnModel col) {
		add(columns.size(), col);
	}

	public void add(int index, ARankColumnModel col) {
		col.init(this);
		setup(col);
		this.columns.add(index, col); // intelligent positioning
		propertySupport.fireIndexedPropertyChange(PROP_COLUMNS, index, null, col);
		findCorrespondingRanker(index).checkOrderChanges(null, col);
	}

	@Override
	public final void move(ARankColumnModel model, int to, boolean clone) {
		int from = this.columns.indexOf(model);
		if (!clone && model.getParent() == this && from >= 0) { // move within the same parent
			if (from == to)
				return;
			ColumnRanker rOld = findCorrespondingRanker(from);
			ColumnRanker rNew = findCorrespondingRanker(to);
			columns.add(to, model);
			columns.remove(from < to ? from : from + 1);
			propertySupport.fireIndexedPropertyChange(PROP_COLUMNS, to, from, model);
			if (rOld != rNew) {
				rOld.checkOrderChanges(model, null);
				rNew.checkOrderChanges(null, model);
			} else
				rOld.checkOrderChanges(null, model);
		} else if (clone) {
			add(to, model.clone());
		} else {
			model.getParent().remove(model);
			add(to, model);
		}
	}

	@Override
	public boolean isMoveAble(ARankColumnModel model, int index, boolean clone) {
		return config.isMoveAble(model, clone) && (clone || model.getParent().isHideAble(model))
				&& !((model instanceof OrderColumn) && index == 0);
	}

	@Override
	public void replace(ARankColumnModel from, ARankColumnModel to) {
		int i = this.columns.indexOf(from);
		columns.set(i, to);
		to.init(this);
		propertySupport.fireIndexedPropertyChange(PROP_COLUMNS, i, from, to);
		from.takeDown();
		takeDown(from);
		findCorrespondingRanker(i).checkOrderChanges(from, to);
	}

	public boolean isCombineAble(ARankColumnModel model, ARankColumnModel with, boolean clone, int combineMode) {
		if (model == with)
			return false;
		if (model.getParent() == with || with.getParent() == model) // already children
			return false;
		if (!clone && !with.getParent().isHideAble(with)) // b must be hide able
			return false;
		return config.isCombineAble(model, with, clone, combineMode);
	}

	private void setup(ARankColumnModel col) {
		col.addPropertyChangeListener(ARankColumnModel.PROP_WIDTH, resort);
		col.addPropertyChangeListener(IMappedColumnMixin.PROP_MAPPING, refilter);
		col.addPropertyChangeListener(IFilterColumnMixin.PROP_FILTER, refilter);
	}

	private void takeDown(ARankColumnModel col) {
		col.removePropertyChangeListener(ARankColumnModel.PROP_WIDTH, resort);
		col.removePropertyChangeListener(IMappedColumnMixin.PROP_MAPPING, refilter);
		col.removePropertyChangeListener(IFilterColumnMixin.PROP_FILTER, refilter);
	}

	@Override
	public void remove(ARankColumnModel model) {
		int index = columns.indexOf(model);
		if (index < 0) { // maybe in the pool
			removeFromPool(model);
			return;
		}
		ColumnRanker r = findCorrespondingRanker(index);
		columns.remove(model);
		propertySupport.fireIndexedPropertyChange(PROP_COLUMNS, index, model, null);
		model.takeDown();
		r.checkOrderChanges(model, null);
	}

	/**
	 * @param model
	 */
	void addToPool(ARankColumnModel model) {
		int bak = pool.size();
		this.pool.add(model);
		model.init(this);
		model.setCollapsed(false);
		propertySupport.fireIndexedPropertyChange(PROP_POOL, bak, null, model);
	}

	public void removeFromPool(ARankColumnModel model) {
		int index = pool.indexOf(model);
		if (index < 0)
			return;
		pool.remove(index);
		propertySupport.fireIndexedPropertyChange(PROP_POOL, index, model, null);
		model.takeDown();
	}

	@Override
	public boolean hide(ARankColumnModel model) {
		remove(model);
		if (!config.isDestroyOnHide())
			addToPool(model);
		return true;
	}

	@Override
	public boolean isDestroyAble(ARankColumnModel model) {
		return pool.contains(model); // just elements in the pool
	}

	@Override
	public final boolean isCollapseAble(ARankColumnModel model) {
		return config.isDefaultCollapseAble();
	}

	@Override
	public boolean isHideAble(ARankColumnModel model) {
		return config.isDefaultHideAble();
	}

	@Override
	public boolean isHidden(ARankColumnModel model) {
		return pool.contains(model);
	}

	@Override
	public RankTableModel getTable() {
		return this;
	}

	/**
	 * explodes the given composite model into its components, i.e children
	 */
	@Override
	public void explode(ACompositeRankColumnModel model) {
		int index = this.columns.indexOf(model);
		List<ARankColumnModel> children = model.getChildren();
		for (ARankColumnModel child : children)
			child.init(this);
		this.columns.set(index, children.get(0));
		propertySupport.fireIndexedPropertyChange(PROP_COLUMNS, index, model, children.get(0));
		if (children.size() > 1) {
			this.columns.addAll(index + 1, children.subList(1, children.size()));
			propertySupport.fireIndexedPropertyChange(PROP_COLUMNS, index + 1, null,
					children.subList(1, children.size()));
		}
		findCorrespondingRanker(index).checkOrderChanges(model, children.get(0));
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

	public List<IRow> getData() {
		return Collections.unmodifiableList(this.data);
	}

	public List<IRow> getMaskedData() {
		if (dataMask == null || dataMask.cardinality() == data.size())
			return getData();
		final int[] lookup = new int[dataMask.cardinality()];
		int j = 0;
		for (int i = dataMask.nextSetBit(0); i >= 0; i = dataMask.nextSetBit(i + 1)) {
			lookup[j++] = i;
		}
		return new AbstractList<IRow>() {
			@Override
			public IRow get(int index) {
				return lookup == null ? data.get(index) : data.get(lookup[index]);
			}

			@Override
			public int size() {
				return lookup == null ? data.size() : lookup.length;
			}
		};
	}

	public int getDataSize() {
		return this.data.size();
	}

	private ColumnRanker findCorrespondingRanker(IRankColumnModel model) {
		if (model == null)
			return findCorrespondingRanker(-1);
		while (model.getParent() != this)
			model = model.getParent();
		return findCorrespondingRanker(columns.indexOf(model));
	}

	private ColumnRanker findCorrespondingRanker(int index) {
		if (index <= 0)
			return defaultRanker;
		for (ListIterator<ARankColumnModel> it = columns.listIterator(index); it.hasPrevious();) {
			ARankColumnModel m = it.previous();
			if (m instanceof OrderColumn)
				return ((OrderColumn) m).getRanker();
		}
		return defaultRanker;
	}

	private Iterable<ColumnRanker> findAllColumnRankers() {
		List<ColumnRanker> r = new ArrayList<>();
		for (ARankColumnModel col : columns)
			if (col instanceof OrderColumn)
				r.add(((OrderColumn) col).getRanker());
		return Iterables.concat(Collections.singleton(defaultRanker), r);
	}

	@Override
	public ColumnRanker getMyRanker(IRankColumnModel model) {
		return findCorrespondingRanker(model);
	}

	/**
	 * @return the config, see {@link #config}
	 */
	public IRankTableConfig getConfig() {
		return config;
	}

	@Override
	public IRankColumnModel getParent() {
		return null;
	}

	@Override
	public String getTitle() {
		return "RankTable";
	}

	/**
	 * @param columnRanker
	 */
	void fireRankingInvalidOf(ColumnRanker ranker) {
		int start = getStartIndex(ranker);
		for (ListIterator<ARankColumnModel> it = columns.listIterator(start); it.hasNext();) {
			ARankColumnModel m = it.next();
			if (m instanceof OrderColumn)
				break;
			m.onRankingInvalid();
		}
		if (ranker == defaultRanker) {
			for (ARankColumnModel m : this.pool)
				m.onRankingInvalid();
		}
	}

	Iterator<ARankColumnModel> getColumnsOf(ColumnRanker ranker) {
		int start = getStartIndex(ranker);
		if (start >= columns.size())
			return Collections.emptyIterator();

		List<ARankColumnModel> r = new ArrayList<>(columns.size() - start);
		for (ListIterator<ARankColumnModel> it = columns.listIterator(start); it.hasNext();) {
			ARankColumnModel m = it.next();
			if (m instanceof OrderColumn)
				break;
			r.add(m);
		}
		return r.iterator();
	}

	private int getStartIndex(ColumnRanker ranker) {
		int start = 0;
		if (ranker != defaultRanker) { // find the start
			for (ARankColumnModel col : columns) {
				start++;
				if ((col instanceof OrderColumn) && ((OrderColumn) col).getRanker() == ranker)
					break;
			}
		}
		return start;
	}

	public IRow getDataItem(int index) {
		return data.get(index);
	}

	@Override
	public void orderBy(IRankableColumnMixin model) {
		getMyRanker(model).orderBy(model);
	}

	/**
	 * adds a snapshot of the given column including a separator and a rank column
	 *
	 * @param model
	 */
	public void addSnapshot(ARankColumnModel model) {
		add(new OrderColumn());
		add(new RankRankColumnModel());
		add(model.clone());
	}
}

