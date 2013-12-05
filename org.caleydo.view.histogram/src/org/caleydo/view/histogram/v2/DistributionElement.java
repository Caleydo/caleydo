/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.histogram.v2;

import gleem.linalg.Vec2f;

import java.util.Collection;
import java.util.Set;

import org.caleydo.core.data.collection.CategoricalHistogram;
import org.caleydo.core.data.collection.Histogram;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.TablePerspectiveSelectionMixin;
import org.caleydo.core.event.EventListenerManager.DeepScan;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.picking.IPickingLabelProvider;

import com.google.common.base.Supplier;

/**
 * Rendering the distribution of a categorical element in various forms
 *
 * @author Samuel Gratzl
 */
public class DistributionElement extends ADistributionElement implements IPickingLabelProvider,
		TablePerspectiveSelectionMixin.ITablePerspectiveMixinCallback {
	@DeepScan
	protected final TablePerspectiveSelectionMixin selections;

	private CategoricalHistogram hist;
	private int histTotal;

	public DistributionElement(TablePerspective tablePerspective, EDistributionMode mode) {
		super(mode);
		this.selections = new TablePerspectiveSelectionMixin(tablePerspective, this);
		onVAUpdate(tablePerspective);
	}


	public final TablePerspective getTablePerspective() {
		return selections.getTablePerspective();
	}

	@Override
	public <T> T getLayoutDataAs(Class<T> clazz, Supplier<? extends T> default_) {
		if (clazz.isAssignableFrom(Vec2f.class))
			return clazz.cast(getMinSize());
		if (clazz.isInstance(getTablePerspective()))
			return clazz.cast(getTablePerspective());
		if (clazz.isInstance(getDataDomain()))
			return clazz.cast(getDataDomain());
		return super.getLayoutDataAs(clazz, default_);
	}

	public final ATableBasedDataDomain getDataDomain() {
		return getTablePerspective().getDataDomain();
	}

	@Override
	public void onSelectionUpdate(SelectionManager manager) {
		repaint();
	}

	@Override
	public void onVAUpdate(TablePerspective tablePerspective) {
		Histogram hist = tablePerspective.getContainerStatistics().getHistogram();
		assert hist instanceof CategoricalHistogram;
		this.hist = (CategoricalHistogram) hist;

		int total = 0;
		for (int i = 0; i < hist.size(); ++i) {
			total += hist.get(i);
		}
		total += hist.getNanCount();
		histTotal = total;

		if (bucketPickingIds != null)
			bucketPickingIds.ensure(0, hist.size());

		repaintAll();
	}

	@Override
	protected void select(Collection<Integer> ids, SelectionType selectionType, boolean clear) {
		SelectionManager manager = selections.getRecordSelectionManager();
		if (clear)
			manager.clearSelection(selectionType);
		manager.addToType(selectionType, ids);
		selections.fireRecordSelectionDelta();
	}

	@Override
	protected Histogram getHist() {
		return hist;
	}

	@Override
	protected String getBinName(int bin) {
		return hist.getName(bin);
	}

	@Override
	protected Color getBinColor(int bin) {
		return hist.getColor(bin);
	}

	@Override
	protected int size() {
		return histTotal;
	}

	@Override
	protected Set<Integer> getElements(SelectionType selectionType) {
		return selections.getRecordSelectionManager().getElements(selectionType);
	}
}
