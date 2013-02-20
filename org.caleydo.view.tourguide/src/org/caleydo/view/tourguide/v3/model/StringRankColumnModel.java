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
package org.caleydo.view.tourguide.v3.model;

import static org.caleydo.core.event.EventListenerManager.triggerEvent;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.view.tourguide.v3.data.IDataProvider;
import org.caleydo.view.tourguide.v3.event.FilterEvent;
import org.caleydo.view.tourguide.v3.model.mixin.IFilterColumnMixin;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
/**
 * @author Samuel Gratzl
 *
 */
public class StringRankColumnModel extends ABasicRankColumnModel implements IFilterColumnMixin {
	public static IDataProvider<String> TO_STRING = new IDataProvider<String>() {
		@Override
		public String apply(IRow row) {
			return Objects.toString(row);
		}

		@Override
		public void prepareFor(Collection<IRow> data) {

		}
	};

	private final IDataProvider<String> data;
	private String filter;
	private boolean removeAble;

	private BitSet cacheFilter = null;
	private final PropertyChangeListener listerner = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			switch (evt.getPropertyName()) {
			case RankTableModel.PROP_DATA:
				@SuppressWarnings("unchecked")
				Collection<IRow> news = (Collection<IRow>) evt.getNewValue();
				data.prepareFor(news);
				cacheFilter = null;
				break;
			}
		}
	};

	public StringRankColumnModel(IGLRenderer header, final IDataProvider<String> data,
			boolean removeAble) {
		super(Color.GRAY, new Color(.95f, .95f, .95f));
		setHeaderRenderer(header);
		setValueRenderer(new IGLRenderer() {
			@Override
			public void render(GLGraphics g, float w, float h, GLElement parent) {
				if (h < 5)
					return;
				String value = data.apply(parent.getLayoutDataAs(IRow.class, null));
				if (value == null)
					return;
				float hi = Math.min(h, 18);
				g.drawText(value, 1, 1 + (h - hi) * 0.5f, w - 2, hi - 2);
			}
		});
		this.data = data;
		this.removeAble = removeAble;
	}

	@Override
	protected void init(RankTableModel table) {
		table.addPropertyChangeListener(RankTableModel.PROP_DATA, listerner);
		this.data.prepareFor(table.getData());
		super.init(table);
	}

	@Override
	protected void takeDown(RankTableModel table) {
		table.removePropertyChangeListener(RankTableModel.PROP_DATA, listerner);
		super.takeDown(table);
	}

	@Override
	public boolean isDestroyAble() {
		return super.isDestroyAble() && removeAble;
	}

	@Override
	public GLElement createSummary() {
		return new GLElement(GLRenderers.fillRect(new Color(0.95f, 0.95f, 0.95f)));
	}

	@Override
	public void editFilter() {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				InputDialog d = new InputDialog(null, "Filter column: " + getHeaderRenderer().toString(),
						"Edit Filter", filter, null);
				if (d.open() == Window.OK) {
					String v = d.getValue().trim();
					if (v.length() == 0)
						v = null;
					triggerEvent(new FilterEvent(v).to(StringRankColumnModel.this));
				}
			}
		});
	}

	@ListenTo(sendToMe = true)
	private void onSetFilter(FilterEvent event) {
		cacheFilter = null;
		propertySupport.firePropertyChange(PROP_FILTER, this.filter, this.filter = (String) event.getFilter());
	}

	@Override
	public boolean isFiltered() {
		return filter != null;
	}

	@Override
	public BitSet getSelectedRows(List<IRow> rows) {
		if (cacheFilter != null)
			return cacheFilter;
		BitSet b = new BitSet(rows.size());
		if (filter == null) {
			b.set(0, rows.size());
		} else {
			String regex = Pattern.quote(filter);
			int i = 0;
			for (IRow row : rows) {
				String v = data.apply(row);
				b.set(i++, Pattern.matches(regex, v));
			}
		}
		cacheFilter = b;
		return b;
	}
}
