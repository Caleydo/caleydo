/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.histogram.v2;

import gleem.linalg.Vec2f;

import java.util.List;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.util.function.IDoubleList;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.basic.ScrollingDecorator.IHasMinSize;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;

import com.google.common.collect.Iterables;

/**
 * shows multiple box and wiskers plots by stacking them vertically or horizontally depending on the split direction
 *
 * @author Samuel Gratzl
 */
public class BoxAndWhiskersMultiElement extends GLElementContainer implements IHasMinSize {
	/**
	 * in which dimension to split
	 */
	private final EDimension split;

	/**
	 * @param split2
	 */
	private BoxAndWhiskersMultiElement(EDimension split) {
		this.split = split;
		setLayout(split.isVertical() ? GLLayouts.flowVertical(0) : GLLayouts.flowHorizontal(0));
	}

	public BoxAndWhiskersMultiElement(TablePerspective tablePerspective, EDetailLevel detailLevel, EDimension split,
			boolean showOutliers, boolean showMinMax) {
		this(split);
		for (TablePerspective t : (split.isVertical() ? tablePerspective.getRecordSubTablePerspectives()
				: tablePerspective.getDimensionSubTablePerspectives())) {
			this.add(new BoxAndWhiskersElement(t, detailLevel, split, showOutliers, showMinMax));
		}
	}

	public BoxAndWhiskersMultiElement(List<IDoubleList> lists, EDetailLevel detailLevel, EDimension split,
			boolean showOutliers, boolean showMinMax) {
		this(split);
		for (IDoubleList list : lists) {
			this.add(new ListBoxAndWhiskersElement(list, detailLevel, split, showOutliers, showMinMax, null, null));
		}
	}

	@Override
	public final Vec2f getMinSize() {
		float w = 0;
		float h = 0;
		for (IHasMinSize m : Iterables.filter(this, IHasMinSize.class)) {
			Vec2f minSize = m.getMinSize();
			if (split.isHorizontal()) {
				w += minSize.x();
				h = Math.max(minSize.y(), h);
			} else {
				w = Math.max(minSize.x(), w);
				h += minSize.y();
			}
		}
		return new Vec2f(w, h);
	}

	/**
	 * @param b
	 */
	public void setShowScale(boolean scale) {
		if (this.isEmpty())
			return;
		((ABoxAndWhiskersElement) this.get(this.size() - 1)).setShowScale(scale);
	}
}
