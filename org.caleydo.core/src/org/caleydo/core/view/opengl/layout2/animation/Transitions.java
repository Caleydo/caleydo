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


/**
 * simple interpolation strategies
 *
 * @author Samuel Gratzl
 *
 */
public class Transitions {

	public interface ITransition {
		/**
		 * strategy for mixing from and to given a alpha value between 0..1
		 *
		 * @param from
		 * @param to
		 * @param alpha
		 * @return
		 */
		float interpolate(float from, float to, float alpha);
	}

	/**
	 * linear interpolation
	 */
	public static final ITransition LINEAR = new ITransition() {
		@Override
		public float interpolate(float from, float to, float alpha) {
			float delta = to - from;
			if (delta == 0)
				return from;
			return from + delta * alpha;
		}
	};
	/**
	 * returns the destination value
	 */
	public static final ITransition NO = new ITransition() {
		@Override
		public float interpolate(float from, float to, float alpha) {
			return to;
		}
	};
	/**
	 * returns always the first value till the end then it will jump to the final one
	 */
	public static final ITransition JUMP = new ITransition() {
		@Override
		public float interpolate(float from, float to, float alpha) {
			if (alpha >= 1)
				return to;
			return from;
		}
	};

}

