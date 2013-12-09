/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.histogram.v2;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.function.AdvancedDoubleStatistics;
import org.caleydo.core.util.function.IDoubleList;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;

import com.google.common.base.Preconditions;

/**
 * a {@link BoxAndWhiskersElement} based on a simple {@link IDoubleList}
 *
 * @author Samuel Gratzl
 *
 */
public class ListBoxAndWhiskersElement extends ABoxAndWhiskersElement {
	private final Color color;
	private final String label;

	public ListBoxAndWhiskersElement(IDoubleList data, EDetailLevel detailLevel, EDimension direction,
			boolean showOutlier, boolean showMinMax, String label, Color color) {
		super(detailLevel, direction, showOutlier, showMinMax);
		this.color = Preconditions.checkNotNull(color);
		this.label = Preconditions.checkNotNull(label);
		setData(data);
	}

	public ListBoxAndWhiskersElement(AdvancedDoubleStatistics stats, EDetailLevel detailLevel, EDimension direction,
			boolean showMinMax, String label, Color color) {
		super(detailLevel, direction, false, showMinMax);
		this.color = Preconditions.checkNotNull(color);
		this.label = Preconditions.checkNotNull(label);
		setData(stats);
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
