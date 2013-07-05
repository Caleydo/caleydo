/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
