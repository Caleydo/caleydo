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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.ISWTLayer.ISWTLayerRunnable;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.vis.lineup.event.FilterEvent;
import org.caleydo.vis.lineup.internal.ui.CatFilterDalog;
import org.caleydo.vis.lineup.model.mixin.IDataBasedColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IFilterColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IGrabRemainingHorizontalSpace;
import org.caleydo.vis.lineup.model.mixin.IRankableColumnMixin;
import org.caleydo.vis.lineup.ui.GLPropertyChangeListeners;
import org.caleydo.vis.lineup.ui.IColumnRenderInfo;
import org.caleydo.vis.lineup.ui.detail.ValueElement;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.google.common.base.Function;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/**
 * @author Samuel Gratzl
 *
 */
public final class CategoricalRankColumnModel<CATEGORY_TYPE extends Comparable<CATEGORY_TYPE>> extends
		ABasicFilterableRankColumnModel implements
 IFilterColumnMixin, IGrabRemainingHorizontalSpace, Cloneable,
		IRankableColumnMixin, IDataBasedColumnMixin {
	private final Function<IRow, CATEGORY_TYPE> data;
	private final Set<CATEGORY_TYPE> selection = new HashSet<>();
	private final String labelNA;
	private boolean filterNA = false;
	private final Map<CATEGORY_TYPE, String> metaData;

	public CategoricalRankColumnModel(IGLRenderer header, final Function<IRow, CATEGORY_TYPE> data,
			Map<CATEGORY_TYPE, String> metaData) {
		this(header, data, metaData, "");
	}

	public CategoricalRankColumnModel(IGLRenderer header, final Function<IRow, CATEGORY_TYPE> data,
			Map<CATEGORY_TYPE, String> metaData, String labelNA) {
		this(header, data, metaData, Color.GRAY, new Color(.95f, .95f, .95f),labelNA);
	}

	public static CategoricalRankColumnModel<String> createSimple(IGLRenderer header,
			final Function<IRow, String> data,
			Collection<String> items) {
		Map<String, String> map = new TreeMap<>();
		for (String s : items)
			map.put(s, s);
		return new CategoricalRankColumnModel<>(header, data, map,"");
	}

	public CategoricalRankColumnModel(IGLRenderer header, final Function<IRow, CATEGORY_TYPE> data,
			Map<CATEGORY_TYPE, String> metaData, Color color, Color bgColor, String labelNA) {
		super(color, bgColor);
		setHeaderRenderer(header);
		this.data = data;
		this.labelNA = labelNA == null? "" : labelNA;
		this.metaData = metaData;
		this.selection.addAll(metaData.keySet());
	}


	public CategoricalRankColumnModel(CategoricalRankColumnModel<CATEGORY_TYPE> copy) {
		super(copy);
		setHeaderRenderer(getHeaderRenderer());
		this.data = copy.data;
		this.metaData = copy.metaData;
		this.selection.addAll(copy.selection);
		this.filterNA = copy.filterNA;
		this.labelNA = copy.labelNA;
	}

	@Override
	public CategoricalRankColumnModel<CATEGORY_TYPE> clone() {
		return new CategoricalRankColumnModel<>(this);
	}

	public Map<CATEGORY_TYPE, String> getMetaData() {
		return metaData;
	}

	/**
	 * @return the data, see {@link #data}
	 */
	@Override
	public Function<IRow, CATEGORY_TYPE> getData() {
		return data;
	}

	@Override
	public String getValue(IRow row) {
		CATEGORY_TYPE value = getCatValue(row);
		if (value == null)
			return labelNA;
		return metaData.get(value);
	}

	@Override
	public GLElement createSummary(boolean interactive) {
		return new MyElement(interactive);
	}

	@Override
	public ValueElement createValue() {
		return new MyValueElement();
	}

	@Override
	public final void editFilter(final GLElement summary, IGLElementContext context) {
		final Vec2f location = summary.getAbsoluteLocation();
		context.getSWTLayer().run(new ISWTLayerRunnable() {
			@Override
			public void run(Display display, Composite canvas) {
				Point loc = canvas.toDisplay((int) location.x(), (int) location.y());
				CatFilterDalog<CATEGORY_TYPE> dialog = new CatFilterDalog<>(canvas.getShell(), getLabel(), summary,
						metaData, selection, CategoricalRankColumnModel.this, getTable().hasSnapshots(), loc, filterNA,
						labelNA);
				dialog.open();
			}
		});
	}

	public void setFilter(Collection<CATEGORY_TYPE> filter, boolean isFilterNA, boolean isGlobalFilter,
			boolean isRankIndependentFilter) {
		if (filter.equals(selection) && isFilterNA == this.filterNA && this.isGlobalFilter == isGlobalFilter
				&& this.isRankIndependentFilter == isRankIndependentFilter)
			return;
		invalidAllFilter();
		this.selection.clear();
		this.selection.addAll(filter);
		this.isGlobalFilter = isGlobalFilter;
		this.filterNA = isFilterNA;
		this.isRankIndependentFilter = isRankIndependentFilter;
		propertySupport.firePropertyChange(PROP_FILTER, false, true);
	}

	@Override
	public boolean isFiltered() {
		return selection.size() < metaData.size();
	}

	/**
	 * @return the selection, see {@link #selection}
	 */
	public Set<CATEGORY_TYPE> getSelection() {
		return selection;
	}

	/**
	 * @return the filterNA, see {@link #filterNA}
	 */
	public boolean isFilterNA() {
		return filterNA;
	}

	public CATEGORY_TYPE getCatValue(IRow row) {
		return data.apply(row);
	}

	@Override
	public int compare(IRow o1, IRow o2) {
		CATEGORY_TYPE t1 = getCatValue(o1);
		CATEGORY_TYPE t2 = getCatValue(o2);
		if ((t1 != null) != (t2 != null))
			return t1 == null ? 1 : -1;
		return t1 == null ? 0 : t1.compareTo(t2);
	}

	@Override
	public void orderByMe() {
		parent.orderBy(this);
	}

	@Override
	protected void updateMask(BitSet todo, List<IRow> data, BitSet mask) {
		for (int i = todo.nextSetBit(0); i >= 0; i = todo.nextSetBit(i + 1)) {
			CATEGORY_TYPE v = this.data.apply(data.get(i));
			if (v == null && filterNA)
				mask.set(i,false);
			else
				mask.set(i, v == null ? true : selection.contains(v));
		}
	}

	/**
	 * @return
	 */
	public Multiset<CATEGORY_TYPE> getHist() {
		Multiset<CATEGORY_TYPE> hist = HashMultiset.create(metaData.size());
		for (IRow r : getMyRanker()) {
			CATEGORY_TYPE v = getCatValue(r);
			if (v == null) // TODO nan
				continue;
			hist.add(v);
		}
		return hist;
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
			g.drawText("Filter:", 4, 2, w - 4, 12);
			String t = "<None>";
			if (isFiltered())
				t = selection.size() + " out of " + metaData.size();
			g.drawText(t, 4, 18, w - 4, 12);
		}

		@SuppressWarnings("unchecked")
		@ListenTo(sendToMe = true)
		private void onSetFilter(FilterEvent event) {
			setFilter((Collection<CATEGORY_TYPE>) event.getFilter(), event.isFilterNA(), event.isFilterGlobally(),
					event.isFilterRankIndendent());
		}
	}

	class MyValueElement extends ValueElement {
		public MyValueElement() {
			setVisibility(EVisibility.VISIBLE);
		}

		@Override
		protected void renderImpl(GLGraphics g, float w, float h, IRow row) {
			if (h < 5)
				return;
			String info = getTooltip();
			if (info == null)
				return;
			float hi = Math.min(h, 18);
			final boolean collapsed = (((IColumnRenderInfo) getParent()).isCollapsed());
			if (collapsed)
				info = StringUtils.substring(info, 0, 1);
			g.drawText(info, 1, 1 + (h - hi) * 0.5f, w - 2, hi - 5);
		}

		@Override
		public String getTooltip() {
			CATEGORY_TYPE value = getCatValue(getLayoutDataAs(IRow.class, null));
			if (value == null)
				return labelNA;
			return metaData.get(value);
		}
	}


}
