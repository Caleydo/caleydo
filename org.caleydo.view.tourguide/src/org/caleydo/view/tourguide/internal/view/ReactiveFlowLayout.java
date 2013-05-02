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
