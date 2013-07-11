/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.rank.model;

import gleem.linalg.Vec2f;

import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.ISWTLayer.ISWTLayerRunnable;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.vis.rank.internal.event.DateFilterEvent;
import org.caleydo.vis.rank.internal.ui.DateFilterDialog;
import org.caleydo.vis.rank.model.mixin.IFilterColumnMixin;
import org.caleydo.vis.rank.model.mixin.IRankableColumnMixin;
import org.caleydo.vis.rank.ui.GLPropertyChangeListeners;
import org.caleydo.vis.rank.ui.detail.ValueElement;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.google.common.base.Function;

/**
 * @author Samuel Gratzl
 *
 */
public class DateRankColumnModel extends ABasicFilterableRankColumnModel implements IRankableColumnMixin,
		IFilterColumnMixin {
	private final Function<IRow, Date> data;

	public enum DateMode {
		DATE, TIME, DATE_TIME
	}

	private Calendar from = null;
	private Calendar to = null;

	private final DateMode mode;
	private final DateFormat formatter;

	public DateRankColumnModel(IGLRenderer header, Function<IRow, Date> data) {
		this(header, data, Color.GRAY, new Color(.95f, .95f, .95f), DateMode.DATE);
	}

	public DateRankColumnModel(IGLRenderer header, Function<IRow, Date> data, Color color, Color bgColor, DateMode mode) {
		super(color, bgColor);
		setHeaderRenderer(header);
		this.data = data;
		this.mode = mode;
		this.formatter = toFormatter(mode);
	}

	private static DateFormat toFormatter(DateMode mode) {
		switch(mode) {
		case DATE:
			return DateFormat.getDateInstance(DateFormat.SHORT, Locale.ENGLISH);
		case TIME:
			return DateFormat.getTimeInstance(DateFormat.SHORT, Locale.ENGLISH);
		case DATE_TIME:
			return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.ENGLISH);
		}
		throw new IllegalStateException();
	}

	private static Calendar asCalendar(Date date, DateMode mode) {
		assert date != null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		switch (mode) {
		case DATE:
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			break;
		case TIME:
			cal.set(Calendar.YEAR, 0);
			cal.set(Calendar.MONTH, 0);
			cal.set(Calendar.DATE, 0);
			break;
		case DATE_TIME:
			break;
		}
		return cal;
	}

	public DateRankColumnModel(DateRankColumnModel copy) {
		super(copy);
		this.data = copy.data;
		this.from = copy.from;
		this.to = copy.to;
		this.mode = copy.mode;
		this.formatter = copy.formatter;
	}

	@Override
	public DateRankColumnModel clone() {
		return new DateRankColumnModel(this);
	}

	@Override
	public GLElement createSummary(boolean interactive) {
		return new MyHeaderElement(interactive);
	}

	@Override
	public ValueElement createValue() {
		return new MyElement();
	}


	@Override
	public void editFilter(final GLElement summary, IGLElementContext context) {
		final Vec2f location = summary.getAbsoluteLocation();
		context.getSWTLayer().run(new ISWTLayerRunnable() {
			@Override
			public void run(Display display, Composite canvas) {
				Point loc = canvas.toDisplay((int) location.x(), (int) location.y());
				DateFilterDialog dialog = new DateFilterDialog(canvas.getShell(), getTitle(), summary, from, to,
						isGlobalFilter, getTable().hasSnapshots(), loc, mode);
				dialog.open();
			}
		});
	}

	/**
	 * @param min2
	 * @param max2
	 */
	public void setFilter(Calendar from2, Calendar to2) {
		invalidAllFilter();
		Pair<Calendar, Calendar> old = Pair.make(from, to);
		from = from2;
		to = to2;
		propertySupport.firePropertyChange(PROP_FILTER, old, Pair.make(from, to));
	}

	@Override
	public boolean isFiltered() {
		return from != null || to != null;
	}

	@Override
	protected void updateMask(BitSet todo, List<IRow> data, BitSet mask) {
		for (int i = todo.nextSetBit(0); i >= 0; i = todo.nextSetBit(i + 1)) {
			Date date = getDate(data.get(i));
			if (date == null || (from == null && to == null)) {
				mask.set(i, true);
				continue;
			}
			Calendar cal = asCalendar(date, mode);
			mask.set(i, (from == null || cal.compareTo(from) >= 0) && (to == null || cal.compareTo(to) <= 0));
		}
	}

	public Date getDate(IRow prow) {
		return data.apply(prow);
	}

	public Calendar getCalendar(IRow prow) {
		Date d = getDate(prow);
		return d == null ? null : asCalendar(d, mode);
	}

	@Override
	public String getValue(IRow row) {
		Date d = getDate(row);
		return d == null ? "" : formatter.format(d);
	}

	@Override
	public int compare(IRow o1, IRow o2) {
		Date a = getDate(o1);
		Date b = getDate(o2);
		if (a == b)
			return 0;
		return (a == null) ? 1 : a.compareTo(b);
	}

	@Override
	public void orderByMe() {
		parent.orderBy(this);
	}

	class MyElement extends ValueElement {
		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			if (h < 5)
				return;
			super.renderImpl(g, w, h);
			float hi = Math.min(h, 16);
			Date f = getDate(getLayoutDataAs(IRow.class, null));
			g.drawText(formatter == null ? f + "" : formatter.format(f), 1, 1 + (h - hi) * 0.5f, w - 2,
						hi - 2);
		}
	}

	private class MyHeaderElement extends GLElement {
		private final PropertyChangeListener repaintListner = GLPropertyChangeListeners.repaintOnEvent(this);

		public MyHeaderElement(boolean interactive) {
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

		@ListenTo(sendToMe = true)
		private void onSetFilter(DateFilterEvent event) {
			setFilter(event.getBefore(), event.getAfter());
		}

		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			super.renderImpl(g, w, h);
			// TODO
			// if (w < 20)
			// return;
			// g.drawText("Filter:", 4, 2, w - 4, 12);
			// StringBuilder b = new StringBuilder();
			// if (min > 0)
			// b.append(min).append(" <= v");
			// if (max < Integer.MAX_VALUE)
			// b.append(b.length() > 0 ? " <= " : "v <= ").append(max);
			// if (b.length() == 0)
			// b.append("<None>");
			// g.drawText(b.toString(), 4, 18, w - 4, 12);
		}
	}

}
