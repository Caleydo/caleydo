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
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.util.base.ILabelProvider;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.vis.rank.internal.event.FilterEvent;
import org.caleydo.vis.rank.model.mixin.IGrabRemainingHorizontalSpace;
import org.caleydo.vis.rank.model.mixin.IRankColumnModel;
import org.caleydo.vis.rank.ui.GLPropertyChangeListeners;
import org.caleydo.vis.rank.ui.detail.ValueElement;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

import com.google.common.base.Function;

/**
 * a special {@link IRankColumnModel} for Strings
 *
 * @author Samuel Gratzl
 *
 */
public class StringRankColumnModel extends ABasicFilterableRankColumnModel implements IGrabRemainingHorizontalSpace {
	public static final Function<IRow, String> DEFAULT = new Function<IRow, String>() {
		@Override
		public String apply(IRow row) {
			if (row instanceof ILabelProvider) {
				return ((ILabelProvider) row).getLabel();
			}
			return Objects.toString(row);
		}
	};

	private final Function<IRow, String> data;
	private String filter;

	public StringRankColumnModel(IGLRenderer header, final Function<IRow, String> data) {
		this(header, data, Color.GRAY, new Color(.95f, .95f, .95f));
	}

	public StringRankColumnModel(IGLRenderer header, final Function<IRow, String> data, Color color, Color bgColor) {
		super(color, bgColor);
		setHeaderRenderer(header);
		this.data = data;
	}

	public StringRankColumnModel(StringRankColumnModel copy) {
		super(copy);
		setHeaderRenderer(getHeaderRenderer());
		this.data = copy.data;
		this.filter = copy.filter;
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
	public final void editFilter(final GLElement summary, IGLElementContext context) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				InputDialog d = new InputDialog(null, "Filter column: " + getTitle(),
						"Edit Filter (use * as wildcard)", filter, null);
				if (d.open() == Window.OK) {
					String v = d.getValue().trim();
					if (v.length() == 0)
						v = null;
					EventPublisher.publishEvent(new FilterEvent(v).to(summary));
				}
			}
		});
	}

	public void setFilter(String filter) {
		invalidAllFilter();
		propertySupport.firePropertyChange(PROP_FILTER, this.filter, this.filter = filter);
	}

	@Override
	public boolean isFiltered() {
		return filter != null;
	}

	@Override
	protected void updateMask(BitSet todo, List<IRow> data, BitSet mask) {
		String regex = starToRegex(filter);
		for (int i = todo.nextSetBit(0); i >= 0; i = todo.nextSetBit(i + 1)) {
			String v = this.data.apply(data.get(i));
			mask.set(i, Pattern.matches(regex, v));
		}
	}

	public static String starToRegex(String filter) {
		return "\\Q" + filter.replace("*", "\\E.*\\Q") + "\\E";
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
			setFilter((String) event.getFilter());
		}

	}

	class MyValueElement extends ValueElement {
		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			if (h < 5)
				return;
			String value = getTooltip();
			if (value == null)
				return;
			float hi = Math.min(h, 19);
			g.drawText(value, 3, (h - hi) * 0.5f, w - 7, hi - 5);
		}

		@Override
		protected String getTooltip() {
			return data.apply(getLayoutDataAs(IRow.class, null));
		}
	}
}
