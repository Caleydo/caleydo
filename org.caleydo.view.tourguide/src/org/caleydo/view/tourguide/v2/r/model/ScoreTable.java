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
package org.caleydo.view.tourguide.v2.r.model;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.caleydo.core.data.collection.Histogram;
import org.caleydo.core.util.collection.Pair;

import com.google.common.collect.Iterables;

/**
 * @author Samuel Gratzl
 *
 */
public class ScoreTable {
	public static final String PROP_SELECTED_ROW = "selectedRow";
	public static final String PROP_SELECTED_COL = "selectedCol";
	public static final String PROP_ORDER = "order";

	private final PropertyChangeSupport propertySupport = new PropertyChangeSupport(this);

	private List<ScoreColumn> combinedColumns = new ArrayList<>();
	private List<ScoreColumn> extraColumns = new ArrayList<>();
	private List<ScoreColumn> hiddenColumns = new ArrayList<>();

	private List<String> rows = new ArrayList<>();

	private Map<ScoreColumn, ScoreColumnData> data = new HashMap<>();

	private int[] order;

	private int selectedRow = -1;
	private int selectedCol = -1;

	public Map<String, Object> cache = new HashMap<>();

	static class ScoreColumnData {
		private final List<IValue> raw;
		private final List<IValue> normalized;
		private final BitSet selected;

		public ScoreColumnData(List<IValue> raw, List<IValue> normalized) {
			this.raw = raw;
			this.normalized = normalized;
			selected = new BitSet(raw.size());
		}
	}

	public ScoreTable() {
		initData();
	}

	private void initData() {
		final PropertyChangeListener sortOnChange = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				sort();
			}
		};
		for(ScoreColumn combinedColumn : combinedColumns) {
			combinedColumn.addPropertyChangeListener(ScoreColumn.PROP_WEIGHT, sortOnChange);
		}
		for (ScoreColumn col : getVisibleColumns()) {
			final PropertyChangeListener filterOnChange = new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					filter((ScoreColumn) evt.getSource());
					sort();
				}
			};
			col.addPropertyChangeListener(ScoreColumn.PROP_SELECTION, filterOnChange);
		}
		for (ScoreColumn col : getVisibleColumns()) {
			filter(col);
		}
		sort();
	}

	/**
	 * @param source
	 */
	protected void filter(ScoreColumn col) {
		ScoreColumnData d = data.get(col);
		d.selected.clear();
		for (int i = 0; i < rows.size(); ++i) {
			d.selected.set(i, col.isSelected(d.raw.get(i), d.normalized.get(i)));
		}
	}

	public ScoreColumn getColumn(int i) {
		if (i < 0)
			return null;
		Pair<List<ScoreColumn>, Integer> p = listFor(i);
		return p.getFirst().get(i - p.getSecond());
	}

	private Pair<List<ScoreColumn>, Integer> listFor(int i) {
		final int bak = i;
		if (i < combinedColumns.size())
			return Pair.make(combinedColumns, 0);
		i -= combinedColumns.size();
		if (i < extraColumns.size())
			return Pair.make(extraColumns, bak - i);
		i -= extraColumns.size();
		return Pair.make(hiddenColumns, bak - i);
	}

	public int moveColumn(int index, ScoreColumn column) {
		if (index < 0)
			return -1;
		int col_index = indexOf(column);
		if (col_index == index) // same position
			return -1;

		Pair<List<ScoreColumn>, Integer> source = listFor(col_index);
		Pair<List<ScoreColumn>, Integer> target = listFor(index);

		if (target.getFirst().equals(source.getFirst())) { //same list special case
			if (index < col_index) { // move to front
				source.getFirst().remove(col_index - source.getSecond());
				target.getFirst().add(index - target.getSecond(), column);
			} else { // add first and remove afterwards
				target.getFirst().add(index - target.getSecond(), column);
				source.getFirst().remove(col_index - source.getSecond());
			}
		} else {
			target.getFirst().add(index-target.getSecond(),column);
			source.getFirst().remove(col_index - source.getSecond());
		}
		if (source.getFirst() == combinedColumns || target.getFirst() == combinedColumns)
			this.sort();
		// FIXME fire listeners
		return col_index;
	}

	private int indexOf(ScoreColumn column) {
		int offset = 0;
		int i = combinedColumns.indexOf(column);
		if (i >= 0)
			return i + offset;
		offset += combinedColumns.size();

		i = extraColumns.indexOf(column);
		if (i >= 0)
			return i + offset;
		offset += extraColumns.size();

		i = hiddenColumns.indexOf(column);
		if (i >= 0)
			return i + offset;
		return -1;
	}

	protected void sort() {
		System.out.println("sort");
		List<Pair<Integer, Float>> tmp = new ArrayList<>(rows.size());

		BitSet valid = new BitSet(rows.size());
		valid.set(0, rows.size());
		for (ScoreColumn col : getVisibleColumns()) {
			valid.and(data.get(col).selected);
		}

		for (int i = 0; i < rows.size(); ++i) {
			if (!valid.get(i))
				continue;
			float s = 0;
			for (ScoreColumn col : this.combinedColumns) {
				s += col.weight(data.get(col).normalized.get(i).asFloat());
			}
			tmp.add(Pair.make(i, -s));
		}
		Collections.sort(tmp, Pair.<Float> compareSecond());
		int[] bak = order;
		order = new int[tmp.size()];
		for (int i = 0; i < tmp.size(); ++i) {
			order[i] = tmp.get(i).getFirst();
		}
		cache.clear();
		propertySupport.firePropertyChange(PROP_ORDER, bak, order);
	}


	public void setSelectedRow(int r) {
		if (selectedRow == r)
			return;
		propertySupport.firePropertyChange(PROP_SELECTED_ROW, this.selectedRow, this.selectedRow = r);
	}

	public void setSelectedCol(int c) {
		if (selectedCol == c)
			return;
		propertySupport.firePropertyChange(PROP_SELECTED_COL, this.selectedCol, this.selectedCol = c);
	}

	public void select(int r, ScoreColumn column) {
		setSelectedRow(r);
	}

	/**
	 * @return the selectedCol, see {@link #selectedCol}
	 */
	public int getSelectedCol() {
		return selectedCol;
	}

	/**
	 * @return the selectedRow, see {@link #selectedRow}
	 */
	public int getSelectedRow() {
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

	public IValue getNormalized(ScoreColumn col, int row) {
		return data.get(col).normalized.get(order[row]);
	}

	public IValue getRaw(ScoreColumn column, int row) {
		return data.get(column).normalized.get(order[row]);
	}

	public String getLabel(int row) {
		return (row + 1) + ". " + rows.get(order[row]);
	}

	public Iterator<IValue> getNormalizedCol(ScoreColumn col) {
		final List<IValue> container = data.get(col).normalized;
		return new Iterator<IValue>() {
			private int cursor = 0;
			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

			@Override
			public IValue next() {
				int i = cursor;
				IValue next = container.get(order[i]);
				cursor = i + 1;
				return next;
			}

			@Override
			public boolean hasNext() {
				return cursor != getNumRows();
			}
		};
	}


	private Iterator<IValue> getNormalizedCombinedCol() {
		return new Iterator<IValue>() {
			private int cursor = 0;

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

			@Override
			public IValue next() {
				return Values.of(getNormalizedCombined(cursor++));
			}

			@Override
			public boolean hasNext() {
				return cursor != getNumRows();
			}
		};
	}

	private float getNormalizedCombined(int row) {
		float s = 0;
		float wsum = 0;
		for (ScoreColumn col : this.combinedColumns) {
			s += col.weight(getNormalized(col, row).asFloat());
			wsum += col.getWeight();
		}
		return s / wsum;
	}

	public Iterator<IValue> getNormalizedRow(final int row) {
		return new Iterator<IValue>() {
			private Iterator<ScoreColumn> it = getVisibleColumns().iterator();

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

			@Override
			public IValue next() {
				return getNormalized(it.next(), row);
			}

			@Override
			public boolean hasNext() {
				return it.hasNext();
			}

		};
	}

	/**
	 * @return
	 */
	public int getNumVisibleColumns() {
		return combinedColumns.size() + extraColumns.size();
	}

	public Iterable<ScoreColumn> getVisibleColumns() {
		return Iterables.concat(this.combinedColumns, this.extraColumns);
	}

	/**
	 * @return
	 */
	public int getNumRows() {
		return order.length;
	}

	/**
	 * @return the combinedColumns, see {@link #combinedColumns}
	 */
	public List<ScoreColumn> getCombinedColumns() {
		return combinedColumns;
	}

	/**
	 * @return the extraColumns, see {@link #extraColumns}
	 */
	public List<ScoreColumn> getExtraColumns() {
		return extraColumns;
	}

	public Color getCombinedColor() {
		return new Color(200, 200, 200);
	}

	public Color getCombinedBackgroundColor() {
		return new Color(240, 240, 240);
	}

	public Histogram getCombinedHist(int bins) {
		String key = "chist" + bins;
		Histogram h = (Histogram) cache.get(key);
		if (h != null)
			return h;
		h = DataUtils.getHist(bins, getNormalizedCombinedCol());
		cache.put(key, h);
		return h;
	}

	public int getCombinedHistBin(int bins, int row) {
		return DataUtils.getHistBin(bins, getNormalizedCombined(row));
	}

	public static ScoreTable demo() {
		Random r = new Random(100);
		int rowCount = 5000;
		ScoreTable t = new ScoreTable();
		ScoreColumn c = new ScoreColumn(t, Color.decode("#ffb380"), Color.decode("#ffe6d5"), "Column 1");
		t.combinedColumns.add(c);

		List<IValue> tmp = randomContainer(r, rowCount, 2, 0.f, 1.f);
		t.data.put(c, new ScoreColumnData(tmp, tmp));

		c = new ScoreColumn(t, Color.decode("#80ffb3"), Color.decode("#e3f4d7"), "Column 2");
		t.combinedColumns.add(c);
		tmp = randomContainer(r, rowCount, 1, 0.f, 1.f);
		t.data.put(c, new ScoreColumnData(tmp, tmp));

		c = new ScoreColumn(t, Color.decode("#5fd3bc"), Color.decode("#d5fff6"), "Column 3");
		t.extraColumns.add(c);
		tmp = randomContainer(r, rowCount, 1, 0.f, 1.f);
		t.data.put(c, new ScoreColumnData(tmp, tmp));

		for (int i = 0; i < rowCount; ++i) {
			t.rows.add("Item " + Integer.toString(i, Character.MAX_RADIX));
		}
		t.initData();
		return t;
	}

	private static List<IValue> randomContainer(Random r, int count, int cols, float min, float max) {
		List<IValue> out = new ArrayList<>(count);
		for (int i = 0; i < count; ++i) {
			if (cols == 1)
				out.add(Values.of(r.nextFloat() * (max - min) + min));
			else {
				float[] vs = new float[cols];
				for (int j = 0; j < cols; ++j)
					vs[j] = (r.nextFloat() * (max - min) + min);
				out.add(Values.max(vs));
			}
		}
		return out;
	}

}
