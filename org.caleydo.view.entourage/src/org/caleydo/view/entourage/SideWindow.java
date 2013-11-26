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
package org.caleydo.view.entourage;

import gleem.linalg.Vec4f;

import org.caleydo.core.view.opengl.layout2.animation.MoveTransitions;
import org.caleydo.core.view.opengl.layout2.animation.MoveTransitions.IMoveTransition;

import com.google.common.base.Supplier;

/**
 * a special GLWindow that uses a special kind of animations that moves out of the view instead of changing the size
 *
 * @author Samuel Gratzl
 *
 */
public class SideWindow extends GLWindow {
	public static final IMoveTransition SLIDE_LEFT_OUT = new IMoveTransition() {
		@Override
		public Vec4f move(Vec4f from, Vec4f to, float w, float h, float alpha) {
			boolean isSlideOut = to.z() <= 1;
			boolean isSlideIn = from.z() <= 1;
			if (isSlideOut) {
				// keep the size and just move to the left
				Vec4f r = new Vec4f();
				r.setX(from.x() - (from.z() - to.z()) * alpha); // keep the size and move it out
				r.setY(to.y()); // final y
				r.setZ(from.z()); // original width
				r.setW(to.w()); // final height
				return r;
			} else if (isSlideIn) {
				Vec4f r = new Vec4f();
				r.setX(from.x() + (from.z() - to.z()) * (1 - alpha)); // keep the size and move it out
				r.setY(to.y()); // target y
				r.setZ(to.z()); // target width with is the real with
				r.setW(to.w()); // target height
				return r;
			} else
				return MoveTransitions.MOVE_AND_GROW_LINEAR.move(from, to, w, h, alpha);
		}
	};
	public static final IMoveTransition SLIDE_BOTTOM_OUT = new IMoveTransition() {
		@Override
		public Vec4f move(Vec4f from, Vec4f to, float w, float h, float alpha) {
			boolean isSlideOut = to.w() <= 1;
			boolean isSlideIn = from.w() <= 1;
			if (isSlideOut) {
				// keep the size and just move to the left
				Vec4f r = new Vec4f();
				r.setX(from.x()); // keep the size and move it out
				r.setY(from.y() + (from.w() - to.w()) * alpha); // final y
				r.setZ(to.z()); // final width
				r.setW(from.w()); // original height
				return r;
			} else if (isSlideIn) {
				Vec4f r = new Vec4f();
				r.setX(from.x()); // keep the size and move it out
				r.setY(from.y() + (from.w() - to.w()) * alpha); // final y
				r.setZ(to.z()); // final width
				r.setW(from.w()); // original height
				return r;
			} else
				return MoveTransitions.MOVE_AND_GROW_LINEAR.move(from, to, w, h, alpha);
		}
	};

	private final IMoveTransition animation;

	public SideWindow(String title, GLEntourage view, IMoveTransition animation) {
		super(title, view);
		this.animation = animation;
	}

	@Override
	public <T> T getLayoutDataAs(Class<T> clazz, Supplier<? extends T> default_) {
		if (clazz.isInstance(animation)) {
			return clazz.cast(animation);
		}
		return super.getLayoutDataAs(clazz, default_);
	}
}
