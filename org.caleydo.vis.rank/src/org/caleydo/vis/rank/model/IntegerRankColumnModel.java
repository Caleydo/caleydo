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

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.BitSet;
import java.util.List;
import java.util.Locale;

import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.ISWTLayer.ISWTLayerRunnable;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.vis.rank.internal.event.SizeFilterEvent;
import org.caleydo.vis.rank.internal.ui.IntegerFilterDialog;
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
public class IntegerRankColumnModel extends ABasicFilterableRankColumnModel implements IRankableColumnMixin,
		IFilterColumnMixin {
	private final Function<IRow, Integer> data;

	private int min = 0;
	private int max = Integer.MAX_VALUE;

	private final NumberFormat formatter;

	public IntegerRankColumnModel(IGLRenderer header, Function<IRow, Integer> data) {
		this(header, data, Color.GRAY, new Color(.95f, .95f, .95f), NumberFormat.getInstance(Locale.ENGLISH));
	}

	public IntegerRankColumnModel(IGLRenderer header, Function<IRow, Integer> data, Color color, Color bgColor,
			NumberFormat formatter) {
		super(color, bgColor);
		setHeaderRenderer(header);
		this.data = data;
		this.formatter = formatter;
	}

	public IntegerRankColumnModel(IntegerRankColumnModel copy) {
		super(copy);
		this.data = copy.data;
		this.min = copy.min;
		this.max = copy.max;
		this.formatter = copy.formatter;
	}

	@Override
	public IntegerRankColumnModel clone() {
		return new IntegerRankColumnModel(this);
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
				IntegerFilterDialog dialog = new IntegerFilterDialog(canvas.getShell(), getTitle(), summary, min, max,
						isGlobalFilter, getTable().hasSnapshots(), loc);
				dialog.open();
			}
		});
	}

	/**
	 * @param min2
	 * @param max2
	 */
	public void setFilter(Integer min2, Integer max2) {
		invalidAllFilter();
		min = min2 == null ? 0 : min2.intValue();
		max = max2 == null ? Integer.MAX_VALUE : max2.intValue();
		propertySupport.firePropertyChange(PROP_FILTER, Pair.make(min, max), Pair.make(min2, max2));
	}

	@Override
	public boolean isFiltered() {
		return min > 0 || max < Integer.MAX_VALUE;
	}

	@Override
	protected void updateMask(BitSet todo, List<IRow> data, BitSet mask) {
		for (int i = todo.nextSetBit(0); i >= 0; i = todo.nextSetBit(i + 1)) {
			int value = getInt(data.get(i));
			mask.set(i, value >= min && value <= max);
		}
	}

	public int getInt(IRow prow) {
		return data.apply(prow);
	}

	@Override
	public String getValue(IRow row) {
		return "" + getInt(row);
	}

	@Override
	public int compare(IRow o1, IRow o2) {
		return getInt(o1) - getInt(o2);
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
			int f = getInt(getLayoutDataAs(IRow.class, null));
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
		private void onSetFilter(SizeFilterEvent event) {
			setFilter(event.getMin(), event.getMax());
		}

		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			super.renderImpl(g, w, h);
			if (w < 20)
				return;
			g.drawText("Filter:", 4, 2, w - 4, 12);
			StringBuilder b = new StringBuilder();
			if (min > 0)
				b.append(min).append(" <= v");
			if (max < Integer.MAX_VALUE)
				b.append(b.length() > 0 ? " <= " : "v <= ").append(max);
			if (b.length() == 0)
				b.append("<None>");
			g.drawText(b.toString(), 4, 18, w - 4, 12);
		}
	}

}