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
public class GLFlowLayout implements IGLLayout {
	private final boolean horizontal;
	private final float gap;

	public GLFlowLayout(boolean horizontal, float gap) {
		this.horizontal = horizontal;
		this.gap = gap;
	}

	@Override
	public boolean doLayout(List<IGLLayoutElement> children, float w, float h) {
		float freeSpace = (horizontal ? w : h) - gap * (children.size() - 1);
		int unbounded = 0;
		float fixUsed = 0;
		float ratioSum = 0;

		// count statistics
		for (IGLLayoutElement child : children) {
			float fix = horizontal ? child.getSetWidth() : child.getSetHeight();
			float ratio = child.getLayoutDataAs(Number.class, Float.NaN).floatValue();
			if (Float.isNaN(fix) && Float.isNaN(ratio)) {
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
			if (Float.isNaN(fix) && Float.isNaN(ratio)) {
				setSize(w, h, child, unboundedSpace);
			} else if (fix >= 0) {
				setSize(w, h, child, fix);
			} else { // (ratio > 0)
				float value = (ratio / ratioMax) * freeSpace;
				setSize(w, h, child, value);
			}
		}

		// set all locations
		float x_acc = 0;
		float y_acc = 0;
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

	private void setSize(float w, float h, IGLLayoutElement child, float value) {
		if (horizontal)
			child.setSize(value, grab(child.getSetHeight(), h));
		else
			child.setSize(grab(child.getSetWidth(), w), value);
	}

	static float grab(float v, float v_full) {
		return (Float.isNaN(v) || v < 0) ? v_full : v;
	}
}