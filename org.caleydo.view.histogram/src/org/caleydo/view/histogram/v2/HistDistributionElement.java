/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.histogram.v2;

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
import org.caleydo.core.view.opengl.layout2.IGLElementContext;

import com.google.common.collect.ImmutableList;

/**
 * a distribution renderer based on a {@link Histogram}
 *
 * @author Samuel Gratzl
 *
 */
public class HistDistributionElement extends ADistributionElement implements
		MultiSelectionManagerMixin.ISelectionMixinCallback {
	@DeepScan
	protected final MultiSelectionManagerMixin selections;

	private final Histogram hist;
	private final int size;
	private final List<Color> colors;
	private final List<String> labels;

	/**
	 *
	 */
	public HistDistributionElement(Histogram hist, IDType idType, EDistributionMode mode, String[] labels,
			Color[] colors) {
		super(mode);
		this.colors = ImmutableList.copyOf(toColors(hist.size(), colors));
		this.labels = ImmutableList.copyOf(toLabels(hist.size(), labels));
		this.size = size(hist);
		this.hist = hist;
		if (idType == null) {
			this.selections = null;
		} else {
			this.selections = new MultiSelectionManagerMixin(this);
			this.selections.add(new SelectionManager(idType));
		}
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
	protected void init(IGLElementContext context) {
		super.init(context);

		if (bucketPickingIds != null)
			bucketPickingIds.ensure(0, hist.size());
	}

	@Override
	protected Histogram getHist() {
		return hist;
	}

	@Override
	protected String getBinName(int bin) {
		if (bin >= labels.size())
			return "<NoName>";
		return labels.get(bin);
	}

	@Override
	protected Color getBinColor(int bin) {
		if (bin >= colors.size())
			return Color.NEUTRAL_GREY;
		return colors.get(bin);
	}

	@Override
	protected void select(Collection<Integer> ids, SelectionType selectionType, boolean clear) {
		if (selections == null)
			return;
		SelectionManager manager = selections.get(0);
		if (clear)
			manager.clearSelection(selectionType);
		manager.addToType(selectionType, ids);
		selections.fireSelectionDelta(manager);
	}

	@Override
	protected Set<Integer> getElements(SelectionType selectionType) {
		if (selections == null)
			return Collections.emptySet();
		return selections.get(0).getElements(selectionType);
	}

	@Override
	public void onSelectionUpdate(SelectionManager manager) {
		repaint();
	}

	@Override
	protected int size() {
		return size;
	}
}
