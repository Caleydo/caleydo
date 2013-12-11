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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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
import com.google.common.base.Functions;
import com.google.common.base.Objects;
import com.google.common.base.Predicates;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Multiset;

/**
 * multiple categories for one element model
 *
 * @author Samuel Gratzl
 *
 */
public class MultiCategoricalRankColumnModel<CATEGORY_TYPE extends Comparable<CATEGORY_TYPE>> extends
		ABasicFilterableRankColumnModel implements IFilterColumnMixin, IGrabRemainingHorizontalSpace,
		IRankableColumnMixin {
	private final Function<IRow, Set<CATEGORY_TYPE>> data;
	private final Set<CATEGORY_TYPE> selection = new HashSet<>();
	private final Set<CATEGORY_TYPE> categories;
	private final Function<? super CATEGORY_TYPE, String> cat2label;
	private final String labelNA;
	private boolean filterNA = false;

	public MultiCategoricalRankColumnModel(IGLRenderer header, final Function<IRow, Set<CATEGORY_TYPE>> data,
			Map<CATEGORY_TYPE, String> metaData, String labelNA) {
		this(header, data, metaData, Color.GRAY, new Color(.95f, .95f, .95f), labelNA);
	}

	public MultiCategoricalRankColumnModel(IGLRenderer header, final Function<IRow, Set<CATEGORY_TYPE>> data,
			Function<? super CATEGORY_TYPE, String> cat2label, Set<CATEGORY_TYPE> categories, String labelNA) {
		this(header, data, cat2label, categories, Color.GRAY, new Color(.95f, .95f, .95f), labelNA);
	}

	public static MultiCategoricalRankColumnModel<String> createSimple(IGLRenderer header,
			final Function<IRow, Set<String>> data, Set<String> items, String none) {
		return new MultiCategoricalRankColumnModel<String>(header, data, Functions.<String> identity(), items, none);
	}


	public MultiCategoricalRankColumnModel(IGLRenderer header, final Function<IRow, Set<CATEGORY_TYPE>> data,
			Map<CATEGORY_TYPE, String> metaData, Color color, Color bgColor, String labelNA) {
		this(header, data, Functions.forMap(metaData, null), metaData.keySet(), color, bgColor, labelNA);

	}

	public MultiCategoricalRankColumnModel(IGLRenderer header, Function<IRow, Set<CATEGORY_TYPE>> data,
			Function<? super CATEGORY_TYPE, String> cat2label, Set<CATEGORY_TYPE> categories, Color color,
			Color bgColor,
			String labelNA) {
		super(color, bgColor);
		setHeaderRenderer(header);
		this.data = data;
		this.cat2label = cat2label;
		this.categories = categories;
		this.selection.addAll(categories);
		this.labelNA = labelNA;
	}

	public MultiCategoricalRankColumnModel(MultiCategoricalRankColumnModel<CATEGORY_TYPE> copy) {
		super(copy);
		setHeaderRenderer(getHeaderRenderer());
		this.data = copy.data;
		this.cat2label = copy.cat2label;
		this.categories = copy.categories;
		this.selection.addAll(copy.selection);
		this.filterNA = copy.filterNA;
		this.labelNA = copy.labelNA;
	}
	@Override
	public MultiCategoricalRankColumnModel<CATEGORY_TYPE> clone() {
		return new MultiCategoricalRankColumnModel<>(this);
	}

	/**
	 * @return the categories, see {@link #categories}
	 */
	public Set<CATEGORY_TYPE> getCategories() {
		return categories;
	}

	@Override
	public String getValue(IRow row) {
		Set<CATEGORY_TYPE> value = getCatValue(row);
		if (value == null || value.isEmpty())
			return labelNA;
		return StringUtils.join(Iterators.transform(value.iterator(), cat2label), ',');
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
						categories, cat2label, selection, MultiCategoricalRankColumnModel.this, getTable()
								.hasSnapshots(), loc,
						filterNA, labelNA);
				dialog.open();
			}
		});
	}

	protected void setFilter(Collection<CATEGORY_TYPE> filter, boolean filterNA, boolean isGlobalFilter,
			boolean isRankIndependentFilter) {
		if (filter.equals(selection) && filterNA == this.filterNA && this.isGlobalFilter == isGlobalFilter
				&& this.isRankIndependentFilter == isRankIndependentFilter)
			return;
		invalidAllFilter();
		this.selection.clear();
		this.selection.addAll(filter);
		this.isGlobalFilter = isGlobalFilter;
		this.filterNA = filterNA;
		this.isRankIndependentFilter = isRankIndependentFilter;
		propertySupport.firePropertyChange(PROP_FILTER, false, true);
	}

	@Override
	public boolean isFiltered() {
		return selection.size() < categories.size() || filterNA;
	}

	/**
	 * @return the filterNA, see {@link #filterNA}
	 */
	public boolean isFilterNA() {
		return filterNA;
	}

	public Set<CATEGORY_TYPE> getCatValue(IRow row) {
		return data.apply(row);
	}

	@Override
	public int compare(IRow o1, IRow o2) {
		Set<CATEGORY_TYPE> t1 = getCatValue(o1);
		Set<CATEGORY_TYPE> t2 = getCatValue(o2);
		if (Objects.equal(t1, t2))
			return 0;
		if ((t1 != null) != (t2 != null))
			return t1 == null ? 1 : -1;
		assert t1 != null && t2 != null;
		// idea: as comparable sort their values, decreasing and compare them
		Iterator<CATEGORY_TYPE> ita = new TreeSet<>(t1).iterator();
		Iterator<CATEGORY_TYPE> itb = new TreeSet<>(t2).iterator();
		int c;
		while (ita.hasNext() && itb.hasNext()) {
			CATEGORY_TYPE a = ita.next();
			CATEGORY_TYPE b = itb.next();
			if ((c = a.compareTo(b)) != 0)
				return c;
		}
		// one is maybe longer than the other
		if (ita.hasNext() == itb.hasNext())
			return 0;
		return ita.hasNext() ? 1 : -1; // the longer the bigger
	}

	@Override
	public void orderByMe() {
		parent.orderBy(this);
	}

	@Override
	protected void updateMask(BitSet todo, List<IRow> data, BitSet mask) {
		for (int i = todo.nextSetBit(0); i >= 0; i = todo.nextSetBit(i + 1)) {
			Set<CATEGORY_TYPE> v = this.data.apply(data.get(i));
			if ((v == null || v.isEmpty()) && filterNA)
				mask.set(i, false);
			else
				mask.set(i, v == null ? true : Iterables.any(v, Predicates.in(selection)));
		}
	}

	/**
	 * @return
	 */
	public Multiset<CATEGORY_TYPE> getHist() {
		Multiset<CATEGORY_TYPE> hist = HashMultiset.create(categories.size());
		for (IRow r : getMyRanker()) {
			Set<CATEGORY_TYPE> vs = getCatValue(r);
			if (vs == null) // TODO nan
				continue;
			hist.addAll(vs);
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
				t = selection.size() + " out of " + categories.size();
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
		protected void renderImpl(GLGraphics g, float w, float h) {
			if (h < 5)
				return;
			String info = getTooltip();
			if (info == null)
				return;
			float hi = Math.min(h, 18);
			if (!(((IColumnRenderInfo) getParent()).isCollapsed())) {
				g.drawText(info, 1, 1 + (h - hi) * 0.5f, w - 2, hi - 5);
			}
		}

		@Override
		public String getTooltip() {
			return getValue(getRow());
		}
	}

}
