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

	public static Double applyPrimitive(IDoubleFunction f, Double in) {
		if (in == null)
			in = Double.NaN;
		return Double.valueOf(f.apply(in.doubleValue()));
	}

	public static final IInvertableDoubleFunction IDENTITY = new IInvertableDoubleFunction() {
		@Override
		public double apply(double in) {
			return in;
		}

		@Override
		public String toString() {
			return "identity";
		}

		@Override
		public Double apply(Double input) {
			return applyPrimitive(this, input);
		}

		@Override
		public double unapply(double v) {
			return v;
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
	public static IInvertableDoubleFunction normalize(final double min, double max) {
		final double delta = max - min;
		return new IInvertableDoubleFunction() {
			@Override
			public double apply(double in) {
				return (in - min) / delta;
			}

			@Override
			public String toString() {
				return String.format("normalize[%f,%f]", min, min + delta);
			}

			@Override
			public Double apply(Double input) {
				return applyPrimitive(this, input);
			}

			@Override
			public double unapply(double in) {
				return delta * in + min;
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
	public static IInvertableDoubleFunction unnormalize(final double min, double max) {
		final double delta = max - min;
		return new IInvertableDoubleFunction() {
			@Override
			public double apply(double in) {
				return delta * in + min;
			}

			@Override
			public String toString() {
				return String.format("unnormalize[%f,%f]", min, min + delta);
			}

			@Override
			public Double apply(Double input) {
				return applyPrimitive(this, input);
			}

			@Override
			public double unapply(double in) {
				return (in - min) / delta;
			}
		};
	}

	/**
	 * simple mapping from a source to a target range
	 *
	 * @param fromMin
	 * @param fromMax
	 * @param toMin
	 * @param toMax
	 * @return
	 */
	public static IInvertableDoubleFunction map(final double fromMin, final double fromMax, final double toMin,
			final double toMax) {
		final double scale = (toMax - toMin) / (fromMax - fromMin);
		return new IInvertableDoubleFunction() {
			@Override
			public double apply(double in) {
				if (in <= fromMin)
					return toMin;
				if (in >= fromMax)
					return toMax;
				return toMin + ((in - fromMin) * scale);
			}

			@Override
			public String toString() {
				return String.format("map[%f,%f->%f,%f]", fromMin, fromMax, toMin, toMax);
			}

			@Override
			public Double apply(Double input) {
				return applyPrimitive(this, input);
			}

			@Override
			public double unapply(double in) {
				if (in <= toMin)
					return fromMin;
				if (in >= toMax)
					return fromMax;
				return fromMin + ((in - toMin) * (1 / scale));
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

	public static IInvertableDoubleFunction invert(final IInvertableDoubleFunction f) {
		return new IInvertableDoubleFunction() {

			@Override
			public Double apply(Double input) {
				return applyPrimitive(this, input);
			}

			@Override
			public double apply(double v) {
				return f.unapply(v);
			}

			@Override
			public double unapply(double in) {
				return f.apply(in);
			}
		};
	}
}

