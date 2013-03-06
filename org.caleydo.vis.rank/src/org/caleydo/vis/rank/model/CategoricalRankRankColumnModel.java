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
import gleem.linalg.Vec2f;
import gleem.linalg.Vec4f;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
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
import org.caleydo.core.io.gui.dataimport.widget.ICallback;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.vis.rank.event.FilterEvent;
import org.caleydo.vis.rank.model.mapping.ICategoricalMappingFunction;
import org.caleydo.vis.rank.model.mixin.IMappedColumnMixin;
import org.caleydo.vis.rank.model.mixin.IRankableColumnMixin;
import org.caleydo.vis.rank.ui.GLPropertyChangeListeners;
import org.caleydo.vis.rank.ui.IColumnRenderInfo;
import org.caleydo.vis.rank.ui.RenderUtils;
import org.caleydo.vis.rank.ui.detail.CategoricalScoreBarRenderer;
import org.caleydo.vis.rank.ui.mapping.MappingFunctionUIs;
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
 * a special categorical column with at most {@link #MAX_CATEGORY_COLORS} colors which supports that this column is part
 * of the ranking
 *
 * @author Samuel Gratzl
 *
 */
public class CategoricalRankRankColumnModel<CATEGORY_TYPE> extends ABasicFilterableRankColumnModel implements
		IMappedColumnMixin, IRankableColumnMixin {
	private static final int MAX_CATEGORY_COLORS = 8;

	private final Function<IRow, CATEGORY_TYPE> data;
	private final Set<CATEGORY_TYPE> selection = new HashSet<>();
	private final Map<CATEGORY_TYPE, CategoryInfo> metaData;
	private final ICategoricalMappingFunction<CATEGORY_TYPE> mapping;

	private SimpleHistogram cacheHist = null;
	private Map<CATEGORY_TYPE, Integer> cacheValueHist = null;

	private final PropertyChangeListener listener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			switch (evt.getPropertyName()) {
			case IRankColumnParent.PROP_INVALID:
				cacheHist = null;
				cacheValueHist = null;
				break;
			}
		}
	};
	private final ICallback<Object> callback = new ICallback<Object>() {
		@Override
		public void on(Object data) {
			cacheHist = null;
			cacheValueHist = null;
			invalidAllFilter();
			propertySupport.firePropertyChange(PROP_MAPPING, null, data);
		}
	};

	private final CategoricalScoreBarRenderer valueRenderer = new CategoricalScoreBarRenderer(this);

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
	protected void init(IRankColumnParent parent) {
		parent.addPropertyChangeListener(IRankColumnParent.PROP_INVALID, listener);
		super.init(parent);
	}

	@Override
	protected void takeDown() {
		parent.removePropertyChangeListener(IRankColumnParent.PROP_INVALID, listener);
		super.takeDown();
	}

	@Override
	public GLElement createSummary(boolean interactive) {
		return new MyElement(interactive);
	}

	@Override
	public GLElement createValue() {
		return new GLElement(valueRenderer);
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
	public float applyPrimitive(IRow in) {
		return mapping.applyPrimitive(getCatValue(in));
	}

	@Override
	public Float apply(IRow in) {
		return applyPrimitive(in);
	}

	@Override
	public SimpleHistogram getHist(int bins) {
		if (cacheHist != null && cacheHist.size() == bins)
			return cacheHist;
		return cacheHist = DataUtils.getHist(bins, parent.getCurrentOrder(), this);
	}

	public Map<CATEGORY_TYPE, Integer> getHist() {
		if (cacheValueHist != null)
			return cacheValueHist;
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
			tableColumn.getColumn().setText("Color");
			tableColumn.getColumn().setWidth(50);
			tableColumn.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public org.eclipse.swt.graphics.Color getBackground(Object element) {
					@SuppressWarnings("unchecked")
					CATEGORY_TYPE k = (CATEGORY_TYPE) element;
					return toSWT(metaData.get(k).getColor());
				}
			});
			tableColumn = new TableViewerColumn(categoriesUI, SWT.LEAD);
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

			sc.setContent(categoriesUI.getTable());
			Point point = categoriesUI.getTable().computeSize(SWT.DEFAULT, SWT.DEFAULT);
			categoriesUI.getTable().setSize(point);
			sc.setMinSize(point);

			applyDialogFont(parent);
			return parent;
		}

		protected org.eclipse.swt.graphics.Color toSWT(Color color) {
			return new org.eclipse.swt.graphics.Color(getShell().getDisplay(), color.getRed(), color.getGreen(),
					color.getBlue());
		}

		@Override
		protected void okPressed() {
			Set<Object> r = new HashSet<>();
			for (Object score : categoriesUI.getCheckedElements()) {
				r.add(score);
			}
			publishEvent(new FilterEvent(r).to(CategoricalRankRankColumnModel.this));
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
			editFilter(this, context);
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

	@Override
	public void editMapping(GLElement summary, IGLElementContext context) {
		GLElement m = MappingFunctionUIs.create(mapping, getHist(), metaData, bgColor, callback);
		m.setzDelta(0.5f);
		Vec2f location = summary.getAbsoluteLocation();
		Vec2f size = summary.getSize();
		context.getPopupLayer().show(m, new Vec4f(location.x(), location.y() + size.y(), 260, 260));
	}
}
