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

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.vis.rank.internal.event.FilterEvent;
import org.caleydo.vis.rank.internal.ui.CatFilterDalog;
import org.caleydo.vis.rank.model.mixin.IFilterColumnMixin;
import org.caleydo.vis.rank.model.mixin.IGrabRemainingHorizontalSpace;
import org.caleydo.vis.rank.model.mixin.IRankableColumnMixin;
import org.caleydo.vis.rank.ui.GLPropertyChangeListeners;
import org.caleydo.vis.rank.ui.IColumnRenderInfo;
import org.caleydo.vis.rank.ui.detail.ValueElement;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.google.common.base.Function;
import com.google.common.collect.Sets;

/**
 * @author Samuel Gratzl
 *
 */
public class CategoricalRankColumnModel<CATEGORY_TYPE extends Comparable<CATEGORY_TYPE>> extends
		ABasicFilterableRankColumnModel implements
 IFilterColumnMixin, IGrabRemainingHorizontalSpace,
		IRankableColumnMixin {
	private final Function<IRow, CATEGORY_TYPE> data;
	private Set<CATEGORY_TYPE> selection = new HashSet<>();
	private Map<CATEGORY_TYPE, String> metaData;

	public CategoricalRankColumnModel(IGLRenderer header, final Function<IRow, CATEGORY_TYPE> data,
			Map<CATEGORY_TYPE, String> metaData) {
		this(header, data, metaData, Color.GRAY, new Color(.95f, .95f, .95f));
	}

	public CategoricalRankColumnModel(IGLRenderer header, final Function<IRow, CATEGORY_TYPE> data,
			Map<CATEGORY_TYPE, String> metaData, Color color, Color bgColor) {
		super(color, bgColor);
		setHeaderRenderer(header);
		this.data = data;
		this.metaData = metaData;
		this.selection.addAll(metaData.keySet());
	}


	public CategoricalRankColumnModel(CategoricalRankColumnModel<CATEGORY_TYPE> copy) {
		super(copy);
		setHeaderRenderer(getHeaderRenderer());
		this.data = copy.data;
		this.metaData = copy.metaData;
		this.selection.addAll(copy.selection);
	}

	@Override
	public CategoricalRankColumnModel<CATEGORY_TYPE> clone() {
		return new CategoricalRankColumnModel<>(this);
	}

	public Map<CATEGORY_TYPE, String> getMetaData() {
		return metaData;
	}

	@Override
	public String getValue(IRow row) {
		CATEGORY_TYPE value = getCatValue(row);
		if (value == null)
			return "";
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
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				new CatFilterDalog<CATEGORY_TYPE>(new Shell(), getTitle(), summary, metaData, selection, isGlobalFilter)
						.open();
			}
		});
	}

	protected void setFilter(Collection<CATEGORY_TYPE> filter, boolean isGlobalFilter) {
		invalidAllFilter();
		Set<CATEGORY_TYPE> bak = new HashSet<>(this.selection);
		this.selection.clear();
		this.selection.addAll(filter);
		if (Sets.difference(bak, this.selection).isEmpty()) {
			setGlobalFilter(isGlobalFilter);
		} else {
			this.isGlobalFilter = isGlobalFilter;
			propertySupport.firePropertyChange(PROP_FILTER, bak, this.selection);
		}
	}

	@Override
	public boolean isFiltered() {
		return selection.size() < metaData.size();
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
			mask.set(i, selection.contains(v));
		}
	}

	/**
	 * @return
	 */
	public Map<CATEGORY_TYPE, Integer> getHist() {
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
			setFilter((Collection<CATEGORY_TYPE>) event.getFilter(), event.isFilterGlobally());
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
			CATEGORY_TYPE value = getCatValue(getLayoutDataAs(IRow.class, null));
			if (value == null)
				return null;
			return metaData.get(value);
		}
	}


}
