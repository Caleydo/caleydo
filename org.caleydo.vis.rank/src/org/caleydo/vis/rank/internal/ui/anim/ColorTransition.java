/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.vis.rank.internal.ui.anim;

import gleem.linalg.Vec4f;

import java.awt.Color;

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
		Color c = new Color(base.getRed(), base.getGreen(), base.getBlue(), (int) (calpha * 255));
		Vec4f bounds = elem.getBounds();
		g.decZ();
		g.color(c);
		g.fillRect(bounds.x(), bounds.y(), bounds.z(), bounds.w());
		g.incZ();
		elem.render(g);

	}
}
