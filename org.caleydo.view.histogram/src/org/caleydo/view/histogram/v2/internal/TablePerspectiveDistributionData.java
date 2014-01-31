/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.histogram.v2.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.collection.CategoricalHistogram;
import org.caleydo.core.data.collection.Histogram;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.TablePerspectiveSelectionMixin;
import org.caleydo.core.data.selection.TablePerspectiveSelectionMixin.ITablePerspectiveMixinCallback;
import org.caleydo.core.event.EventListenerManager.DeepScan;
import org.caleydo.core.view.opengl.layout2.GLElement;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
/**
 * Rendering the distribution of a categorical element in various forms
 *
 * @author Samuel Gratzl
 */
public class TablePerspectiveDistributionData implements IDistributionData, ITablePerspectiveMixinCallback {
	@DeepScan
	protected final TablePerspectiveSelectionMixin selections;

	private int histTotal;
	private List<DistributionEntry> entries;

	private GLElement callback;

	public TablePerspectiveDistributionData(TablePerspective tablePerspective) {
		this.selections = new TablePerspectiveSelectionMixin(tablePerspective, this);
		onVAUpdate(tablePerspective);
	}

	@Override
	public void onChange(GLElement callback) {
		this.callback = callback;
	}

	public final TablePerspective getTablePerspective() {
		return selections.getTablePerspective();
	}

	@Override
	public <T> T getLayoutDataAs(Class<T> clazz, T default_) {
		return getLayoutDataAs(clazz, Suppliers.ofInstance(default_));
	}

	@Override
	public <T> T getLayoutDataAs(Class<T> clazz, Supplier<? extends T> default_) {
		if (clazz.isInstance(getTablePerspective()))
			return clazz.cast(getTablePerspective());
		if (clazz.isInstance(getDataDomain()))
			return clazz.cast(getDataDomain());
		return default_.get();
	}

	public final ATableBasedDataDomain getDataDomain() {
		return getTablePerspective().getDataDomain();
	}

	@Override
	public void onSelectionUpdate(SelectionManager manager) {
		callback.repaint();
	}

	@Override
	public void onVAUpdate(TablePerspective tablePerspective) {
		Histogram hist = tablePerspective.getContainerStatistics().getHistogram();
		assert hist instanceof CategoricalHistogram;
		CategoricalHistogram chist = (CategoricalHistogram) hist;

		float factor;
		if (tablePerspective.getParentTablePerspective() != null)
			factor = 1.f / tablePerspective.getParentTablePerspective().getContainerStatistics().getHistogram()
					.getLargestValue();
		else
			factor = 1.f / hist.getLargestValue();

		int total = 0;
		this.entries = new ArrayList<DistributionEntry>(hist.size());
		for (int i = 0; i < hist.size(); ++i) {
			total += hist.get(i);
			float value = hist.get(i) * factor;
			entries.add(new DistributionEntry(chist.getName(i), chist.getColor(i), value, hist.getIDsForBucket(i)));
		}
		total += hist.getNanCount();
		histTotal = total;


		callback.repaintAll();
	}

	@Override
	public DistributionEntry getOf(int dataIndex) {
		return null;
	}

	@Override
	public DistributionEntry get(int entry) {
		return entries.get(entry);
	}

	@Override
	public List<DistributionEntry> getEntries() {
		return entries;
	}

	@Override
	public void select(Collection<Integer> ids, SelectionType selectionType, boolean clear) {
		SelectionManager manager = selections.getRecordSelectionManager();
		if (clear)
			manager.clearSelection(selectionType);
		manager.addToType(selectionType, ids);
		selections.fireRecordSelectionDelta();
	}


	@Override
	public int size() {
		return histTotal;
	}

	@Override
	public Set<Integer> getElements(SelectionType selectionType) {
		return selections.getRecordSelectionManager().getElements(selectionType);
	}
}
