/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.model;

import gleem.linalg.Vec2f;

import java.beans.PropertyChangeListener;
import java.util.BitSet;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.util.base.ICallback;
import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.ISWTLayer.ISWTLayerRunnable;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.vis.lineup.internal.event.FilterEvent;
import org.caleydo.vis.lineup.internal.event.SearchEvent;
import org.caleydo.vis.lineup.internal.event.SearchEvent.SearchResult;
import org.caleydo.vis.lineup.internal.ui.StringFilterDialog;
import org.caleydo.vis.lineup.internal.ui.StringSearchDialog;
import org.caleydo.vis.lineup.model.mixin.IFilterColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IGrabRemainingHorizontalSpace;
import org.caleydo.vis.lineup.model.mixin.IRankColumnModel;
import org.caleydo.vis.lineup.model.mixin.IRankableColumnMixin;
import org.caleydo.vis.lineup.model.mixin.ISearchableColumnMixin;
import org.caleydo.vis.lineup.ui.GLPropertyChangeListeners;
import org.caleydo.vis.lineup.ui.detail.ValueElement;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.google.common.base.Function;

/**
 * a special {@link IRankColumnModel} for Strings
 *
 * @author Samuel Gratzl
 *
 */
public class StringRankColumnModel extends ABasicFilterableRankColumnModel implements IGrabRemainingHorizontalSpace,
		IFilterColumnMixin, IRankableColumnMixin, ISearchableColumnMixin {
	/**
	 * different strategies for filter modi
	 *
	 * @author Samuel Gratzl
	 *
	 */
	public enum FilterStrategy {
		STAR_WILDCARD, SUBSTRING, REGEX;

		public String getHint() {
			switch (this) {
			case SUBSTRING:
				return "(containing)";
			case STAR_WILDCARD:
				return "(use * as wildcard)";
			case REGEX:
				return "(a valid regular expression)";
			}
			throw new IllegalStateException();
		}

		public String prepare(String filter) {
			switch (this) {
			case SUBSTRING:
				return filter.toLowerCase();
			case STAR_WILDCARD:
				return "\\Q" + filter.toLowerCase().replace("*", "\\E.*\\Q") + "\\E";
			case REGEX:
				return filter;
			}
			throw new IllegalStateException();
		}

		public boolean apply(String prepared, String v) {
			if (v == null || v.isEmpty())
				return false;
			switch (this) {
			case SUBSTRING:
				return v.toLowerCase().contains(prepared);
			case STAR_WILDCARD:
				return Pattern.matches(prepared, v.toLowerCase());
			case REGEX:
				return Pattern.matches(prepared, v);
			}
			throw new IllegalStateException();
		}
	}

	public static final Function<IRow, String> DEFAULT = new Function<IRow, String>() {
		@Override
		public String apply(IRow row) {
			if (row instanceof ILabeled) {
				return ((ILabeled) row).getLabel();
			}
			return Objects.toString(row);
		}
	};

	private final Function<IRow, String> data;
	private String filter;
	private final FilterStrategy filterStrategy;

	public StringRankColumnModel(IGLRenderer header, final Function<IRow, String> data) {
		this(header, data, Color.GRAY, new Color(.95f, .95f, .95f));
	}

	public StringRankColumnModel(IGLRenderer header, final Function<IRow, String> data, Color color, Color bgColor) {
		this(header, data, color, bgColor, FilterStrategy.SUBSTRING);
	}

	public StringRankColumnModel(IGLRenderer header, final Function<IRow, String> data, Color color, Color bgColor,
			FilterStrategy filterStrategy) {
		super(color, bgColor);
		setHeaderRenderer(header);
		this.data = data;
		this.filterStrategy = filterStrategy;
	}

	public StringRankColumnModel(StringRankColumnModel copy) {
		super(copy);
		setHeaderRenderer(getHeaderRenderer());
		this.data = copy.data;
		this.filter = copy.filter;
		this.filterStrategy = copy.filterStrategy;
	}

	@Override
	public StringRankColumnModel clone() {
		return new StringRankColumnModel(this);
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
	public int compare(IRow o1, IRow o2) {
		String s1 = data.apply(o1);
		String s2 = data.apply(o2);
		if (Objects.equals(s1, s2))
			return 0;
		if (s1 == null)
			return 1;
		if (s2 == null)
			return -1;
		return String.CASE_INSENSITIVE_ORDER.compare(s1, s2);
	}

	@Override
	public String getValue(IRow row) {
		return data.apply(row);
	}

	@Override
	public void orderByMe() {
		parent.orderBy(this);
	}

	@Override
	public final void editFilter(final GLElement summary, IGLElementContext context) {
		final Vec2f location = summary.getAbsoluteLocation();
		context.getSWTLayer().run(new ISWTLayerRunnable() {
			@Override
			public void run(Display display, Composite canvas) {
				Point loc = canvas.toDisplay((int) location.x(), (int) location.y());
				StringFilterDialog dialog = new StringFilterDialog(canvas.getShell(), getLabel(), filterStrategy
						.getHint(), summary, filter, StringRankColumnModel.this, getTable().hasSnapshots(), loc);
				dialog.open();
			}
		});
	}

	@Override
	public void openSearchDialog(final GLElement summary, IGLElementContext context) {
		final Vec2f location = summary.getAbsoluteLocation();
		context.getSWTLayer().run(new ISWTLayerRunnable() {
			@Override
			public void run(Display display, Composite canvas) {
				Point loc = canvas.toDisplay((int) location.x(), (int) location.y());
				StringSearchDialog dialog = new StringSearchDialog(canvas.getShell(), getLabel(), filterStrategy
						.getHint(), summary, filter, loc);
				dialog.open();
			}
		});
	}

	/**
	 * @param search
	 * @param isForward
	 */
	public void onSearch(String search, boolean wrapSearch, boolean isForward, ICallback<SearchResult> callback) {
		if (search == null || search.trim().isEmpty())
			return;
		final ColumnRanker ranker = getMyRanker();
		final RankTableModel table = ranker.getTable();
		final int[] order = ranker.getOrder();
		final int size = ranker.size();
		final int selected = ranker.getSelectedRank();
		BitSet matching = searchAll(search, table, order);

		int toSelect = -1;
		if (isForward) {
			int start = Math.min(selected < 0 ? 0 : selected + 1, size - 1);
			// from start to end
			toSelect = matching.nextSetBit(start);
			if (toSelect < 0 && wrapSearch) {
				toSelect = matching.nextSetBit(0);
				if (toSelect >= start)
					toSelect = -1;
			}
		} else {
			int start = Math.max(selected < 0 ? order.length - 1 : selected - 1, 0);
			// from start to 0
			toSelect = matching.previousSetBit(start - 1);
			if (toSelect < 0 && wrapSearch) {
				toSelect = matching.previousSetBit(matching.length());
				if (toSelect <= start)
					toSelect = -1;
			}
		}
		if (toSelect >= 0)
			table.setSelectedRow(table.getDataItem(order[toSelect]));
		else
			table.setSelectedRow(null);
		if (callback != null)
			callback.on(new SearchResult(matching.cardinality()));
	}

	/**
	 * @param order
	 * @param table
	 * @param preparedFilter
	 * @return
	 */
	private BitSet searchAll(String search, final RankTableModel table, final int[] order) {
		String preparedFilter = filterStrategy.prepare(search);

		BitSet r = new BitSet(order.length);
		for (int i = 0; i < order.length; ++i) {
			IRow row = table.getDataItem(order[i]);
			r.set(i, filterStrategy.apply(preparedFilter, this.data.apply(row)));
		}
		return r;
	}

	public void setFilter(String filter, boolean isFilterGlobally, boolean isRankIndependentFilter) {
		if (filter != null && filter.trim().length() == 0)
			filter = null;
		if (Objects.equals(filter, this.filter) && this.isGlobalFilter == isFilterGlobally
				&& this.isRankIndependentFilter == isRankIndependentFilter)
			return;
		invalidAllFilter();
		this.filter = filter;
		this.isGlobalFilter = isFilterGlobally;
		this.isRankIndependentFilter = isRankIndependentFilter;
		propertySupport.firePropertyChange(PROP_FILTER, false, true);
	}

	@Override
	public boolean isFiltered() {
		return filter != null;
	}

	@Override
	protected void updateMask(BitSet todo, List<IRow> data, BitSet mask) {
		String prepared = filterStrategy.prepare(filter);
		for (int i = todo.nextSetBit(0); i >= 0; i = todo.nextSetBit(i + 1)) {
			String v = this.data.apply(data.get(i));
			if (v == null)
				continue;
			mask.set(i, filterStrategy.apply(prepared, v));
		}
	}

	public static String starToRegex(String filter) {
		return "\\Q" + filter.toLowerCase().replace("*", "\\E.*\\Q") + "\\E";
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
			if (w < 20)
				return;
			g.drawText("Filter:", 4, 2, w - 4, 12);
			String t = "<None>";
			if (filter != null)
				t = filter;
			g.drawText(t, 4, 18, w - 4, 12);
		}

		@ListenTo(sendToMe = true)
		private void onSetFilter(FilterEvent event) {
			setFilter((String) event.getFilter(), event.isFilterGlobally(), event.isFilterRankIndendent());
		}

		@ListenTo(sendToMe = true)
		private void onSetSearch(SearchEvent event) {
			onSearch((String) event.getSearch(), event.isWrapSearch(), event.isForward(), event.getCallback());
		}

	}

	class MyValueElement extends ValueElement {
		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			if (h < 5 || (w - 7) < 10)
				return;
			String value = getTooltip();
			if (value == null)
				return;
			float hi = Math.min(h, 19);
			g.drawText(value, 3, (h - hi) * 0.5f, w - 7, hi - 5);
		}

		@Override
		public String getTooltip() {
			return data.apply(getLayoutDataAs(IRow.class, null));
		}
	}
}
