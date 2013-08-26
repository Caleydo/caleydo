/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.function;

import java.util.Arrays;

/**
 * advanced version of {@link DoubleStatistics} including median, and quartiles but requires the full data so no line
 * version
 *
 * @author Samuel Gratzl
 *
 */
public class AdvancedDoubleStatistics extends DoubleStatistics {
	private final double median, quartile25, quartile75, medianAbsoluteDeviation;

	// http://stat.ethz.ch/R-manual/R-patched/library/stats/html/mad.html
	public static final double MEDIAN_ABSOLUTE_DEVIATION_CONSTANT = 1.4826f;

	public AdvancedDoubleStatistics(double median, double quartile25, double quartile75, double medianAbsoluteDeviation) {
		this.median = median;
		this.quartile25 = quartile25;
		this.quartile75 = quartile75;
		this.medianAbsoluteDeviation = medianAbsoluteDeviation;
	}

	/**
	 * @return the median, see {@link #median}
	 */
	public double getMedian() {
		return median;
	}

	/**
	 * @return the median absolute deviation as implemented in R:
	 *         http://stat.ethz.ch/R-manual/R-patched/library/stats/html/mad.html, see {@link #medianAbsoluteDeviation}
	 */
	public double getMedianAbsoluteDeviation() {
		return medianAbsoluteDeviation;
	}

	/**
	 * @return the quartile25, see {@link #quartile25}
	 */
	public double getQuartile25() {
		return quartile25;
	}

	/**
	 * @return the quartile75, see {@link #quartile75}
	 */
	public double getQuartile75() {
		return quartile75;
	}

	public double getIQR() {
		return quartile75 - quartile25;
	}

	public static AdvancedDoubleStatistics of(IDoubleList list) {
		return ofImpl(list.toPrimitiveArray());
	}

	public static AdvancedDoubleStatistics of(double[] arr) {
		return ofImpl(Arrays.copyOf(arr, arr.length));
	}

	private static AdvancedDoubleStatistics ofImpl(double[] data) {
		Arrays.sort(data);

		double median = median(data);

		double quartile25 = percentile(data, 0.25f);
		double quartile75 = percentile(data, 0.75f);

		double[] medianDeltas = new double[data.length];
		for (int i = 0; i < data.length; i++) {
			medianDeltas[i] = Math.abs(data[i] - median);
		}
		Arrays.sort(medianDeltas);

		double medianAbsoluteDeviation = MEDIAN_ABSOLUTE_DEVIATION_CONSTANT * median(medianDeltas);

		AdvancedDoubleStatistics result = new AdvancedDoubleStatistics(median, quartile25, quartile75,
				medianAbsoluteDeviation);
		result.add(data);
		return result;
	}

	/*
	 * assumes sorted data
	 */
	private static double median(double[] data) {
		final int n = data.length;

		int middle = n / 2;

		double median;
		if (n % 2 == 1) {
			median = data[middle];
		} else {
			median = (data[middle - 1] + data[middle]) * 0.5f;
		}

		return median;
	}

	/*
	 * assumes sorted data
	 */
	private static double percentile(double[] data, double percentile) {
		final int n = data.length;

		double k = (n - 1) * percentile;
		int f = (int) Math.floor(k);
		int c = (int) Math.ceil(k);
		if (f == c) {
			return data[(int) k];
		} else {
			double d0 = data[f] * (c - k);
			double d1 = data[c] * (k - f);
			return d0 + d1;
		}
	}

}
