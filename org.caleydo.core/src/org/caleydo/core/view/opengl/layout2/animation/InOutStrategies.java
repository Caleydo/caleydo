/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2.animation;


/**
 * see {@link InOutInitializers} for a specific component (x,y,w,h)
 *
 * @author Samuel Gratzl
 *
 */
public class InOutStrategies {
	public interface IInOutStrategy {
		float compute(float other, float max);
	}

	public static final IInOutStrategy ZERO = new IInOutStrategy() {
		@Override
		public float compute(float other, float max) {
			return 0;
		}
	};
	public static final IInOutStrategy MAX = new IInOutStrategy() {
		@Override
		public float compute(float other, float max) {
			return max;
		}
	};
	public static final IInOutStrategy OTHER = new IInOutStrategy() {
		@Override
		public float compute(float other, float max) {
			return other;
		}
	};
}

