/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
	public static final IMoveTransition GROW_LINEAR = new MoveTransitionBase(NO, NO, LINEAR, LINEAR);

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

