/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.idbrowser.internal.model;

import gleem.linalg.Vec2f;

import java.util.BitSet;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.ISWTLayer.ISWTLayerRunnable;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.vis.lineup.event.FilterEvent;
import org.caleydo.vis.lineup.model.ABasicFilterableRankColumnModel;
import org.caleydo.vis.lineup.model.IRow;
import org.caleydo.vis.lineup.ui.AFilterDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.jogamp.common.util.IntObjectHashMap;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class ADataDomainRankTableModel extends ABasicFilterableRankColumnModel {
	protected final ATableBasedDataDomain d;
	protected final EDimension dim;

	protected final IntObjectHashMap cache = new IntObjectHashMap();

	protected final VirtualArray others;

	private boolean filterMissingEntries = false;
	/**
	 * @param d
	 * @param dim
	 */
	public ADataDomainRankTableModel(ATableBasedDataDomain d, EDimension dim) {
		super(Color.GRAY, new Color(0.95f, .95f, .95f));
		this.d = d;
		this.dim = dim;
		setHeaderRenderer(GLRenderers.drawText(d.getLabel(), VAlign.CENTER));

		final Table table = d.getTable();
		this.others = dim.opposite()
				.select(table.getDefaultDimensionPerspective(false), table.getDefaultRecordPerspective(false))
				.getVirtualArray();
	}

	public ADataDomainRankTableModel(TablePerspective t, EDimension dim) {
		super(Color.GRAY, new Color(0.95f, .95f, .95f));
		this.d = t.getDataDomain();
		this.dim = dim;
		setHeaderRenderer(GLRenderers.drawText(d.getLabel(), VAlign.CENTER));

		this.others = dim.opposite().select(t.getDimensionPerspective(), t.getRecordPerspective()).getVirtualArray();
	}

	/**
	 * @param distributionRankTableModel
	 */
	public ADataDomainRankTableModel(ADataDomainRankTableModel clone) {
		super(clone);
		this.d = clone.d;
		this.dim = clone.dim;
		this.others = clone.others;
		this.cache.putAll(clone.cache);
		setHeaderRenderer(GLRenderers.drawText(d.getLabel(), VAlign.CENTER));
	}

	public final IDType getIDType() {
		return dim.select(d.getDimensionIDType(), d.getRecordIDType());
	}

	private boolean hasValue(IRow row) {
		Set<Object> ids = ((IIDRow) row).get(getIDType());
		if (ids == null || ids.isEmpty())
			return false;
		return true;
	}

	@Override
	public GLElement createSummary(boolean interactive) {
		return new SummaryElement();
	}

	@Override
	public boolean isFiltered() {
		return filterMissingEntries;
	}

	public void setFilter(boolean filterMissing, boolean isFilterGlobally, boolean isRankIndependentFilter) {
		if (this.filterMissingEntries == filterMissing && this.isGlobalFilter == isFilterGlobally
				&& this.isRankIndependentFilter == isRankIndependentFilter)
			return;
		invalidAllFilter();
		this.filterMissingEntries = filterMissing;
		this.isGlobalFilter = isFilterGlobally;
		this.isRankIndependentFilter = isRankIndependentFilter;
		propertySupport.firePropertyChange(PROP_FILTER, false, true);
	}

	/**
	 * @return the filterMissingEntries, see {@link #filterMissingEntries}
	 */
	public boolean isFilterMissingEntries() {
		return filterMissingEntries;
	}

	@Override
	public void editFilter(final GLElement summary, IGLElementContext context) {
		final Vec2f location = summary.getAbsoluteLocation();
		context.getSWTLayer().run(new ISWTLayerRunnable() {
			@Override
			public void run(Display display, Composite canvas) {
				Point loc = canvas.toDisplay((int) location.x(), (int) location.y());
				FilterDialog dialog = new FilterDialog(canvas.getShell(), getLabel(), summary,
						ADataDomainRankTableModel.this,
						getTable()
								.hasSnapshots(), loc);
				dialog.open();
			}
		});
	}

	@Override
	protected void updateMask(BitSet todo, List<IRow> rows, BitSet mask) {
		for (int i = todo.nextSetBit(0); i >= 0; i = todo.nextSetBit(i + 1)) {
			boolean has = hasValue(rows.get(i));
			mask.set(i, !filterMissingEntries || has);
		}
	}

	private static class FilterDialog extends AFilterDialog {
		private final boolean filterMissing;
		private Button filterNotMappedUI;

		public FilterDialog(Shell parentShell, String title, Object receiver, ADataDomainRankTableModel model,
				boolean hasSnapshots, Point loc) {
			super(parentShell, title, receiver, model, hasSnapshots, loc);
			this.filterMissing = model.isFilterMissingEntries();
		}

		@Override
		public void create() {
			super.create();
			getShell().setText("Edit Filter of size");
		}

		@Override
		protected void createSpecificFilterUI(Composite composite) {
			filterNotMappedUI = new Button(composite, SWT.CHECK);
			filterNotMappedUI.setText("Filter missing entries?");
			filterNotMappedUI.setLayoutData(twoColumns(new GridData(SWT.LEFT, SWT.CENTER, true, true)));
			filterNotMappedUI.setSelection(filterMissing);

			SelectionAdapter adapter = new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					triggerEvent(false);
				}
			};
			filterNotMappedUI.addSelectionListener(adapter);
			addButtonAndOption(composite);
		}

		@Override
		protected void triggerEvent(boolean cancel) {
			if (cancel) // original values
				EventPublisher.trigger(new FilterEvent(null, filterMissing, filterGlobally, filterRankIndependent)
						.to(receiver));
			else {
				EventPublisher.trigger(new FilterEvent(null, filterNotMappedUI.getSelection(), isFilterGlobally(),
						isFilterRankIndependent())
						.to(receiver));
			}
		}
	}

	private class SummaryElement extends GLElement {
		@ListenTo(sendToMe = true)
		private void onFilterEvent(FilterEvent event) {
			setFilter(event.isFilterNA(), event.isFilterGlobally(), event.isFilterRankIndendent());
		}
	}
}
