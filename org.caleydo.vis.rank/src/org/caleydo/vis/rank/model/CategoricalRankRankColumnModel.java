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

import java.beans.PropertyChangeListener;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.io.gui.dataimport.widget.ICallback;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.ISWTLayer.ISWTLayerRunnable;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.vis.rank.internal.event.FilterEvent;
import org.caleydo.vis.rank.internal.ui.CatFilterDalog;
import org.caleydo.vis.rank.model.mapping.ICategoricalMappingFunction;
import org.caleydo.vis.rank.model.mixin.IFilterColumnMixin;
import org.caleydo.vis.rank.model.mixin.IFloatRankableColumnMixin;
import org.caleydo.vis.rank.model.mixin.IMappedColumnMixin;
import org.caleydo.vis.rank.ui.GLPropertyChangeListeners;
import org.caleydo.vis.rank.ui.IColumnRenderInfo;
import org.caleydo.vis.rank.ui.RenderUtils;
import org.caleydo.vis.rank.ui.detail.CategoricalScoreBarElement;
import org.caleydo.vis.rank.ui.detail.ValueElement;
import org.caleydo.vis.rank.ui.mapping.MappingFunctionUIs;
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
public class CategoricalRankRankColumnModel<CATEGORY_TYPE> extends ABasicFilterableRankColumnModel implements
		IFilterColumnMixin, IMappedColumnMixin, IFloatRankableColumnMixin {
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
				CatFilterDalog<CATEGORY_TYPE> dialog = new CatFilterDalog<>(canvas.getShell(), getTitle(), summary,
						metaData, selection, isGlobalFilter, getTable().hasSnapshots(), loc);
				dialog.open();
			}
		});
	}

	protected void setFilter(Collection<CATEGORY_TYPE> filter, boolean isGlobalFilter) {
		invalidAllFilter();
		Set<CATEGORY_TYPE> bak = new HashSet<>(this.selection);
		this.selection.clear();
		this.selection.addAll(filter);
		if (this.selection.equals(bak)) {
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
	public float applyPrimitive(IRow in) {
		return mapping.applyPrimitive(getCatValue(in));
	}

	@Override
	public Float apply(IRow in) {
		return applyPrimitive(in);
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
			RenderUtils.renderHist(g, hist, w, h, selected, metaData);
		}

		@SuppressWarnings("unchecked")
		@ListenTo(sendToMe = true)
		private void onSetFilter(FilterEvent event) {
			setFilter((Collection<CATEGORY_TYPE>) event.getFilter(), event.isFilterGlobally());
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
	public void editMapping(GLElement summary, IGLElementContext context) {
		GLElement m = MappingFunctionUIs.create(mapping, getHist(), metaData, color, bgColor, callback);
		m.setzDelta(0.5f);
		Vec2f location = summary.getAbsoluteLocation();
		Vec2f size = summary.getSize();
		context.getPopupLayer().show(m, new Vec4f(location.x(), location.y() + size.y(), 260, 260));
	}
}
