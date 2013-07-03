/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2.layout;

import java.util.List;

/**
 * Layout similar to {@link GLFlowLayout}, but this layout guarantees all elements to fit the parent, except for
 * children that in sum specify a fixed pixel size that exceeds the parents size. The size of ratio elements is
 * calculated as follows: <code>ratio_size = (parent_size - pixel_elements_size) * ratio</code>. If this results in a
 * negative number, the size of the ratio element is set to 0. Unbounded elements uniformly distribute the space that is
 * left.
 *
 *
 * @author Christian Partl
 *
 */
public class GLSizeRestrictiveFlowLayout extends GLFlowLayout {

	/**
	 * @param horizontal
	 * @param gap
	 * @param padding
	 */
	public GLSizeRestrictiveFlowLayout(boolean horizontal, float gap, GLPadding padding) {
		super(horizontal, gap, padding);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
		w -= padding.hor();
		h -= padding.vert();
		float freeSpace = (horizontal ? w : h) - gap * (children.size() - 1);
		int unbounded = 0;
		float fixUsed = 0;
		float ratioSum = 0;

		// count statistics
		for (IGLLayoutElement child : children) {
			float fix = horizontal ? child.getSetWidth() : child.getSetHeight();
			float ratio = child.getLayoutDataAs(Number.class, Float.NaN).floatValue();
			if (isDefault(fix) && isDefault(ratio)) {
				unbounded++;
			} else if (fix >= 0) {
				fixUsed += fix;
			} else { // (ratio > 0)
				ratioSum += ratio;
			}
		}
		float ratioMax = (ratioSum < 1) ? 1 : ratioSum;
		float ratioSpace = ((freeSpace - fixUsed) > 0) ? (freeSpace - fixUsed) * ratioSum / ratioMax : 0;
		float unboundedSpace = ((unbounded > 0) && (freeSpace - fixUsed - ratioSpace) > 0) ? (freeSpace - fixUsed - ratioSpace)
				/ unbounded
				: 0;

		// set all sizes
		for (IGLLayoutElement child : children) {
			float fix = horizontal ? child.getSetWidth() : child.getSetHeight();
			float ratio = child.getLayoutDataAs(Number.class, Float.NaN).floatValue();
			if (isDefault(fix) && isDefault(ratio)) {
				setSize(w, h, child, unboundedSpace);
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
	}

}
