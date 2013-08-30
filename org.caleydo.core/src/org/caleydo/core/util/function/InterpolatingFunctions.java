/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.util.function;

import static org.caleydo.core.util.function.FloatFunctions.CLAMP01;
/**
 * @author Samuel Gratzl
 *
 */
public final class InterpolatingFunctions {

	private InterpolatingFunctions() {

	}

	/**
	 * return a function that will always return the given constant value c
	 *
	 * @param c
	 *            the value to return
	 */
	public static IFloatFunction constant(final float c) {
		return new AFloatFunction() {
			@Override
			public float apply(float in) {
				return c;
			}

			@Override
			public String toString() {
				return String.valueOf(c);
			}
		};
	}

	/**
	 * simple linear interpolation between the two given values
	 *
	 * @param v0
	 * @param v1
	 * @return
	 */
	public static final IFloatFunction linear(final float v0, final float v1) {
		return new LinearInterpolationFunction(v1, v0);
	}

	/**
	 * @author Samuel Gratzl
	 *
	 */
	private static final class LinearInterpolationFunction extends AFloatFunction {
		private final float v0;
		private final float v1;

		private LinearInterpolationFunction(float v1, float v0) {
			this.v1 = v1;
			this.v0 = v0;
		}

		@Override
		public float apply(float t) {
			t = CLAMP01.apply(t);
			return v0 * (1 - t) + v1 * t;
		}

		/**
		 * @return the v0, see {@link #v0}
		 */
		public float getV0() {
			return v0;
		}

		/**
		 * @return the v1, see {@link #v1}
		 */
		public float getV1() {
			return v1;
		}

		@Override
		public String toString() {
			return String.format("(%f*(1-t) + %f*(t))", v0, v1);
		}
	}

}
