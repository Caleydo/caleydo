/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.rank.model;

import gleem.linalg.Vec2f;

import java.beans.PropertyChangeListener;
import java.util.BitSet;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.ISWTLayer.ISWTLayerRunnable;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.vis.rank.internal.event.FilterEvent;
import org.caleydo.vis.rank.internal.event.SearchEvent;
import org.caleydo.vis.rank.internal.ui.StringFilterDialog;
import org.caleydo.vis.rank.internal.ui.StringSearchDialog;
import org.caleydo.vis.rank.model.mixin.IFilterColumnMixin;
import org.caleydo.vis.rank.model.mixin.IGrabRemainingHorizontalSpace;
import org.caleydo.vis.rank.model.mixin.IRankColumnModel;
import org.caleydo.vis.rank.model.mixin.IRankableColumnMixin;
import org.caleydo.vis.rank.model.mixin.ISearchableColumnMixin;
import org.caleydo.vis.rank.ui.GLPropertyChangeListeners;
import org.caleydo.vis.rank.ui.detail.ValueElement;
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
		return String.CASE_INSENSITIVE_ORDER.compare(data.apply(o1), data.apply(o2));
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
				StringFilterDialog dialog = new StringFilterDialog(canvas.getShell(), getTitle(), filterStrategy
						.getHint(), summary, filter, isGlobalFilter, getTable().hasSnapshots(), loc);
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
				StringSearchDialog dialog = new StringSearchDialog(canvas.getShell(), getTitle(), filterStrategy
						.getHint(), summary, filter, loc);
				dialog.open();
			}
		});
	}

	/**
	 * @param search
	 * @param isForward
	 */
	public void onSearch(String search, boolean wrapSearch, boolean isForward) {
		if (search == null || search.trim().isEmpty())
			return;
		String prepared = filterStrategy.prepare(search);
		ColumnRanker ranker = getMyRanker();
		final int selected = ranker.getSelectedRank();
		int[] order = ranker.getOrder();
		RankTableModel table = ranker.getTable();

		if (isForward) {
			int start = Math.min(selected < 0 ? 0 : selected + 1, order.length - 1);
			// from start to end
			for (int i = start; i < order.length; ++i) {
				IRow row = table.getDataItem(order[i]);
				if (filterStrategy.apply(prepared, this.data.apply(row))) {
					table.setSelectedRow(row);
					return;
				}
			}
			if (wrapSearch) {
				// from 0 to start-1
				for (int i = 0; i < start - 1; ++i) {
					IRow row = table.getDataItem(order[i]);
					if (filterStrategy.apply(prepared, this.data.apply(row))) {
						table.setSelectedRow(row);
						return;
					}
				}
			}
		} else {
			int start = Math.max(selected < 0 ? order.length - 1 : selected - 1, 0);
			// from start to 0
			for (int i = start; i >= 0; --i) {
				IRow row = table.getDataItem(order[i]);
				if (filterStrategy.apply(prepared, this.data.apply(row))) {
					table.setSelectedRow(row);
					return;
				}
			}
			if (wrapSearch) {
				// from end to start+1
				for (int i = order.length - 1; i >= start + 1; --i) {
					IRow row = table.getDataItem(order[i]);
					if (filterStrategy.apply(prepared, this.data.apply(row))) {
						table.setSelectedRow(row);
						return;
					}
				}
			}
		}
		// nothing found
		table.setSelectedRow(null);
	}

	public void setFilter(String filter, boolean isFilterGlobally) {
		if (filter != null && filter.trim().length() == 0)
			filter = null;
		if (Objects.equals(filter, this.filter) && this.isGlobalFilter == isFilterGlobally)
			return;
		invalidAllFilter();
		if (Objects.equals(filter, this.filter)) {
			setGlobalFilter(isFilterGlobally);
		} else {
			this.isGlobalFilter = isFilterGlobally;
			propertySupport.firePropertyChange(PROP_FILTER, this.filter, this.filter = filter);
		}
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
			setFilter((String) event.getFilter(), event.isFilterGlobally());
		}

		@ListenTo(sendToMe = true)
		private void onSetSearch(SearchEvent event) {
			onSearch((String) event.getSearch(), event.isWrapSearch(), event.isForward());
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
