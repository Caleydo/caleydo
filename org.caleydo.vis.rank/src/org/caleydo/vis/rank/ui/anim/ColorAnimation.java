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
package org.caleydo.vis.rank.ui.anim;

import gleem.linalg.Vec4f;

import java.awt.Color;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.animation.StyleAnimations.IStyleAnimation;

/**
 * @author Samuel Gratzl
 *
 */
public class ColorAnimation implements IStyleAnimation {
	// fly weights
	private static final ColorAnimation[] ups;
	private static final ColorAnimation[] downs;

	static {
		ups = new ColorAnimation[10];
		downs = new ColorAnimation[10];
		for(int i = 0; i<10; ++i) {
			ups[i] = new ColorAnimation(i + 1);
			downs[i] = new ColorAnimation(-i - 1);
		}
	}

	public static ColorAnimation get(int delta) {
		if (delta > 0 && delta <= 10)
			return ups[delta - 1];
		if (delta < 0 && delta >= -10)
			return downs[-delta + 1];
		return new ColorAnimation(delta);
	}

	private final int delta;

	public ColorAnimation(int delta) {
		this.delta = delta;
	}

	@Override
	public void render(GLElement elem, GLGraphics g, float alpha) {
		elem.render(g);
		Color c = delta < 0 ? Color.GREEN : Color.RED; // TODO alpha
		g.decZ();
		Vec4f b = elem.getBounds();
		g.color(c).fillRect(b.x(), b.y(), b.z(), b.w());
		g.incZ();
	}

}
