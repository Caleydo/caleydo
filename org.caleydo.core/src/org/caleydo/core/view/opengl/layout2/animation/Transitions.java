/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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

