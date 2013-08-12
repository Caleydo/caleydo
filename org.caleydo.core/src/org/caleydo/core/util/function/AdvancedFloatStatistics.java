/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.function;

import java.util.Arrays;

/**
 * advanced version of {@link FloatStatistics} including median, and quartiles but requires the full data so no line
 * version
 * 
 * @author Samuel Gratzl
 * 
 */
public class AdvancedFloatStatistics extends FloatStatistics {
	private final float median, quartile25, quartile75, medianAbsoluteDeviation;
	
	// http://stat.ethz.ch/R-manual/R-patched/library/stats/html/mad.html
	public static final float MEDIAN_ABSOLUTE_DEVIATION_CONSTANT = 1.4826f; 

	public AdvancedFloatStatistics(float median, float quartile25, float quartile75, float medianAbsoluteDeviation) {
		this.median = median;
		this.quartile25 = quartile25;
		this.quartile75 = quartile75;
		this.medianAbsoluteDeviation = medianAbsoluteDeviation;		
	}

	/**
	 * @return the median, see {@link #median}
	 */
	public float getMedian() {
		return median;
	}

	/**
	 * @return the median absolute deviation as implemented in R: http://stat.ethz.ch/R-manual/R-patched/library/stats/html/mad.html, see {@link #medianAbsoluteDeviation}
	 */
	public float getMedianAbsoluteDeviation() {
		return medianAbsoluteDeviation;
	}

	
	/**
	 * @return the quartile25, see {@link #quartile25}
	 */
	public float getQuartile25() {
		return quartile25;
	}

	/**
	 * @return the quartile75, see {@link #quartile75}
	 */
	public float getQuartile75() {
		return quartile75;
	}

	public float getIQR() {
		return quartile75 - quartile25;
	}

	public static AdvancedFloatStatistics of(IFloatList list) {
		return ofImpl(list.toPrimitiveArray());
	}

	public static AdvancedFloatStatistics of(float[] arr) {
		return ofImpl(Arrays.copyOf(arr, arr.length));
	}

	private static AdvancedFloatStatistics ofImpl(float[] data) {
		Arrays.sort(data);
		
		float median = median(data);

		float quartile25 = percentile(data, 0.25f);
		float quartile75 = percentile(data, 0.75f);

		float[] medianDeltas = new float[data.length];
		float medianAbsoluteDeviation = 0;
		
		for ( int i = 0; i < data.length; ++i ) {
			medianDeltas[i] = Math.abs( data[i] - median );
			Arrays.sort(medianDeltas);
						
			medianAbsoluteDeviation = AdvancedFloatStatistics.MEDIAN_ABSOLUTE_DEVIATION_CONSTANT * median(medianDeltas);			
		}

		AdvancedFloatStatistics result = new AdvancedFloatStatistics(median, quartile25, quartile75, medianAbsoluteDeviation);
		result.add(data);
		return result;
	}

	/*
	 * assumes sorted data
	 */
	private static float median( float[] data ) {
		final int n = data.length;

		int middle = n/2;

		float median;
        if (n % 2 == 1) {
        	median = data[middle];
        } else {
        	median = (data[middle-1] + data[middle])*0.5f;
        }
        
        return median;
	}

	/*
	 * assumes sorted data
	 */
	private static float percentile(float[] data, float percentile) {
		final int n = data.length;

		float k = (n - 1) * percentile;
		int f = (int) Math.floor(k);
		int c = (int) Math.ceil(k);
		if (f == c) {
			return data[(int) k];
		} else {
			float d0 = data[f] * (c - k);
			float d1 = data[c] * (k - f);
			return d0 + d1;
		}
	}

}
