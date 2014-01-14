/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.util.function;

import static org.caleydo.core.util.function.DoubleFunctions.CLAMP01;
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
	public static IDoubleFunction constant(final double c) {
		return new ADoubleFunction() {
			@Override
			public double apply(double in) {
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
	public static final IDoubleFunction linear(final double v0, final double v1) {
		return new LinearInterpolationFunction(v1, v0);
	}

	/**
	 * @author Samuel Gratzl
	 *
	 */
	private static final class LinearInterpolationFunction extends ADoubleFunction {
		private final double v0;
		private final double v1;

		private LinearInterpolationFunction(double v1, double v0) {
			this.v1 = v1;
			this.v0 = v0;
		}

		@Override
		public double apply(double t) {
			t = CLAMP01.apply(t);
			return v0 * (1 - t) + v1 * t;
		}

		@Override
		public String toString() {
			return String.format("(%f*(1-t) + %f*(t))", v0, v1);
		}
	}

}
