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
package org.caleydo.core.view.opengl.layout2.animation;

import static org.caleydo.core.view.opengl.layout2.animation.Transitions.LINEAR;
import static org.caleydo.core.view.opengl.layout2.animation.Transitions.NO;
import gleem.linalg.Vec4f;

import org.caleydo.core.view.opengl.layout2.animation.Transitions.ITransition;

/**
 * different strategies to move an element
 *
 * @author Samuel Gratzl
 *
 */
public class MoveTransitions {
	public interface IMoveTransition {
		/**
		 * interpolation between bounds (x,y,w,h) of an element
		 * 
		 * @param from
		 * @param to
		 * @param w
		 *            max width value of the parent
		 * @param h
		 *            max height value of the parent
		 * @param alpha
		 * @return
		 */
		Vec4f move(Vec4f from, Vec4f to, float w, float h, float alpha);
	}

	public static final IMoveTransition MOVE_LINEAR = new MoveTransitionBase(LINEAR, LINEAR, NO, NO);
	public static final IMoveTransition MOVE_AND_GROW_LINEAR = new MoveTransitionBase(LINEAR, LINEAR, LINEAR, LINEAR);

	public static class MoveTransitionBase implements IMoveTransition {
		protected final ITransition x;
		protected final ITransition y;
		protected final ITransition w;
		protected final ITransition h;

		public MoveTransitionBase(ITransition x, ITransition y, ITransition w, ITransition h) {
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
		}

		@Override
		public Vec4f move(Vec4f from, Vec4f to, float w, float h, float alpha) {
			Vec4f r = new Vec4f();
			r.setX(this.x.interpolate(from.x(), to.x(), alpha));
			r.setY(this.y.interpolate(from.y(), to.y(), alpha));
			r.setZ(this.w.interpolate(from.z(), to.z(), alpha));
			r.setW(this.h.interpolate(from.w(), to.w(), alpha));
			return r;
		}
	}
}

