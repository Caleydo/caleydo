/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.function;

import com.google.common.base.Function;

/**
 * factory for various {@link IDoubleFunction}s
 *
 * @author Samuel Gratzl
 *
 */
public final class DoubleFunctions {
	private DoubleFunctions() {

	}

	public static final ADoubleFunction IDENTITY = new ADoubleFunction() {
		@Override
		public double apply(double in) {
			return in;
		}

		@Override
		public String toString() {
			return "identity";
		}
	};

	/**
	 * clamps the given value between 0 and 1
	 */
	public static final ADoubleFunction CLAMP01 = new ADoubleFunction() {
		@Override
		public double apply(double in) {
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
	public static IDoubleFunction normalize(final double min, double max) {
		final double delta = max - min;
		return new ADoubleFunction() {
			@Override
			public double apply(double in) {
				return (in - min) / delta;
			}

			@Override
			public String toString() {
				return String.format("normalize[%f,%f]", min, min + delta);
			}
		};
	}

	/**
	 * reverse operation of {@link #normalize(double, double)}
	 *
	 * @param min
	 * @param max
	 * @return
	 */
	public static IDoubleFunction unnormalize(final double min, double max) {
		final double delta = max - min;
		return new ADoubleFunction() {
			@Override
			public double apply(double in) {
				return delta * in + min;
			}

			@Override
			public String toString() {
				return String.format("unnormalize[%f,%f]", min, min + delta);
			}
		};
	}

	public static IDoubleFunction wrap(final Function<Double, Double> f) {
		return new IDoubleFunction() {
			@Override
			public final Double apply(Double in) {
				return f.apply(in);
			}
			@Override
			public double apply(double in) {
				return f.apply(in);
			}

			@Override
			public String toString() {
				return f.toString();
			}
		};
	}
}

