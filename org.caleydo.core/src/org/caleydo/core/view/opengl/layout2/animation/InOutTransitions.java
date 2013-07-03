/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2.animation;

import static org.caleydo.core.view.opengl.layout2.animation.InOutInitializers.LEFT;
import static org.caleydo.core.view.opengl.layout2.animation.InOutInitializers.RIGHT;
import static org.caleydo.core.view.opengl.layout2.animation.MoveTransitions.MOVE_LINEAR;
import gleem.linalg.Vec4f;

import org.caleydo.core.view.opengl.layout2.animation.InOutInitializers.IInOutInitializer;
import org.caleydo.core.view.opengl.layout2.animation.MoveTransitions.IMoveTransition;

/**
 * differnt ways to handle in / out animations
 * 
 * @author Samuel Gratzl
 * 
 */
public class InOutTransitions {
	public interface IOutTransition {
		Vec4f out(Vec4f from, float w, float h, float alpha);
	}

	public interface IInTransition {
		Vec4f in(Vec4f to, float w, float h, float alpha);
	}

	public static final IInTransition SLIDE_HOR_IN = new InOutTransitionBase(LEFT, MOVE_LINEAR);
	public static final IOutTransition SLIDE_HOR_OUT = new InOutTransitionBase(RIGHT, MOVE_LINEAR);

	private InOutTransitions() {

	}

	public static class InOutTransitionBase implements IOutTransition, IInTransition {
		private final IInOutInitializer init;
		private final IMoveTransition move;

		public InOutTransitionBase(IInOutInitializer init, IMoveTransition move) {
			this.init = init;
			this.move = move;
		}

		@Override
		public Vec4f out(Vec4f from, float w, float h, float alpha) {
			Vec4f to = init.get(from, w, h);
			return move.move(from, to, w, h, alpha);
		}

		@Override
		public Vec4f in(Vec4f to, float w, float h, float alpha) {
			Vec4f from = init.get(to, w, h);
			return move.move(from, to, w, h, alpha);
		}
	}
}
