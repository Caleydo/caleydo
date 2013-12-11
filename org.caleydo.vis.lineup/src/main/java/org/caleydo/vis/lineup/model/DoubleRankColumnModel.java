/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.model;

import gleem.linalg.Vec2f;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.util.base.ICallback;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.format.Formatter;
import org.caleydo.core.util.function.DoubleStatistics;
import org.caleydo.core.util.function.IDoubleSizedIterable;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.ISWTLayer.ISWTLayerRunnable;
import org.caleydo.core.view.opengl.layout2.geom.Rect;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.vis.lineup.config.IRankTableUIConfig;
import org.caleydo.vis.lineup.data.IDoubleFunction;
import org.caleydo.vis.lineup.data.IDoubleInferrer;
import org.caleydo.vis.lineup.event.FilterEvent;
import org.caleydo.vis.lineup.internal.ui.FloatFilterDialog;
import org.caleydo.vis.lineup.internal.ui.FloatFilterDialog.FilterChecked;
import org.caleydo.vis.lineup.model.mapping.IMappingFunction;
import org.caleydo.vis.lineup.model.mapping.PiecewiseMapping;
import org.caleydo.vis.lineup.model.mixin.IDoubleRankableColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IFilterColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IMappedColumnMixin;
import org.caleydo.vis.lineup.model.mixin.ISetableColumnMixin;
import org.caleydo.vis.lineup.model.mixin.ISnapshotableColumnMixin;
import org.caleydo.vis.lineup.ui.detail.ScoreBarElement;
import org.caleydo.vis.lineup.ui.detail.ScoreSummary;
import org.caleydo.vis.lineup.ui.detail.ValueElement;
import org.caleydo.vis.lineup.ui.mapping.MappingFunctionUIs;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * @author Samuel Gratzl
 *
 */
public class DoubleRankColumnModel extends ABasicFilterableRankColumnModel implements IMappedColumnMixin,
		IDoubleRankableColumnMixin, ISetableColumnMixin, ISnapshotableColumnMixin {

	private final IMappingFunction mapping;
	private final IDoubleInferrer missingValueInferer;

	private final NumberFormat formatter;

	protected final IDoubleFunction<IRow> data;
	private final Map<IRow, Double> valueOverrides = new HashMap<>(3);
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
			missingValue = Double.NaN;
		}
	};

	private final HistCache cacheHist = new HistCache();
	private boolean dirtyDataStats = true;
	private double missingValue = Double.NaN;

	private boolean filterNotMappedEntries = true;
	private boolean filterMissingEntries = false;

	public DoubleRankColumnModel(IDoubleFunction<IRow> data, IGLRenderer header, Color color, Color bgColor,
			PiecewiseMapping mapping, IDoubleInferrer missingValue) {
		this(data, header, color, bgColor, mapping, missingValue, null);
	}

	public DoubleRankColumnModel(IDoubleFunction<IRow> data, IGLRenderer header, Color color, Color bgColor,
			PiecewiseMapping mapping, IDoubleInferrer missingValue, NumberFormat formatter) {
		super(color, bgColor);
		this.data = data;
		this.mapping = mapping;
		this.missingValueInferer = missingValue;
		this.formatter = formatter;
		setHeaderRenderer(header);
	}

	public DoubleRankColumnModel(DoubleRankColumnModel copy) {
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
	public DoubleRankColumnModel clone() {
		return new DoubleRankColumnModel(this);
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
	public void editMapping(GLElement summary, IGLElementContext context, IRankTableUIConfig config) {
		GLElement m = MappingFunctionUIs.create(mapping, asRawData(), getColor(), new Color(0.95f, .95f, .95f),
				callback, config);
		m.setzDelta(0.5f);
		Vec2f location = summary.getAbsoluteLocation();
		Vec2f size = summary.getSize();
		context.getPopupLayer().show(m, new Rect(location.x(), location.y() + size.y(), 260, 300));
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

	private IDoubleSizedIterable asRawData() {
		return getTable().getFilteredMappedData(new org.caleydo.vis.lineup.data.ADoubleFunction<IRow>() {
			@Override
			public double applyPrimitive(IRow row) {
				return getRaw(row);
			}
		});
	}

	private double computeMissingValue() {
		if (Double.isNaN(missingValue)) {
			IDoubleSizedIterable list = asRawData();
			missingValue = missingValueInferer.infer(list.iterator());
		}
		return missingValue;
	}

	private void checkMapping() {
		if (dirtyDataStats) {
			DoubleStatistics stats = DoubleStatistics.of(asRawData().iterator());
			mapping.setActStatistics(stats);
			dirtyDataStats = false;
			invalidAllFilter();
		}
	}

	@Override
	public Double apply(IRow row) {
		return applyPrimitive(row);
	}

	@Override
	public double applyPrimitive(IRow row) {
		double value = getRaw(row);
		if (Double.isNaN(value))
			value = computeMissingValue();
		checkMapping();
		return mapping.apply(value);
	}

	protected double getRaw(IRow row) {
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
			valueOverrides.put(row, Double.NaN);
		} else {
			try {
				valueOverrides.put(row, Double.parseDouble(value));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		cacheHist.invalidate();
		invalidAllFilter();
		dirtyDataStats = true;
		missingValue = Double.NaN;
		propertySupport.firePropertyChange(PROP_MAPPING, null, data);
	}

	@Override
	public String getValue(IRow row) {
		return getRawValue(row);
	}

	@Override
	public int compare(IRow o1, IRow o2) {
		return Double.compare(applyPrimitive(o1), applyPrimitive(o2));
	}

	@Override
	public void orderByMe() {
		parent.orderBy(this);
	}

	@Override
	public boolean isValueInferred(IRow row) {
		return Double.isNaN(getRaw(row));
	}

	private String format(double value) {
		if (Double.isNaN(value))
			value = computeMissingValue();
		if (Double.isNaN(value))
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
	 * @return the filterMissingEntries, see {@link #filterMissingEntries}
	 */
	public boolean isFilterMissingEntries() {
		return filterMissingEntries;
	}

	/**
	 * @return the filterNotMappedEntries, see {@link #filterNotMappedEntries}
	 */
	public boolean isFilterNotMappedEntries() {
		return filterNotMappedEntries;
	}

	/**
	 * @param c
	 * @param filterGlobally
	 * @param filterNotMappedEntries
	 *            setter, see {@link filterNotMappedEntries}
	 */
	public void setFilter(FloatFilterDialog.FilterChecked checked, boolean filterGlobally,
			boolean filterRankIndependently) {
		if (this.filterNotMappedEntries == checked.isFilterNotMapped()
				&& this.filterMissingEntries == checked.isFilterMissing()
 && this.isGlobalFilter == filterGlobally
				&& this.isRankIndependentFilter == filterRankIndependently)
			return;
		FilterChecked bak = new FilterChecked(filterNotMappedEntries, filterMissingEntries);

		this.filterMissingEntries = checked.isFilterMissing();
		this.filterNotMappedEntries = checked.isFilterNotMapped();
		this.isGlobalFilter = filterGlobally;
		this.isRankIndependentFilter = filterRankIndependently;
		invalidAllFilter();
		cacheHist.invalidate();
		propertySupport.firePropertyChange(IFilterColumnMixin.PROP_FILTER, bak, checked);
	}

	public void setFilter(boolean filterNotMappedEntries, boolean filterMissingEntries, boolean isGlobalFilter,
			boolean filterRankIndependently) {
		setFilter(new FilterChecked(filterNotMappedEntries, filterMissingEntries), isGlobalFilter,
				filterRankIndependently);
	}

	@Override
	public void editFilter(final GLElement summary, IGLElementContext context) {
		final Vec2f location = summary.getAbsoluteLocation();
		context.getSWTLayer().run(new ISWTLayerRunnable() {
			@Override
			public void run(Display display, Composite canvas) {
				Point loc = canvas.toDisplay((int) location.x(), (int) location.y());
				FloatFilterDialog dialog = new FloatFilterDialog(canvas.getShell(), getLabel(), summary,
						filterNotMappedEntries, filterMissingEntries, DoubleRankColumnModel.this, getTable()
								.hasSnapshots(), loc);
				dialog.open();
			}
		});
	}

	@Override
	protected void updateMask(BitSet todo, List<IRow> rows, BitSet mask) {
		for (int i = todo.nextSetBit(0); i >= 0; i = todo.nextSetBit(i + 1)) {
			double v = getRaw(rows.get(i));
			if (filterMissingEntries && Double.isNaN(v)) {
				mask.set(i, false);
				continue;
			}
			if (Double.isNaN(v))
				v = computeMissingValue();

			if (!filterNotMappedEntries || Double.isNaN(v)) {
				mask.set(i, true);
				continue;
			}

			checkMapping();
			double f = mapping.apply(v);
			mask.set(i, !Double.isNaN(f));
		}
	}

	static class MyScoreSummary extends ScoreSummary {
		public MyScoreSummary(IDoubleRankableColumnMixin model, boolean interactive) {
			super(model, interactive);
		}

		@ListenTo(sendToMe = true)
		private void onFilterChanged(FilterEvent event) {
			FilterChecked f = (FilterChecked) event.getFilter();
			((DoubleRankColumnModel) model).setFilter(f, event.isFilterGlobally(), event.isFilterRankIndendent());
		}

	}
}
