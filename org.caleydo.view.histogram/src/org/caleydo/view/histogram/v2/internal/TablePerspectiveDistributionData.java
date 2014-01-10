/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.histogram.v2.internal;

import java.util.Collection;
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
import org.caleydo.core.util.color.Color;
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

	private CategoricalHistogram hist;
	private int histTotal;

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
		this.hist = (CategoricalHistogram) hist;

		int total = 0;
		for (int i = 0; i < hist.size(); ++i) {
			total += hist.get(i);
		}
		total += hist.getNanCount();
		histTotal = total;

		callback.repaintAll();
	}

	@Override
	public int getBinOf(int dataIndex) {
		return -1;
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
	public Histogram getHist() {
		return hist;
	}

	@Override
	public String getBinName(int bin) {
		return hist.getName(bin);
	}

	@Override
	public Color getBinColor(int bin) {
		return hist.getColor(bin);
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
