/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.function;

import com.google.common.base.Function;

/**
 * factory for various {@link IFloatFunction}s
 *
 * @author Samuel Gratzl
 *
 */
public final class FloatFunctions {
	private FloatFunctions() {

	}

	/**
	 * identity mapping
	 */
	public static final AFloatFunction IDENTITY = new AFloatFunction() {
		@Override
		public float apply(float in) {
			return in;
		}
	};

	/**
	 * clamps the given value between 0 and 1
	 */
	public static final AFloatFunction CLAMP01 = new AFloatFunction() {
		@Override
		public float apply(float in) {
			return (in < 0 ? 0 : (in > 1 ? 1 : in));
		}
	};


	/**
	 * returns a function that normalizes the input
	 *
	 * <pre>
	 * @code (x -min) / (max-min)
	 * </pre>
	 *
	 * @param min
	 * @param max
	 * @return
	 */
	public static IFloatFunction normalize(final float min, float max) {
		final float delta = max - min;
		return new AFloatFunction() {
			@Override
			public float apply(float in) {
				return (in - min) / delta;
			}
		};
	}

	/**
	 * reverse operation of {@link #normalize(float, float)}
	 *
	 * @param min
	 * @param max
	 * @return
	 */
	public static IFloatFunction unnormalize(final float min, final float max) {
		final float delta = max - min;
		return new AFloatFunction() {
			@Override
			public float apply(float in) {
				return delta * in + min;
			}
		};
	}

	public static IFloatFunction wrap(final Function<Float,Float> f) {
		return new IFloatFunction() {
			@Override
			public final Float apply(Float in) {
				return f.apply(in);
			}
			@Override
			public float apply(float in) {
				return f.apply(in);
			}
		};
	}
}

