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

import org.caleydo.core.view.opengl.layout2.animation.InOutTransitions.IInTransition;
import org.caleydo.core.view.opengl.layout2.animation.InOutTransitions.IOutTransition;
import org.caleydo.core.view.opengl.layout2.animation.MoveTransitions.IMoveTransition;

/**
 * @author Samuel Gratzl
 *
 */
public class ReRankTransition implements IMoveTransition, IInTransition, IOutTransition {
	public static final ReRankTransition INSTANCE = new ReRankTransition();

	private ReRankTransition() {
	}

	@Override
	public Vec4f move(Vec4f from, Vec4f to, float w, float h, float alpha) {
		if (from.w() <= 0)
			return in(to, w, h, alpha);
		if (to.w() <= 0)
			return out(from, w, h, alpha);
		Vec4f delta = to.minus(from);
		Vec4f r = new Vec4f();
		r.setX(from.x() + delta.x() * alpha);

		r.setY(from.y() + delta.y() * alpha);
		r.setZ(to.z());// from.z() + delta.z() * alpha);
		r.setW(from.w() + delta.w() * alpha);
		if (delta.y() < -20) {
			r.setX(r.x() - (float) Math.sin(Math.PI * alpha) * (delta.y()));
		}
		return r;
		// return MoveTransitions.MOVE_AND_GROW_LINEAR.move(from, to, w, h, alpha);
	}

	@Override
	public Vec4f in(Vec4f to, float w, float h, float alpha) {
		return to;
	}

	@Override
	public Vec4f out(Vec4f from, float w, float h, float alpha) {
		return new Vec4f(from.x(), from.y(), from.z(), 0);
	}

}
