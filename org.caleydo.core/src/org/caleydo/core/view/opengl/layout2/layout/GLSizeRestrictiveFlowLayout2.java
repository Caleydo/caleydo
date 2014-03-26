/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2.layout;

import java.util.List;

/**
 * Same as {@link GLSizeRestrictiveFlowLayout}, but uses the min size to calculate the size of unbounded elements.
 *
 * @author Christian
 *
 */
public class GLSizeRestrictiveFlowLayout2 extends GLSizeRestrictiveFlowLayout {

	/**
	 * @param horizontal
	 * @param gap
	 * @param padding
	 */
	public GLSizeRestrictiveFlowLayout2(boolean horizontal, float gap, GLPadding padding) {
		super(horizontal, gap, padding);
	}

	@Override
	public boolean doLayout(List<? extends IGLLayoutElement> children, float w, float h, IGLLayoutElement parent,
			int deltaTimeMs) {
		w -= padding.hor();
		h -= padding.vert();
		float freeSpace = (horizontal ? w : h) - gap * (children.size() - 1);
		int unbounded = 0;
		float fixUsed = 0;
		float ratioSum = 0;
		float minSizeUnboundSum = 0;

		// count statistics
		for (IGLLayoutElement child : children) {
			float fix = horizontal ? child.getSetWidth() : child.getSetHeight();
			float ratio = child.getLayoutDataAs(Number.class, Float.NaN).floatValue();
			if (isDefault(fix) && isDefault(ratio)) {
				unbounded++;
				minSizeUnboundSum += horizontal ? child.asElement().getMinSize().x() : child.asElement().getMinSize()
						.y();
			} else if (fix >= 0) {
				fixUsed += fix;
			} else { // (ratio > 0)
				ratioSum += ratio;
			}
		}
		float ratioMax = (ratioSum < 1) ? 1 : ratioSum;
		float ratioSpace = ((freeSpace - fixUsed) > 0) ? (freeSpace - fixUsed) * ratioSum / ratioMax : 0;
		float totalUnboundedSpace = ((unbounded > 0) && (freeSpace - fixUsed - ratioSpace) > 0) ? freeSpace - fixUsed
				- ratioSpace : 0;

		// set all sizes
		for (IGLLayoutElement child : children) {
			float fix = horizontal ? child.getSetWidth() : child.getSetHeight();
			float ratio = child.getLayoutDataAs(Number.class, Float.NaN).floatValue();
			if (isDefault(fix) && isDefault(ratio)) {
				float minSize = horizontal ? child.asElement().getMinSize().x() : child.asElement().getMinSize().y();
				float size = minSizeUnboundSum != 0 ? totalUnboundedSpace * minSize / minSizeUnboundSum
						: totalUnboundedSpace / unbounded;
				setSize(w, h, child, size);
			} else if (fix >= 0) {
				setSize(w, h, child, fix);
			} else { // (ratio > 0)
				float value = (ratio / ratioMax) * (freeSpace - fixUsed);
				setSize(w, h, child, value);
			}
		}

		// set all locations
		float x_acc = padding.left;
		float y_acc = padding.top;
		for (IGLLayoutElement child : children) {
			child.setLocation(x_acc, y_acc);
			if (horizontal) {
				x_acc += child.getWidth() + gap;
			} else {
				y_acc += child.getHeight() + gap;
			}
		}
		return false;
	}

}
