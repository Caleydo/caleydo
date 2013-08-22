/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2.layout;

import java.util.List;

/**
 * supports: for in the layouting direction (horizontal or vertical)
 * <dl>
 * <dt>unbound</dt>
 * <dd>default, i.e everything NaN</dd>
 * <dt>ratio</dt>
 * <dd>by float layout data 0..1</dd>
 * <dt>fix</dt>
 * <dd>by set sized in element</dd>
 * <dt>multiple</dt>
 * <dd>activated, when the sum of ratios &gt; 1</dd>
 * </dl>
 * for the other one
 * <dl>
 * <dt>unbound</dt>
 * <dd>default, i.e everything NaN</dd>
 * <dt>fix</dt>
 * <dd>by set sized in element</dd>
 * </dl>
 *
 * @author Samuel Gratzl
 *
 */
public class GLFlowLayout implements IGLLayout2 {
	protected final boolean horizontal;
	protected final float gap;
	protected final GLPadding padding;

	public GLFlowLayout(boolean horizontal, float gap, GLPadding padding) {
		this.horizontal = horizontal;
		this.gap = gap;
		this.padding = padding;
	}

	@Override
	public boolean doLayout(List<? extends IGLLayoutElement> children, float w, float h, IGLLayoutElement parent) {
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
			if (isDefault(fix) && isDefault(fix)) {
				unbounded++;
			} else if (fix >= 0) {
				fixUsed += fix;
			} else { // (ratio > 0)
				ratioSum += ratio;
			}
		}
		float ratioMax = (ratioSum < 1) ? 1 : ratioSum;
		float unboundedSpace = (freeSpace - fixUsed - freeSpace * ratioSum / ratioMax) / unbounded;

		// set all sizes
		for (IGLLayoutElement child : children) {
			float fix = horizontal ? child.getSetWidth() : child.getSetHeight();
			float ratio = child.getLayoutDataAs(Number.class, Float.NaN).floatValue();
			if (isDefault(fix) && isDefault(ratio)) {
				setSize(w, h, child, unboundedSpace);
			} else if (fix >= 0) {
				setSize(w, h, child, fix);
			} else { // (ratio > 0)
				float value = (ratio / ratioMax) * freeSpace;
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

	protected static boolean isDefault(float v) {
		return v < 0 || Float.isNaN(v);
	}

	protected void setSize(float w, float h, IGLLayoutElement child, float value) {
		if (horizontal)
			child.setSize(value, grab(child.getSetHeight(), h));
		else
			child.setSize(grab(child.getSetWidth(), w), value);
	}

	protected static float grab(float v, float v_full) {
		return isDefault(v) ? v_full : v;
	}
}
