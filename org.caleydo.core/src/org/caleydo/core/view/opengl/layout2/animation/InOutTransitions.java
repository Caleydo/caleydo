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
