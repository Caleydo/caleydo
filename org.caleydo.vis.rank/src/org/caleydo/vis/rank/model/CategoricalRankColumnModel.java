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

import static org.caleydo.core.event.EventPublisher.publishEvent;

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import org.caleydo.vis.rank.event.FilterEvent;
import org.caleydo.vis.rank.ui.GLPropertyChangeListeners;
import org.caleydo.vis.rank.ui.IColumnRenderInfo;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
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
	private Map<CATEGORY_TYPE, String> metaData;

	public CategoricalRankColumnModel(IGLRenderer header, final Function<IRow, CATEGORY_TYPE> data,
			Map<CATEGORY_TYPE, String> metaData) {
		super(Color.GRAY, new Color(.95f, .95f, .95f));
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
		CATEGORY_TYPE value = getCatValue(parent.getLayoutDataAs(IRow.class, null));
		if (value == null)
			return;
		String info = metaData.get(value);
		if (info == null)
			return;
		float hi = Math.min(h, 18);
		if (!(((IColumnRenderInfo) parent.getParent()).isCollapsed())) {
			g.drawText(info, 1, 1 + (h - hi) * 0.5f, w - 2, hi - 5);
		}
	}

	@Override
	public final void editFilter(GLElement summary, IGLElementContext context) {
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

	public CATEGORY_TYPE getCatValue(IRow row) {
		return data.apply(row);
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

			ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
			sc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			sc.setExpandVertical(false);
			sc.setExpandHorizontal(false);

			this.categoriesUI = CheckboxTableViewer.newCheckList(sc, SWT.BORDER | SWT.FULL_SELECTION);
			categoriesUI.getTable().setLayoutData(
					new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
			Table table = categoriesUI.getTable();
			table.setHeaderVisible(true);
			table.setLinesVisible(true);
			table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

			TableViewerColumn tableColumn;
			tableColumn = new TableViewerColumn(categoriesUI, SWT.LEAD);
			tableColumn.getColumn().setText("Category");
			tableColumn.getColumn().setWidth(200);
			tableColumn.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					@SuppressWarnings("unchecked")
					CATEGORY_TYPE k = (CATEGORY_TYPE) element;
					return metaData.get(k);
				}
			});
			categoriesUI.setContentProvider(ArrayContentProvider.getInstance());
			categoriesUI.setInput(metaData.keySet());
			for (Object s : selection) {
				categoriesUI.setChecked(s, true);
			}

			sc.setContent(categoriesUI.getTable());
			Point point = categoriesUI.getTable().computeSize(SWT.DEFAULT, SWT.DEFAULT);
			categoriesUI.getTable().setSize(point);
			sc.setMinSize(point);

			applyDialogFont(parent);
			return parent;
		}

		@Override
		protected void okPressed() {
			Set<Object> r = new HashSet<>();
			for (Object score : categoriesUI.getCheckedElements()) {
				r.add(score);
			}
			publishEvent(new FilterEvent(r).to(CategoricalRankColumnModel.this));
			super.okPressed();
		}
	}

	/**
	 * @return
	 */
	public Map<CATEGORY_TYPE, Integer> getHist() {
		Map<CATEGORY_TYPE, Integer> hist = new HashMap<>();
		for(Iterator<IRow> it = parent.getCurrentOrder(); it.hasNext(); ) {
			CATEGORY_TYPE v = getCatValue(it.next());
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
			editFilter(this, context);
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
	}
}