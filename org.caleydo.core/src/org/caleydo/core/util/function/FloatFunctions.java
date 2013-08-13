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

		@Override
		public String toString() {
			return "IDENTITY";
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

		@Override
		public String toString() {
			return "clamp01";
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

			@Override
			public String toString() {
				return String.format("normalize[%f,%f]", min, min + delta);
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
	public static IFloatFunction unnormalize(final float min, float max) {
		final float delta = max - min;
		return new AFloatFunction() {
			@Override
			public float apply(float in) {
				return delta * in + min;
			}

			@Override
			public String toString() {
				return String.format("unnormalize[%f,%f]", min, min + delta);
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

			@Override
			public String toString() {
				return f.toString();
			}
		};
	}

	/**
	 * composes the given function. For {@code f: A->B} and {@code g: B->C}, composition is defined as the function h
	 * such that {@code h(a) == g(f(a))} for each {@code a}.
	 * 
	 * @param g
	 * @param f
	 * @return g(f(in))
	 * @see <a href="//en.wikipedia.org/wiki/Function_composition">function composition</a>
	 */
	public static IFloatFunction compose(final IFloatFunction g, final IFloatFunction f) {
		return new IFloatFunction() {

			@Override
			public Float apply(Float input) {
				return g.apply(f.apply(input));
			}

			@Override
			public float apply(float in) {
				return g.apply(f.apply(in));
			}

			@Override
			public String toString() {
				return String.format("%s(%s)", g, f);
			}
		};
	}
}

