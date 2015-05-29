/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.vis.lineup.model;

import gleem.linalg.Vec2f;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.List;

import org.caleydo.core.util.base.ICallback;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.function.DoubleStatistics;
import org.caleydo.core.util.function.IDoubleSizedIterable;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.geom.Rect;
import org.caleydo.vis.lineup.config.IRankTableUIConfig;
import org.caleydo.vis.lineup.model.mapping.IMappingFunction;
import org.caleydo.vis.lineup.model.mapping.PiecewiseMapping;
import org.caleydo.vis.lineup.model.mixin.IDoubleRankableColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IExplodeableColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IFilterColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IHideableColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IMappedColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IRankColumnModel;
import org.caleydo.vis.lineup.model.mixin.IRankableColumnMixin;
import org.caleydo.vis.lineup.ui.detail.ScoreBarElement;
import org.caleydo.vis.lineup.ui.detail.ScoreSummary;
import org.caleydo.vis.lineup.ui.detail.ValueElement;
import org.caleydo.vis.lineup.ui.mapping.MappingFunctionUIs;

import com.google.common.collect.Iterables;
import com.jogamp.common.util.IntIntHashMap;

/**
 * specialized container the represents the delta of two rankings
 *
 * @author Samuel Gratzl
 *
 */
public final class RankDeltaRankColumnModel extends ACompositeRankColumnModel implements Cloneable, IMappedColumnMixin,
		IExplodeableColumnMixin, IHideableColumnMixin, IDoubleRankableColumnMixin {

	private final IMappingFunction mapping;

	private final ICallback<IMappingFunction> callback = new ICallback<IMappingFunction>() {
		@Override
		public void on(IMappingFunction data) {
			cacheHist.invalidate();
			propertySupport.firePropertyChange(PROP_MAPPING, null, data);
		}
	};
	private final PropertyChangeListener dataListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			dirtyDataStats = true;
		}
	};
	private final PropertyChangeListener listener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			switch (evt.getPropertyName()) {
			case IFilterColumnMixin.PROP_FILTER:
			case IMappedColumnMixin.PROP_MAPPING:
				deltas = null;
				propertySupport.firePropertyChange(evt);
				break;
			}
		}
	};
	private IntIntHashMap deltas;
	private final HistCache cacheHist = new HistCache();
	private boolean dirtyDataStats = true;

	public RankDeltaRankColumnModel() {
		this(Color.GRAY, new Color(0.95f, .95f, .95f));
	}

	public RankDeltaRankColumnModel(Color color, Color bgColor) {
		this(color, bgColor, createAbsMapping());
	}

	public RankDeltaRankColumnModel(Color color, Color bgColor, PiecewiseMapping mapping) {
		super(color, bgColor);
		this.mapping = mapping;
	}

	/**
	 * @return
	 */
	private static PiecewiseMapping createAbsMapping() {
		PiecewiseMapping m = new PiecewiseMapping(Double.NaN, Double.NaN);
		return m;
	}

	public RankDeltaRankColumnModel(RankDeltaRankColumnModel copy) {
		super(copy);
		cloneInitChildren();
		this.deltas = copy.deltas;
		this.mapping = copy.mapping.clone();
		this.dirtyDataStats = copy.dirtyDataStats;
	}

	@Override
	public RankDeltaRankColumnModel clone() {
		return new RankDeltaRankColumnModel(this);
	}

	@Override
	public boolean canAdd(ARankColumnModel model) {
		return super.canAdd(model) && size() < 2 && model instanceof IRankableColumnMixin;
	}

	@Override
	protected void init(ARankColumnModel model) {
		super.init(model);
		model.addPropertyChangeListener(IFilterColumnMixin.PROP_FILTER, listener);
		model.addPropertyChangeListener(IMappedColumnMixin.PROP_MAPPING, listener);
	}

	@Override
	protected void takeDown(ARankColumnModel model) {
		super.takeDown(model);
		model.removePropertyChangeListener(IFilterColumnMixin.PROP_FILTER, listener);
		model.removePropertyChangeListener(IMappedColumnMixin.PROP_MAPPING, listener);
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

	@Override
	public ColumnRanker getMyRanker(IRankColumnModel model) {
		return parent.getMyRanker(this);
	}

	@Override
	public void orderBy(IRankableColumnMixin model) {
		// can't sort by child
	}

	private IDoubleSizedIterable asRawData() {
		return getTable().getFilteredMappedData(new org.caleydo.vis.lineup.data.ADoubleFunction<IRow>() {
			@Override
			public double applyPrimitive(IRow row) {
				return getRaw(row);
			}
		});
	}

	@Override
	public boolean isFlatAdding(ACompositeRankColumnModel model) {
		return false;
	}

	private void checkMapping() {
		if (dirtyDataStats) {
			DoubleStatistics stats = DoubleStatistics.of(asRawData().iterator());
			mapping.setActStatistics(stats);
			dirtyDataStats = false;
		}
	}

	@Override
	public double applyPrimitive(IRow row) {
		double value = getRaw(row);
		checkMapping();
		return mapping.apply(value);
	}

	protected double getRaw(IRow row) {
		return getDelta(row);
	}

	@Override
	public String getValue(IRow row) {
		int delta = getDelta(row);
		return String.valueOf(delta);
	}

	private int getDelta(IRow row) {
		if (deltas == null)
			deltas = computeDeltas();
		return deltas.get(row.getIndex());
	}

	/**
	 * @return
	 */
	private IntIntHashMap computeDeltas() {
		IntIntHashMap m = new IntIntHashMap();
		m.setKeyNotFoundValue(0); // 0 delta on unknown values
		if (this.size() != 2)
			return m;
		final IRankableColumnMixin a = (IRankableColumnMixin)this.get(0);
		final IRankableColumnMixin b = (IRankableColumnMixin)this.get(1);
		Helper[] h = toHelperData();
		Arrays.sort(h, new ComparatorAdapter(a));
		for (int i = 0; i < h.length; ++i) { // save rank
			h[i].rank1 = i;
		}
		Arrays.sort(h, new ComparatorAdapter(b));
		for (int i = 0; i < h.length; ++i) {
			final Helper hi = h[i];
			int index = hi.value.getIndex();
			int rank1 = hi.rank1;
			int rank2 = i;
			int delta = rank1 - rank2;
			if (delta != 0)
				m.put(index, delta);
		}
		return m;
	}

	private Helper[] toHelperData() {
		BitSet filter = getMyRanker().getFilter();
		Helper[] h = new Helper[filter.cardinality()];
		final List<IRow> data = getTable().getData();
		int j = 0;
		for(int i = 0; i < data.size(); ++i) {
			if (!filter.get(i))
				continue;
			h[j++] = new Helper(data.get(i));
		}
		h = Arrays.copyOf(h,j);
		return h;
	}

	/**
	 * @author Samuel Gratzl
	 *
	 */
	private static final class ComparatorAdapter implements Comparator<Helper> {
		private final IRankableColumnMixin a;
		private ComparatorAdapter(IRankableColumnMixin a) {
			this.a = a;
		}
		@Override
		public int compare(Helper o1, Helper o2) {
			return a.compare(o1.value, o2.value);
		}
	}

	private static class Helper {
		final IRow value;
		int rank1;

		public Helper(IRow value) {
			this.value = value;
		}

	}

	@Override
	public int compare(IRow o1, IRow o2) {
		return Integer.compare(getDelta(o1), getDelta(o2));
	}

	@Override
	public void orderByMe() {
		parent.orderBy(this);
	}

	@Override
	public GLElement createSummary(boolean interactive) {
		return new ScoreSummary(this, interactive);
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
	public String getRawValue(IRow row) {
		return getValue(row);
	}

	@Override
	public boolean isComplexMapping() {
		return mapping.isComplexMapping();
	}


	@Override
	public Double apply(IRow input) {
		return applyPrimitive(input);
	}

	@Override
	public boolean isValueInferred(IRow row) {
		for (IDoubleRankableColumnMixin r : Iterables.filter(this, IDoubleRankableColumnMixin.class))
			if (r.isValueInferred(row))
				return true;
		return false;
	}

	@Override
	public SimpleHistogram getHist(float width) {
		return cacheHist.get(width, getMyRanker(), this);
	}

	@Override
	public void explode() {
		getParent().explode(this);
	}

}
