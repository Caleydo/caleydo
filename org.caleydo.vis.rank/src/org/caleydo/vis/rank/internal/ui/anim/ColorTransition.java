/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.rank.internal.ui.anim;

import gleem.linalg.Vec4f;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.vis.rank.ui.RenderStyle;

/**
 * @author Samuel Gratzl
 *
 */
public class ColorTransition {
	// fly weights
	private static final ColorTransition[] ups;
	private static final ColorTransition[] downs;

	static {
		final int size = 20;
		ups = new ColorTransition[size];
		downs = new ColorTransition[size];
		for (int i = 0; i < size; ++i) {
			ups[i] = new ColorTransition(i + 1);
			downs[i] = new ColorTransition(-i - 1);
		}
	}

	/**
	 * factory class for creating a {@link ColorTransition}
	 *
	 * @param delta
	 * @return
	 */
	public static ColorTransition get(int delta) {
		if (delta > 0 && delta < ups.length)
			return ups[delta - 1];
		if (delta < 0 && (-delta) < downs.length)
			return downs[-delta - 1];
		return new ColorTransition(delta);
	}

	private final int delta;

	private ColorTransition(int delta) {
		this.delta = delta;
	}

	public void render(GLElement elem, GLGraphics g, float alpha) {
		float calpha = RenderStyle.computeHighlightAlpha(alpha, delta);
		Color base = delta < 0 ? Color.GREEN : Color.RED;
		Color c = new Color(base.r, base.g, base.b, (int) (calpha * 255));
		Vec4f bounds = elem.getBounds();
		g.decZ();
		g.color(c);
		g.fillRect(bounds.x(), bounds.y(), bounds.z(), bounds.w());
		g.incZ();
		elem.render(g);

	}
}
