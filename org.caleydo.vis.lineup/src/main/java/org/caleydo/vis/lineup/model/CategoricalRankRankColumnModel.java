/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.model;

import gleem.linalg.Vec2f;

import java.beans.PropertyChangeListener;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.util.base.ICallback;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.IGLElementParent;
import org.caleydo.core.view.opengl.layout2.ISWTLayer.ISWTLayerRunnable;
import org.caleydo.core.view.opengl.layout2.geom.Rect;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.vis.lineup.config.IRankTableUIConfig;
import org.caleydo.vis.lineup.event.FilterEvent;
import org.caleydo.vis.lineup.internal.ui.CatFilterDalog;
import org.caleydo.vis.lineup.model.mapping.ICategoricalMappingFunction;
import org.caleydo.vis.lineup.model.mixin.IDoubleRankableColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IFilterColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IMappedColumnMixin;
import org.caleydo.vis.lineup.ui.GLPropertyChangeListeners;
import org.caleydo.vis.lineup.ui.IColumnRenderInfo;
import org.caleydo.vis.lineup.ui.RenderUtils;
import org.caleydo.vis.lineup.ui.detail.CategoricalScoreBarElement;
import org.caleydo.vis.lineup.ui.detail.ValueElement;
import org.caleydo.vis.lineup.ui.mapping.MappingFunctionUIs;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.google.common.base.Function;

/**
 * a special categorical column with at most {@link #MAX_CATEGORY_COLORS} colors which supports that this column is part
 * of the ranking
 *
 * @author Samuel Gratzl
 *
 */
public final class CategoricalRankRankColumnModel<CATEGORY_TYPE> extends ABasicFilterableRankColumnModel implements
		IFilterColumnMixin, IMappedColumnMixin, IDoubleRankableColumnMixin, Cloneable {
	private static final int MAX_CATEGORY_COLORS = 8;

	private final Function<IRow, CATEGORY_TYPE> data;
	private final Set<CATEGORY_TYPE> selection = new HashSet<>();
	private final Map<CATEGORY_TYPE, CategoryInfo> metaData;
	private final ICategoricalMappingFunction<CATEGORY_TYPE> mapping;

	private final HistCache cacheHist = new HistCache();
	private Map<CATEGORY_TYPE, Integer> cacheValueHist = null;

	private final ICallback<Object> callback = new ICallback<Object>() {
		@Override
		public void on(Object data) {
			cacheHist.invalidate();
			cacheValueHist = null;
			invalidAllFilter();
			propertySupport.firePropertyChange(PROP_MAPPING, null, data);
		}
	};

	public CategoricalRankRankColumnModel(IGLRenderer header, final Function<IRow, CATEGORY_TYPE> data,
			Map<CATEGORY_TYPE, CategoryInfo> metaData, ICategoricalMappingFunction<CATEGORY_TYPE> mapping) {
		super(Color.GRAY, new Color(.95f, .95f, .95f));
		setHeaderRenderer(header);
		this.data = data;
		this.metaData = metaData;
		this.selection.addAll(metaData.keySet());
		assert metaData.size() <= MAX_CATEGORY_COLORS;
		this.mapping = mapping;
	}

	public CategoricalRankRankColumnModel(CategoricalRankRankColumnModel<CATEGORY_TYPE> copy) {
		super(copy);
		setHeaderRenderer(getHeaderRenderer());
		this.data = copy.data;
		this.metaData = copy.metaData;
		this.selection.addAll(copy.selection);
		this.mapping = copy.mapping.clone();
	}

	@Override
	public CategoricalRankRankColumnModel<CATEGORY_TYPE> clone() {
		return new CategoricalRankRankColumnModel<>(this);
	}

	@Override
	public boolean isComplexMapping() {
		return mapping.isComplexMapping();
	}

	@Override
	public void onRankingInvalid() {
		cacheHist.invalidate();
		cacheValueHist = null;
	}

	@Override
	public String getValue(IRow row) {
		CATEGORY_TYPE value = getCatValue(row);
		if (value == null)
			return "";
		return metaData.get(value).getLabel();
	}

	@Override
	public GLElement createSummary(boolean interactive) {
		return new MyElement(interactive);
	}

	@Override
	public ValueElement createValue() {
		return new CategoricalScoreBarElement(this);
	}

	@Override
	public final void editFilter(final GLElement summary, IGLElementContext context) {
		final Vec2f location = summary.getAbsoluteLocation();
		context.getSWTLayer().run(new ISWTLayerRunnable() {
			@Override
			public void run(Display display, Composite canvas) {
				Point loc = canvas.toDisplay((int) location.x(), (int) location.y());
				CatFilterDalog<CATEGORY_TYPE> dialog = new CatFilterDalog<>(canvas.getShell(), getLabel(), summary,
						metaData, selection, CategoricalRankRankColumnModel.this, getTable().hasSnapshots(), loc,
						false, "");
				dialog.open();
			}
		});
	}

	protected void setFilter(Collection<CATEGORY_TYPE> filter, boolean isGlobalFilter, boolean isRankIndependentFilter) {
		if (filter.equals(this.selection) && this.isGlobalFilter == isGlobalFilter
				&& this.isRankIndependentFilter == isRankIndependentFilter)
			return;
		invalidAllFilter();
		this.selection.clear();
		this.selection.addAll(filter);
		this.isGlobalFilter = isGlobalFilter;
		this.isRankIndependentFilter = isRankIndependentFilter;
		propertySupport.firePropertyChange(PROP_FILTER, false, true);
	}

	@Override
	public boolean isFiltered() {
		return selection.size() < metaData.size();
	}

	public CATEGORY_TYPE getCatValue(IRow row) {
		return data.apply(row);
	}

	@Override
	public double applyPrimitive(IRow in) {
		return mapping.applyPrimitive(getCatValue(in));
	}

	@Override
	public Double apply(IRow in) {
		return applyPrimitive(in);
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
	public SimpleHistogram getHist(float width) {
		return cacheHist.get(width, getMyRanker(), this);
	}

	public Map<CATEGORY_TYPE, Integer> getHist() {
		if (cacheValueHist != null)
			return cacheValueHist;
		Map<CATEGORY_TYPE, Integer> hist = new HashMap<>();
		for (IRow r : getMyRanker()) {
			CATEGORY_TYPE v = getCatValue(r);
			if (v == null) // TODO nan
				continue;
			Integer c = hist.get(v);
			if (c == null)
				hist.put(v, 1);
			else
				hist.put(v, c + 1);
		}
		return cacheValueHist = hist;
	}

	@Override
	public String getRawValue(IRow row) {
		CATEGORY_TYPE t = getCatValue(row);
		if (t == null)
			return "";
		CategoryInfo info = metaData.get(t);
		if (info == null)
			return "";
		return info.getLabel();
	}

	/**
	 * @param r
	 */
	public Color getColor(IRow row) {
		CATEGORY_TYPE t = getCatValue(row);
		if (t == null)
			return color;
		CategoryInfo info = metaData.get(t);
		if (info == null)
			return color;
		return info.getColor();
	}

	@Override
	public boolean isValueInferred(IRow row) {
		return getCatValue(row) == null;
	}

	@Override
	protected void updateMask(BitSet todo, List<IRow> data, BitSet mask) {
		for (int i = todo.nextSetBit(0); i >= 0; i = todo.nextSetBit(i + 1)) {
			CATEGORY_TYPE v = this.data.apply(data.get(i));
			mask.set(i, selection.contains(v));
		}
	}

	private class MyElement extends GLElement {
		private final PropertyChangeListener repaintListner = GLPropertyChangeListeners.repaintOnEvent(this);

		public MyElement(boolean interactive) {
			setzDelta(0.25f);
			if (!interactive)
				setVisibility(EVisibility.VISIBLE);
		}

		@Override
		protected void init(IGLElementContext context) {
			super.init(context);
			addPropertyChangeListener(PROP_FILTER, repaintListner);
		}

		@Override
		protected void takeDown() {
			removePropertyChangeListener(PROP_FILTER, repaintListner);
			super.takeDown();
		}

		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			super.renderImpl(g, w, h);
			if (((IColumnRenderInfo) getParent()).isCollapsed())
				return;
			Map<CATEGORY_TYPE, Integer> hist = getHist();
			IRow s = getTable().getSelectedRow();
			CATEGORY_TYPE selected = s == null ? null : getCatValue(s);
			RenderUtils.renderHist(g, hist, w, h, selected, metaData, findRenderInfo().getBarOutlineColor());
		}

		/**
		 * @return
		 */
		private IColumnRenderInfo findRenderInfo() {
			IGLElementParent p = getParent();
			while (!(p instanceof IColumnRenderInfo) && p != null)
				p = p.getParent();
			return (IColumnRenderInfo) p;
		}

		@SuppressWarnings("unchecked")
		@ListenTo(sendToMe = true)
		private void onSetFilter(FilterEvent event) {
			setFilter((Collection<CATEGORY_TYPE>) event.getFilter(), event.isFilterGlobally(),
					event.isFilterRankIndendent());
		}

	}

	public static class CategoryInfo {
		private final String label;
		private final Color color;

		public CategoryInfo(String label, Color color) {
			super();
			this.label = label;
			this.color = color;
		}

		/**
		 * @return the color, see {@link #color}
		 */
		public Color getColor() {
			return color;
		}

		/**
		 * @return the label, see {@link #label}
		 */
		public String getLabel() {
			return label;
		}

		@Override
		public String toString() {
			return label;
		}
	}

	@Override
	public void editMapping(GLElement summary, IGLElementContext context, IRankTableUIConfig config) {
		GLElement m = MappingFunctionUIs.create(mapping, getHist(), metaData, color, bgColor, callback, config);
		m.setzDelta(0.5f);
		Vec2f location = summary.getAbsoluteLocation();
		Vec2f size = summary.getSize();
		context.getPopupLayer().show(m, new Rect(location.x(), location.y() + size.y(), 260, 260));
	}
}
