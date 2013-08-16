/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.function;

/**
 * @author Samuel Gratzl
 *
 */
public class FloatStatistics {
	private float min = Float.NaN, max = Float.NaN, sum = 0, mean = 0, var = 0;
	private float moment2, moment3, moment4;
	private int n = 0, nans = 0;

	public FloatStatistics() {
	}

	public final float getMean() {
		return mean;
	}

	public final float getVar() {
		return n > 1 ? var / (n-1) : 0;
	}

	/** Returns the standard deviation */
	public final float getSd() {
		return (float) Math.sqrt(getVar());
	}

	/**
	 * @return the min, see {@link #min}
	 */
	public final float getMin() {
		return min;
	}

	/**
	 * @return the max, see {@link #max}
	 */
	public final float getMax() {
		return max;
	}

	/**
	 * @return the sum, see {@link #sum}
	 */
	public final float getSum() {
		return sum;
	}

	public final float getKurtosis() {
		if (n == 0)
			return 0;
		return (n * moment4) / (moment2 * moment2) - 3;
	}

	public final float getSkewness() {
		if (n == 0)
			return 0;
		return (float) (Math.sqrt(n) * moment3 / (Math.pow(moment2, 3.f / 2.f)));
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

	public FloatStatistics add(float x) {
		if (Float.isNaN(x)) {
			nans++;
			return this;
		}

		n++;
		sum += x;
		if (x < min || Float.isNaN(min))
			min = x;
		if (max < x || Float.isNaN(max))
			max = x;
		// http://www.johndcook.com/standard_deviation.html
		// See Knuth TAOCP vol 2, 3rd edition, page 232
		// http://en.wikipedia.org/wiki/Algorithms_for_calculating_variance#Higher-order_statistics

		if (n == 1) {
			mean = x;
			var = 0;
			moment2 = moment3 = moment4 = 0;
		} else {
			float mean_m1 = mean;
			mean = mean_m1 + (x - mean_m1) / n;
			var = var + (x - mean_m1) * (x - mean);

			float delta = x - mean_m1;
			float delta_n = delta / n;
			float delta_n2 = delta_n * delta_n;
			float term1 = delta * delta_n * (n - 1);
			moment4 += term1 * delta_n2 * (n * n - 3 * n + 3) + 6 * delta_n2 * moment2 - 4 * delta_n * moment3;
			moment3 += term1 * delta_n * (n - 2) - 3 * delta_n * moment2;
			moment2 += term1;
		}
		return this;
	}

	protected FloatStatistics add(IFloatIterator it) {
		while (it.hasNext())
			add(it.nextPrimitive());
		return this;
	}

	protected FloatStatistics add(float[] xs) {
		for (int i = 0; i < xs.length; ++i)
			add(xs[i]);
		return this;
	}

	public static FloatStatistics of(IFloatIterator it) {
		return new FloatStatistics().add(it);
	}

	public static FloatStatistics of(float[] arr) {
		return new FloatStatistics().add(arr);
	}

	public static FloatStatistics of(IFloatList list) {
		return new FloatStatistics().add(list.iterator());
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FloatStatistics [");
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

}
