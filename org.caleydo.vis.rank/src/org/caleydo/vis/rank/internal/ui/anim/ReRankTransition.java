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

import org.caleydo.core.view.opengl.layout2.animation.InOutTransitions.IInTransition;
import org.caleydo.core.view.opengl.layout2.animation.InOutTransitions.IOutTransition;
import org.caleydo.core.view.opengl.layout2.animation.MoveTransitions.IMoveTransition;

/**
 * @author Samuel Gratzl
 *
 */
public class ReRankTransition implements IMoveTransition, IInTransition, IOutTransition {
	public static final ReRankTransition INSTANCE = new ReRankTransition();

	protected ReRankTransition() {

	}

	@Override
	public Vec4f move(Vec4f from, Vec4f to, float w, float h, float alpha) {
		Vec4f delta = to.minus(from);
		Vec4f r = new Vec4f();
		if (from.w() <= 0) { // show
			r.setX(to.x());
			if (from.y() <= 0) { // from top

			} else { // from bottom

			}
			r.setY(from.y() + delta.y() * alpha);
			r.setW(from.w() + delta.w() * alpha);
		} else if (to.w() <= 0) { // hide
			r.setX(to.x());
			if (to.y() <= 0) { // to top

			} else { // to bottom

			}
			r.setY(from.y() + delta.y() * alpha);
			r.setW(from.w() + delta.w() * alpha);
		} else { // move
			r.setX(from.x() + delta.x() * alpha);
			r.setY(from.y() + delta.y() * alpha);
			r.setW(from.w() + delta.w() * alpha);
		}
		r.setZ(to.z());// from.z() + delta.z() * alpha);
		return r;
	}

	@Override
	public Vec4f in(Vec4f to, float w, float h, float alpha) {
		return to.copy();
	}

	@Override
	public Vec4f out(Vec4f from, float w, float h, float alpha) {
		return new Vec4f(from.x(), from.y(), from.z(), 0);
	}
}
