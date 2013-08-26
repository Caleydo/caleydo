/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.view;

import static org.caleydo.core.view.opengl.layout2.layout.GLLayouts.isDefault;
import gleem.linalg.Vec2f;

import java.util.List;

import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;

/**
 * @author Samuel Gratzl
 *
 */
public class ReactiveFlowLayout implements IGLLayout {

	private final float gap;

	public ReactiveFlowLayout(float gap) {
		this.gap = gap;
	}

	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
		final boolean isHorizontal = isHorizontal(w, h);

		float max = isHorizontal ? w : h;
		max -= (children.size() - 1) * gap;

		int unbound = 0;
		for (IGLLayoutElement child : children) {
			Vec2f t = new Vec2f(child.getSetWidth(), child.getSetHeight());
			t = child.getLayoutDataAs(Vec2f.class, t);
			float x = isHorizontal ? t.x() : t.y();

			if (isDefault(x))
				unbound++;
			else
				max -= x;
		}

		float x = 0;
		for (IGLLayoutElement child : children) {
			Vec2f t = new Vec2f(child.getSetWidth(), child.getSetHeight());
			t = child.getLayoutDataAs(Vec2f.class, t);
			float cx = isHorizontal ? t.x() : t.y();
			if (isDefault(cx))
				cx = max / unbound;
			if (isHorizontal)
				child.setBounds(x, 0, cx, h);
			else
				child.setBounds(0, x, w, cx);
			x += cx + gap;
		}
	}

	private boolean isHorizontal(float w, float h) {
		return h < w;
	}

}
