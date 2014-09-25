/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.histogram.v2.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.collection.Histogram;
import org.caleydo.core.data.selection.MultiSelectionManagerMixin;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.event.EventListenerManager.DeepScan;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.ColorBrewer;
import org.caleydo.core.view.opengl.layout2.GLElement;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

/**
 * a distribution renderer based on a {@link Histogram}
 *
 * @author Samuel Gratzl
 *
 */
public class HistDistributionData implements IDistributionData, MultiSelectionManagerMixin.ISelectionMixinCallback {
	@DeepScan
	protected final MultiSelectionManagerMixin selections;

	private final int size;
		private final List<Integer> ids;

	private GLElement callback;

	private List<DistributionEntry> entries;

	public HistDistributionData(Histogram hist, int largestBinSize, IDType idType, String[] labels,
 Color[] colors,
			List<Integer> ids, int size) {
		this.ids = ids;
		this.size = size < 0 ? size(hist) : size;
		this.entries = toDistributions(toColors(hist.size(), colors), toLabels(hist.size(), labels), hist, largestBinSize);
		if (idType == null) {
			this.selections = null;
		} else {
			this.selections = new MultiSelectionManagerMixin(this);
			this.selections.add(new SelectionManager(idType));
		}
	}

	@Override
	public boolean hasIds() {
		return selections != null;
	}

	private List<DistributionEntry> toDistributions(Color[] colors, String[] labels, Histogram hist, int largestBinSize) {
		float f = 1.f / largestBinSize;
		List<DistributionEntry> r = new ArrayList<>(hist.size());
		for(int i = 0; i < hist.size(); ++i) {
			float value = f * hist.get(i);
			String label = i < labels.length ? labels[i] : "NoName";
			Color color = i < colors.length ? colors[i] : Color.NEUTRAL_GREY;
			r.add(new DistributionEntry(label, color, value, hist.getIDsForBucket(i)));
		}
		return r;
	}

	@Override
	public void onChange(GLElement callback) {
		this.callback = callback;
	}

	@Override
	public <T> T getLayoutDataAs(Class<T> clazz, T default_) {
		return getLayoutDataAs(clazz, Suppliers.ofInstance(default_));
	}

	@Override
	public <T> T getLayoutDataAs(Class<T> clazz, Supplier<? extends T> default_) {
		return default_.get();
	}

	/**
	 * @param hist2
	 * @return
	 */
	private static int size(Histogram hist) {
		int s = 0;
		for (int i = 0; i < hist.size(); ++i)
			s += hist.get(i);
		return s + hist.getNanCount();
	}

	private static String[] toLabels(int size, String[] labels) {
		if (labels != null)
			return labels;
		String[] l = new String[size];
		for (int i = 0; i < size; ++i)
			l[i] = "Bin " + (i + 1);
		return l;
	}

	private static Color[] toColors(int size, Color[] colors) {
		if (colors != null)
			return colors;
		return ColorBrewer.Set2.getColors(size).toArray(new Color[0]);
	}


	@Override
	public List<DistributionEntry> getEntries() {
		return entries;
	}

	@Override
	public DistributionEntry get(int entry) {
		return entries.get(entry);
	}

	@Override
	public DistributionEntry getOf(int dataIndex) {
		if (ids == null || ids.isEmpty()) {
			// bin order
			int offset = 0;
			for (DistributionEntry entry : entries) {
				offset += entry.getIDs().size();
				if (offset >= dataIndex)
					return entry;
			}
		} else {
			Integer id = ids.get(dataIndex);
			for (DistributionEntry entry : entries) {
				if (entry.getIDs().contains(id))
					return entry;
			}
		}
		return null;
	}

	@Override
	public void select(Collection<Integer> ids, SelectionType selectionType, boolean clear) {
		if (selections == null)
			return;
		SelectionManager manager = selections.get(0);
		if (clear)
			manager.clearSelection(selectionType);
		manager.addToType(selectionType, ids);
		selections.fireSelectionDelta(manager);
	}

	@Override
	public Set<Integer> getElements(SelectionType selectionType) {
		if (selections == null)
			return Collections.emptySet();
		return selections.get(0).getElements(selectionType);
	}

	@Override
	public void onSelectionUpdate(SelectionManager manager) {
		callback.repaint();
	}

	@Override
	public int size() {
		return size;
	}
}
