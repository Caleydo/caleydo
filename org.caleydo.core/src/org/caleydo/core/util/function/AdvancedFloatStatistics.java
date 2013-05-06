/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.util.function;

import java.util.Arrays;

/**
 * advanced version of {@link FloatStatistics} including median, and quartiles
 *
 * @author Samuel Gratzl
 *
 */
public class AdvancedFloatStatistics extends FloatStatistics {
	private final float median, quartile25, quartile75;

	public AdvancedFloatStatistics(float median, float quartile25, float quartile75) {
		this.median = median;
		this.quartile25 = quartile25;
		this.quartile75 = quartile75;
	}

	/**
	 * @return the median, see {@link #median}
	 */
	public float getMedian() {
		return median;
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
		final int n = data.length;

		int middle = n/2;

		float median;
        if (n % 2 == 1) {
        	median = data[middle];
        } else {
        	median = (data[middle-1] + data[middle])*0.5f;
        }

		float quartile25 = percentile(data, 0.25f);
		float quartile75 = percentile(data, 0.75f);

		AdvancedFloatStatistics result = new AdvancedFloatStatistics(median, quartile25, quartile75);
		result.add(data);
		return result;
	}

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
