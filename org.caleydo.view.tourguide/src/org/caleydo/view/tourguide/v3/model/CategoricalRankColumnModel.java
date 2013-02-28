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
import java.beans.PropertyChangeListener;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.tourguide.v3.event.FilterEvent;
import org.caleydo.view.tourguide.v3.ui.GLPropertyChangeListeners;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

import com.google.common.base.Function;

/**
 * @author Samuel Gratzl
 *
 */
public class CategoricalRankColumnModel<CATEGORY_TYPE> extends ABasicFilterableRankColumnModel implements IGLRenderer {
	private final Function<IRow, CATEGORY_TYPE> data;
	private Set<CATEGORY_TYPE> selection = new HashSet<>();
	private Map<CATEGORY_TYPE, CategoryInfo> metaData;

	public CategoricalRankColumnModel(IGLRenderer header, final Function<IRow, CATEGORY_TYPE> data, Map<CATEGORY_TYPE, CategoryInfo> metaData) {
		super(Color.GRAY, new Color(.95f, .95f, .95f));
		setHeaderRenderer(header);
		this.data = data;
		this.metaData = metaData;
		this.selection.addAll(metaData.keySet());
	}

	@Override
	protected void init(RankTableModel table) {
		super.init(table);
	}

	@Override
	protected void takeDown(RankTableModel table) {
		super.takeDown(table);
	}

	@Override
	public GLElement createSummary(boolean interactive) {
		return new MyElement(interactive);
	}

	@Override
	public GLElement createValue() {
		return new GLElement(this);
	}

	@Override
	public void render(GLGraphics g, float w, float h, GLElement parent) {
		if (h < 5)
			return;
		CATEGORY_TYPE value = data.apply(parent.getLayoutDataAs(IRow.class, null));
		if (value == null)
			return;
		CategoryInfo info = metaData.get(value);
		if (info == null)
			return;
		float hi = Math.min(h, 18);
		g.color(info.getColor()).fillRect(1, 1, w - 2, h - 2);
		if (w > COLLAPSED_WIDTH) { // FIXME is Collapsed really not approximated
			g.drawText(info.getLabel(), 1, 1 + (h - hi) * 0.5f, w - 2, hi - 2);
		}
	}

	@Override
	public final void editFilter(GLElement summary) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				new CategoricalFilterDialog(new Shell()).open();
			}
		});
	}

	@SuppressWarnings("unchecked")
	@ListenTo(sendToMe = true)
	private void onSetFilter(FilterEvent event) {
		invalidAllFilter();
		Set<CATEGORY_TYPE> bak = new HashSet<>(this.selection);
		this.selection.clear();
		this.selection.addAll((Collection<CATEGORY_TYPE>) event.getFilter());
		propertySupport.firePropertyChange(PROP_FILTER, bak, this.selection);
	}

	@Override
	public boolean isFiltered() {
		return selection.size() < metaData.size();
	}

	@Override
	protected void updateMask(BitSet todo, List<IRow> data, BitSet mask) {
		for (int i = todo.nextSetBit(0); i >= 0; i = todo.nextSetBit(i + 1)) {
			CATEGORY_TYPE v = this.data.apply(data.get(i));
			mask.set(i, selection.contains(v));
		}
	}

	private class CategoricalFilterDialog extends Dialog {
		// the visual selection widget group
		private CheckboxTableViewer categoriesUI;

		public CategoricalFilterDialog(Shell shell) {
			super(shell);
		}

		@Override
		public void create() {
			super.create();
			getShell().setText("Edit Filter of " + getHeaderRenderer());
			this.setBlockOnOpen(false);
		}

		@Override
		protected Control createDialogArea(Composite parent) {
			parent = (Composite) super.createDialogArea(parent);

			this.categoriesUI = CheckboxTableViewer.newCheckList(parent, SWT.BORDER | SWT.FULL_SELECTION);
			categoriesUI.getTable().setLayoutData(
					new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
			Table table = categoriesUI.getTable();
			table.setHeaderVisible(true);
			table.setLinesVisible(true);
			table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
			TableViewerColumn tableColumn = new TableViewerColumn(categoriesUI, SWT.LEAD);
			tableColumn.getColumn().setText("Category");
			tableColumn.getColumn().setWidth(200);
			tableColumn.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					@SuppressWarnings("unchecked")
					CATEGORY_TYPE k = (CATEGORY_TYPE) element;
					return metaData.get(k).getLabel();
				}
			});
			categoriesUI.setContentProvider(ArrayContentProvider.getInstance());
			categoriesUI.setInput(metaData.keySet());
			for (Object s : selection) {
				categoriesUI.setChecked(s, true);
			}
			applyDialogFont(parent);
			return parent;
		}

		@Override
		protected void okPressed() {
			Set<Object> r = new HashSet<>();
			for (Object score : categoriesUI.getCheckedElements()) {
				r.add(score);
			}
			triggerEvent(new FilterEvent(r).to(CategoricalRankColumnModel.this));
			super.okPressed();
		}
	}

	private class MyElement extends PickableGLElement {
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
		protected void onMouseReleased(Pick pick) {
			if (pick.isAnyDragging())
				return;
			editFilter(this);
		}

		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			super.renderImpl(g, w, h);
			if (w < COLLAPSED_WIDTH)
				return;
			// g.drawText("Filter:", 4, 2, w - 4, 12);
			// String t = "<None>";
			// if (filter != null)
			// t = filter;
			// g.drawText(t, 4, 18, w - 4, 12);
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

	}
}
