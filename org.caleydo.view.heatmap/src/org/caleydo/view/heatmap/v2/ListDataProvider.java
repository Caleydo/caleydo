/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.heatmap.v2;

import java.util.List;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.data.selection.MultiSelectionManagerMixin;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.event.EventListenerManager.DeepScan;
import org.caleydo.core.id.IDType;
import org.caleydo.view.heatmap.v2.internal.IHeatMapDataProvider;

import com.google.common.base.Function;

/**
 * @author Samuel Gratzl
 *
 */
public class ListDataProvider implements IHeatMapDataProvider,
 MultiSelectionManagerMixin.ISelectionMixinCallback {
	@DeepScan
	private final MultiSelectionManagerMixin selections;
	private final DimensionData record;
	private final DimensionData dimension;

	private IDataChangedCallback callback;


	public ListDataProvider(DimensionData record, DimensionData dimension) {
		this.record = record;
		this.dimension = dimension;
		selections = new MultiSelectionManagerMixin(this);
		selections.add(new SelectionManager(dimension.idType));
		selections.add(new SelectionManager(record.idType));
	}

	@Override
	public List<Integer> getData(EDimension dim) {
		return get(dim).data;
	}

	private DimensionData get(EDimension dim) {
		return dim.select(dimension, record);
	}

	@Override
	public List<Group> getGroups(EDimension dim) {
		return get(dim).groups;
	}

	@Override
	public int indexOf(EDimension dim, Integer id) {
		return getData(dim).indexOf(id);
	}

	@Override
	public String getLabel(EDimension dim, Integer id) {
		return get(dim).labels.apply(id);
	}

	@Override
	public void setCallback(IDataChangedCallback callback) {
		this.callback = callback;
	}

	@Override
	public void onSelectionUpdate(SelectionManager manager) {
		if (callback != null)
			callback.onSelectionUpdate();
	}

	@Override
	public SelectionManager getManager(EDimension dim) {
		return selections.get(dim.select(0, 1));
	}

	@Override
	public void fireSelectionChanged(SelectionManager manager) {
		selections.fireSelectionDelta(manager);
	}

	public static class DimensionData {
		private final List<Integer> data;
		private final Function<Integer, String> labels;
		private final List<Group> groups;
		private final IDType idType;

		public DimensionData(List<Integer> data, Function<Integer, String> labels, List<Group> groups, IDType idType) {
			this.data = data;
			this.labels = labels;
			this.groups = groups;
			this.idType = idType;
		}
	}
}
