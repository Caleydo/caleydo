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

import gleem.linalg.Vec2f;
import gleem.linalg.Vec4f;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.io.gui.dataimport.widget.ICallback;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.format.Formatter;
import org.caleydo.core.util.function.AFloatList;
import org.caleydo.core.util.function.FloatStatistics;
import org.caleydo.core.util.function.IFloatList;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.vis.rank.data.IFloatFunction;
import org.caleydo.vis.rank.data.IFloatInferrer;
import org.caleydo.vis.rank.internal.event.FilterEvent;
import org.caleydo.vis.rank.internal.ui.FloatFilterDialog;
import org.caleydo.vis.rank.model.mapping.IMappingFunction;
import org.caleydo.vis.rank.model.mapping.PiecewiseMapping;
import org.caleydo.vis.rank.model.mixin.IFilterColumnMixin;
import org.caleydo.vis.rank.model.mixin.IFloatRankableColumnMixin;
import org.caleydo.vis.rank.model.mixin.IMappedColumnMixin;
import org.caleydo.vis.rank.model.mixin.ISetableColumnMixin;
import org.caleydo.vis.rank.model.mixin.ISnapshotableColumnMixin;
import org.caleydo.vis.rank.ui.detail.ScoreBarElement;
import org.caleydo.vis.rank.ui.detail.ScoreSummary;
import org.caleydo.vis.rank.ui.detail.ValueElement;
import org.caleydo.vis.rank.ui.mapping.MappingFunctionUIs;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Samuel Gratzl
 *
 */
public class FloatRankColumnModel extends ABasicFilterableRankColumnModel implements IMappedColumnMixin,
		IFloatRankableColumnMixin, ISetableColumnMixin, ISnapshotableColumnMixin, IFilterColumnMixin {

	private final IMappingFunction mapping;
	private final IFloatInferrer missingValueInferer;

	private final NumberFormat formatter;

	private final IFloatFunction<IRow> data;
	private final Map<IRow, Float> valueOverrides = new HashMap<>(3);
	private final ICallback<IMappingFunction> callback = new ICallback<IMappingFunction>() {
		@Override
		public void on(IMappingFunction data) {
			cacheHist.invalidate();
			invalidAllFilter();
			propertySupport.firePropertyChange(PROP_MAPPING, null, data);
		}
	};
	private final PropertyChangeListener dataListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			dirtyDataStats = true;
			missingValue = Float.NaN;
		}
	};

	private final HistCache cacheHist = new HistCache();
	private boolean dirtyDataStats = true;
	private float missingValue = Float.NaN;

	private boolean filterNotMappedEntries = true;
	private boolean filterMissingEntries = false;

	public FloatRankColumnModel(IFloatFunction<IRow> data, IGLRenderer header, Color color, Color bgColor,
			PiecewiseMapping mapping, IFloatInferrer missingValue) {
		this(data, header, color, bgColor, mapping, missingValue, NumberFormat.getInstance(Locale.ENGLISH));
	}

	public FloatRankColumnModel(IFloatFunction<IRow> data, IGLRenderer header, Color color, Color bgColor,
			PiecewiseMapping mapping, IFloatInferrer missingValue, NumberFormat formatter) {
		super(color, bgColor);
		this.data = data;
		this.mapping = mapping;
		this.missingValueInferer = missingValue;
		this.formatter = formatter;
		setHeaderRenderer(header);
	}

	public FloatRankColumnModel(FloatRankColumnModel copy) {
		super(copy);
		this.data = copy.data;
		this.mapping = copy.mapping.clone();
		this.missingValueInferer = copy.missingValueInferer;
		setHeaderRenderer(copy.getHeaderRenderer());
		this.missingValue = copy.missingValue;
		this.dirtyDataStats = copy.dirtyDataStats;
		this.formatter = copy.formatter;
		this.valueOverrides.putAll(copy.valueOverrides);
		this.filterMissingEntries = copy.filterMissingEntries;
		this.filterNotMappedEntries = copy.filterNotMappedEntries;
	}

	@Override
	public FloatRankColumnModel clone() {
		return new FloatRankColumnModel(this);
	}

	@Override
	public boolean isComplexMapping() {
		return mapping.isComplexMapping();
	}

	@Override
	public void onRankingInvalid() {
		cacheHist.invalidate();
		super.onRankingInvalid();
	}

	@Override
	public GLElement createSummary(boolean interactive) {
		return new MyScoreSummary(this, interactive);
	}

	@Override
	public ValueElement createValue() {
		return new ScoreBarElement(this);
	}

	@Override
	public void editMapping(GLElement summary, IGLElementContext context) {
		GLElement m = MappingFunctionUIs.create(mapping, asRawData(), getColor(), getBgColor(), callback);
		m.setzDelta(0.5f);
		Vec2f location = summary.getAbsoluteLocation();
		Vec2f size = summary.getSize();
		context.getPopupLayer().show(m, new Vec4f(location.x(), location.y() + size.y(), 260, 300));
	}

	@Override
	protected void init(IRankColumnParent parent) {
		super.init(parent);
		final RankTableModel table = getTable();
		table.addPropertyChangeListener(RankTableModel.PROP_DATA, dataListener);
		table.addPropertyChangeListener(RankTableModel.PROP_DATA_MASK, dataListener);
		table.addPropertyChangeListener(RankTableModel.PROP_FILTER_INVALID, dataListener);
	}

	@Override
	protected void takeDown() {
		final RankTableModel table = getTable();
		table.removePropertyChangeListener(RankTableModel.PROP_DATA, dataListener);
		table.removePropertyChangeListener(RankTableModel.PROP_DATA_MASK, dataListener);
		table.removePropertyChangeListener(RankTableModel.PROP_FILTER_INVALID, dataListener);
		super.takeDown();
	}

	private IFloatList asRawData() {
		final List<IRow> data2 = getTable().getFilteredData();
		return new AFloatList() {
			@Override
			public float getPrimitive(int index) {
				return getRaw(data2.get(index));
			}

			@Override
			public int size() {
				return data2.size();
			}
		};
	}

	private float computeMissingValue() {
		if (Float.isNaN(missingValue)) {
			IFloatList list = asRawData();
			missingValue = missingValueInferer.infer(list.iterator(), list.size());
		}
		return missingValue;
	}

	private void checkMapping() {
		if (dirtyDataStats) {
			FloatStatistics stats = FloatStatistics.compute(asRawData().iterator());
			mapping.setActStatistics(stats);
			dirtyDataStats = false;
			invalidAllFilter();
		}
	}

	@Override
	public Float apply(IRow row) {
		return applyPrimitive(row);
	}

	@Override
	public float applyPrimitive(IRow row) {
		float value = getRaw(row);
		if (Float.isNaN(value))
			value = computeMissingValue();
		checkMapping();
		return mapping.apply(value);
	}

	protected float getRaw(IRow row) {
		if (valueOverrides.containsKey(row))
			return valueOverrides.get(row);
		return data.applyPrimitive(row);
	}

	@Override
	public boolean isOverriden(IRow row) {
		return valueOverrides.containsKey(row);
	}

	@Override
	public String getOriginalValue(IRow row) {
		return format(data.applyPrimitive(row));
	}

	@Override
	public void set(IRow row, String value) {
		if (value == null) {
			valueOverrides.remove(row);
		} else if (value.length() == 0) {
			valueOverrides.put(row, Float.NaN);
		} else {
			try {
				valueOverrides.put(row, Float.parseFloat(value));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		cacheHist.invalidate();
		invalidAllFilter();
		dirtyDataStats = true;
		missingValue = Float.NaN;
		propertySupport.firePropertyChange(PROP_MAPPING, null, data);
	}

	@Override
	public String getValue(IRow row) {
		return getRawValue(row);
	}

	@Override
	public int compare(IRow o1, IRow o2) {
		return Float.compare(applyPrimitive(o1), applyPrimitive(o2));
	}

	@Override
	public void orderByMe() {
		parent.orderBy(this);
	}

	@Override
	public boolean isValueInferred(IRow row) {
		return Float.isNaN(getRaw(row));
	}

	private String format(float value) {
		if (Float.isNaN(value))
			value = computeMissingValue();
		if (Float.isNaN(value))
			return "";
		if (formatter != null)
			return formatter.format(value);
		return Formatter.formatNumber(value);
	}

	@Override
	public String getRawValue(IRow row) {
		return format(getRaw(row));
	}

	@Override
	public SimpleHistogram getHist(float width) {
		return cacheHist.get(width, getMyRanker(), this);
	}

	@Override
	public boolean isFiltered() {
		return filterNotMappedEntries || filterMissingEntries;
	}

	/**
	 * @param filterNotMappedEntries
	 *            setter, see {@link filterNotMappedEntries}
	 */
	public void setFilter(boolean filterNotMappedEntries, boolean filterMissingEntries) {
		if (this.filterNotMappedEntries == filterNotMappedEntries && this.filterMissingEntries == filterMissingEntries)
			return;
		boolean bak = this.filterNotMappedEntries;
		boolean bak2 = this.filterMissingEntries;
		this.filterMissingEntries = filterMissingEntries;
		this.filterNotMappedEntries = filterNotMappedEntries;
		invalidAllFilter();
		cacheHist.invalidate();
		propertySupport.firePropertyChange(IFilterColumnMixin.PROP_FILTER, Pair.make(bak, bak2),
				Pair.make(filterNotMappedEntries, filterMissingEntries));
	}

	@Override
	public void editFilter(final GLElement summary, IGLElementContext context) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				new FloatFilterDialog(new Shell(), getTitle(), summary, filterNotMappedEntries, filterMissingEntries)
						.open();
			}
		});
	}

	@Override
	protected void updateMask(BitSet todo, List<IRow> rows, BitSet mask) {
		for (int i = todo.nextSetBit(0); i >= 0; i = todo.nextSetBit(i + 1)) {
			float v = getRaw(rows.get(i));
			if (filterMissingEntries && Float.isNaN(v)) {
				mask.set(i, false);
				continue;
			}
			if (!filterNotMappedEntries) {
				mask.set(i, true);
				continue;
			}

			if (Float.isNaN(v))
				v = computeMissingValue();
			checkMapping();
			float f = mapping.apply(v);
			mask.set(i, !Float.isNaN(f));
		}
	}

	static class MyScoreSummary extends ScoreSummary {
		public MyScoreSummary(IFloatRankableColumnMixin model, boolean interactive) {
			super(model, interactive);
		}

		@ListenTo(sendToMe = true)
		private void onFilterChanged(FilterEvent event) {
			@SuppressWarnings("unchecked")
			Pair<Boolean, Boolean> f = (Pair<Boolean, Boolean>) event.getFilter();
			((FloatRankColumnModel) model).setFilter(f.getFirst(), f.getSecond());
		}

	}
}
