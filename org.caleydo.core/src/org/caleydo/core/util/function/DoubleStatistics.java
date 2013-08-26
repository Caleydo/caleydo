/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.function;

/**
 * Statistics calculated over a set of doubles.
 *
 * @author Samuel Gratzl
 *
 */
public class DoubleStatistics {
	private double min = Double.NaN, max = Double.NaN, sum = 0, mean = 0, var = 0;
	private double moment2, moment3, moment4;
	private int n = 0, nans = 0;

	public DoubleStatistics() {
	}

	public final double getMean() {
		return mean;
	}

	public final double getVar() {
		return n > 1 ? var / (n - 1) : 0;
	}

	/** Returns the standard deviation */
	public final double getSd() {
		return Math.sqrt(getVar());
	}

	/**
	 * @return the min, see {@link #min}
	 */
	public final double getMin() {
		return min;
	}

	/**
	 * @return the max, see {@link #max}
	 */
	public final double getMax() {
		return max;
	}

	/**
	 * @return the sum, see {@link #sum}
	 */
	public final double getSum() {
		return sum;
	}

	public final double getKurtosis() {
		if (n == 0)
			return 0;
		return (n * moment4) / (moment2 * moment2) - 3;
	}

	public final double getSkewness() {
		if (n == 0)
			return 0;
		return Math.sqrt(n) * moment3 / (Math.pow(moment2, 3.f / 2.f));
	}

	/**
	 * @return the n, see {@link #n}
	 */
	public final int getN() {
		return n;
	}

	/**
	 * @return the nans, see {@link #nans}
	 */
	public final int getNaNs() {
		return nans;
	}

	protected DoubleStatistics add(float x) {
		return add((double) x);
	}

	protected DoubleStatistics add(double x) {
		if (Double.isNaN(x)) {
			nans++;
			return this;
		}

		n++;
		sum += x;
		if (x < min || Double.isNaN(min))
			min = x;
		if (max < x || Double.isNaN(max))
			max = x;
		// http://www.johndcook.com/standard_deviation.html
		// See Knuth TAOCP vol 2, 3rd edition, page 232
		// http://en.wikipedia.org/wiki/Algorithms_for_calculating_variance#Higher-order_statistics

		if (n == 1) {
			mean = x;
			var = 0;
			moment2 = moment3 = moment4 = 0;
		} else {
			double mean_m1 = mean;
			mean = mean_m1 + (x - mean_m1) / n;
			var = var + (x - mean_m1) * (x - mean);

			double delta = x - mean_m1;
			double delta_n = delta / n;
			double delta_n2 = delta_n * delta_n;
			double term1 = delta * delta_n * (n - 1);
			moment4 += term1 * delta_n2 * (n * n - 3 * n + 3) + 6 * delta_n2 * moment2 - 4 * delta_n * moment3;
			moment3 += term1 * delta_n * (n - 2) - 3 * delta_n * moment2;
			moment2 += term1;
		}
		return this;
	}

	protected DoubleStatistics add(IDoubleIterator it) {
		while (it.hasNext())
			add(it.nextPrimitive());
		return this;
	}

	protected DoubleStatistics add(float... xs) {
		for (int i = 0; i < xs.length; ++i)
			add(xs[i]);
		return this;
	}

	protected DoubleStatistics add(double... xs) {
		for (int i = 0; i < xs.length; ++i)
			add(xs[i]);
		return this;
	}

	public static DoubleStatistics of(IDoubleIterator it) {
		return new DoubleStatistics().add(it);
	}

	public static DoubleStatistics of(double... arr) {
		return new DoubleStatistics().add(arr);
	}

	public static DoubleStatistics of(float... arr) {
		return new DoubleStatistics().add(arr);
	}

	public static DoubleStatistics of(IDoubleList list) {
		return new DoubleStatistics().add(list.iterator());
	}

	public static Builder builder() {
		return new Builder();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("doubleStatistics [");
		builder.append("min=").append(min);
		builder.append(", max=").append(max);
		builder.append(", mean=").append(getMean());
		builder.append(", var=").append(getVar());
		builder.append(", sd=").append(getSd());
		builder.append(", n=").append(n);
		builder.append(", nans=").append(nans);
		builder.append(", sum=").append(sum);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * builder pattern for {@link DoubleStatistics}
	 *
	 * @author Samuel Gratzl
	 *
	 */
	public static final class Builder {
		private DoubleStatistics stats;

		private Builder() {
			this.stats = new DoubleStatistics();
		}

		public Builder add(double v) {
			stats.add(v);
			return this;
		}

		public Builder add(double... v) {
			stats.add(v);
			return this;
		}

		public Builder add(float... v) {
			stats.add(v);
			return this;
		}

		public Builder add(IDoubleIterator it) {
			stats.add(it);
			return this;
		}

		public DoubleStatistics build() {
			return stats;
		}

	}

}
