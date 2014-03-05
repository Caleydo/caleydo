/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.histogram.v2;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.function.AdvancedDoubleStatistics;
import org.caleydo.core.util.function.IDoubleList;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.manage.GLLocation;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

/**
 * a {@link BoxAndWhiskersElement} based on a simple {@link IDoubleList}
 *
 * @author Samuel Gratzl
 *
 */
public class ListBoxAndWhiskersElement extends ABoxAndWhiskersElement {
	private final Color color;
	private final String label;
	private IDoubleList data;

	public ListBoxAndWhiskersElement(IDoubleList data, EDetailLevel detailLevel, EDimension direction,
			boolean showOutlier, boolean showMinMax, String label, Color color, GLPadding padding) {
		super(detailLevel, direction, showOutlier, showMinMax, padding);
		this.color = Preconditions.checkNotNull(color);
		this.label = Preconditions.checkNotNull(label);
		setData(data);
		this.data = data;
	}

	public ListBoxAndWhiskersElement(AdvancedDoubleStatistics stats, EDetailLevel detailLevel, EDimension direction,
			boolean showMinMax, String label, Color color, GLPadding padding) {
		super(detailLevel, direction, false, showMinMax, GLPadding.ONE);
		this.color = Preconditions.checkNotNull(color);
		this.label = Preconditions.checkNotNull(label);
		setData(stats);
	}

	public boolean hasData() {
		return this.data != null;
	}

	/**
	 * @return the data, see {@link #data}
	 */
	public IDoubleList getData() {
		return data;
	}

	public GLLocation getLocation(int dataIndex) {
		if (dataIndex < 0 || !hasData() || dataIndex >= this.data.size())
			return GLLocation.UNKNOWN;
		float total = getDirection().select(getSize());
		double value = this.data.getPrimitive(dataIndex);
		double pos = total * normalize.apply(value);
		return new GLLocation(pos, 1. / data.size());
	}

	public Set<Integer> forLocation(GLLocation location) {
		if (!hasData())
			return GLLocation.UNKNOWN_IDS;
		float total = getDirection().select(getSize());
		double from = normalize.unapply(location.getOffset() / total);
		double to = normalize.unapply(location.getOffset2() / total);
		List<Integer> r = new ArrayList<>();
		for (int i = 0; i < this.data.size(); ++i) {
			double v = this.data.getPrimitive(i);
			if (from <= v && v <= to)
				r.add(i);
		}
		return ImmutableSet.copyOf(r);
	}

	@Override
	public void setData(IDoubleList list, double min, double max) {
		this.data = list;
		super.setData(list, min, max);
	}

	@Override
	public void setData(AdvancedDoubleStatistics stats, double min, double max) {
		this.data = null;
		super.setData(stats, min, max);
	}
	/**
	 * @return the label, see {@link #label}
	 */
	@Override
	public String getLabel() {
		return label;
	}

	/**
	 * @return the color, see {@link #color}
	 */
	@Override
	public Color getColor() {
		return color;
	}
}
