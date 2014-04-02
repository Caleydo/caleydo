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
import org.caleydo.core.util.function.Function2;
import org.caleydo.view.heatmap.v2.internal.IHeatMapDataProvider;

import com.google.common.base.Function;
import com.jogamp.common.util.IntIntHashMap;

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
	private final Function2<? super Integer, ? super Integer, String> cell2label;

	private IDataChangedCallback callback;


	public ListDataProvider(DimensionData record, DimensionData dimension,
			Function2<? super Integer, ? super Integer, String> cell2label) {
		this.record = record;
		this.dimension = dimension;
		this.cell2label = cell2label;
		selections = new MultiSelectionManagerMixin(this);
		if (dimension != null && dimension.idType != null)
			selections.add(new SelectionManager(dimension.idType));
		if (record != null && record.idType != null)
			selections.add(new SelectionManager(record.idType));
	}

	@Override
	public String getLabel(Integer recordID, Integer dimensionID) {
		if (cell2label == null)
			return "";
		return cell2label.apply(recordID, dimensionID);
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
		return get(dim).indexOf(id);
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
		return selections.get(get(dim).idType);
	}

	@Override
	public void fireSelectionChanged(SelectionManager manager) {
		selections.fireSelectionDelta(manager);
	}

	public static class DimensionData {
		private final List<Integer> data;
		private final IntIntHashMap toIndex;
		private final Function<? super Integer, String> labels;
		private final List<Group> groups;
		private final IDType idType;

		public DimensionData(List<Integer> data, Function<? super Integer, String> labels, List<Group> groups,
				IDType idType) {
			this.data = data;
			this.labels = labels;
			this.groups = groups;
			this.idType = idType;

			if (data.size() > 2048) {
				toIndex = buildCache(data);
			} else
				toIndex = null;
		}

		/**
		 * @param data2
		 * @return
		 */
		private IntIntHashMap buildCache(List<Integer> ids) {
			IntIntHashMap m = new IntIntHashMap(ids.size());
			for (int i = ids.size() - 1; i >= 0; --i) {
				Integer id = ids.get(i);
				if (id == null)
					continue;
				m.put(id.intValue(), i);
			}
			return m;
		}

		/**
		 * @param id
		 * @return
		 */
		public int indexOf(Integer id) {
			if (toIndex == null)
				return data.indexOf(id);
			if (id == null)
				return -1;
			return toIndex.get(id.intValue());
		}
	}
}
