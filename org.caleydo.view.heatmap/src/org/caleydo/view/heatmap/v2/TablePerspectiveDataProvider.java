/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.heatmap.v2;

import java.util.List;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.TablePerspectiveSelectionMixin;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.event.EventListenerManager.DeepScan;
import org.caleydo.core.view.opengl.layout2.layout.IHasGLLayoutData;
import org.caleydo.view.heatmap.v2.internal.IHeatMapDataProvider;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

/**
 * @author Samuel Gratzl
 *
 */
public class TablePerspectiveDataProvider implements IHeatMapDataProvider,
		TablePerspectiveSelectionMixin.ITablePerspectiveMixinCallback, IHasGLLayoutData {
	@DeepScan
	private final TablePerspectiveSelectionMixin selections;
	private IDataChangedCallback callback;


	public TablePerspectiveDataProvider(TablePerspective tablePerspective) {
		selections = new TablePerspectiveSelectionMixin(tablePerspective, this);
	}

	@Override
	public <T> T getLayoutDataAs(Class<T> clazz, Supplier<? extends T> default_) {
		if (clazz.isInstance(this))
			return clazz.cast(this);
		if (clazz.isInstance(getDataDomain()))
			return clazz.cast(getDataDomain());
		if (clazz.isInstance(getTablePerspective()))
			return clazz.cast(getTablePerspective());
		return default_.get();
	}

	@Override
	public <T> T getLayoutDataAs(Class<T> clazz, T default_) {
		return getLayoutDataAs(clazz, Suppliers.ofInstance(default_));
	}

	@Override
	public List<Integer> getData(EDimension dim) {
		return getVA(dim).getIDs();
	}

	private VirtualArray getVA(EDimension dim) {
		TablePerspective t = getTablePerspective();
		return (dim.isDimension() ? t.getDimensionPerspective() : t.getRecordPerspective()).getVirtualArray();
	}

	@Override
	public List<Group> getGroups(EDimension dim) {
		return getVA(dim).getGroupList().getGroups();
	}

	@Override
	public int indexOf(EDimension dim, Integer id) {
		return getVA(dim).indexOf(id);
	}

	@Override
	public String getLabel(EDimension dim, Integer id) {
		if (dim.isRecord())
			return getDataDomain().getRecordLabel(id);
		else
			return getDataDomain().getDimensionLabel(id);
	}

	@Override
	public void setCallback(IDataChangedCallback callback) {
		this.callback = callback;
	}

	@Override
	public void onVAUpdate(TablePerspective tablePerspective) {
		if (callback != null)
			callback.onDataUpdate();
	}

	public final TablePerspective getTablePerspective() {
		return selections.getTablePerspective();
	}

	public final ATableBasedDataDomain getDataDomain() {
		return getTablePerspective().getDataDomain();
	}

	@Override
	public void onSelectionUpdate(SelectionManager manager) {
		if (callback != null)
			callback.onSelectionUpdate();
	}

	@Override
	public SelectionManager getManager(EDimension dim) {
		return dim.isDimension() ? selections.getDimensionSelectionManager() : selections.getRecordSelectionManager();
	}
}
