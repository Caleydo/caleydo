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

/**
 * @author Samuel Gratzl
 *
 */
public class FloatStatistics {
	private final float min, max, sum, sqrsum;
	private final int n, nans;

	public FloatStatistics(float min, float max, float sum, float sqrsum, int n, int nans) {
		this.min = min;
		this.max = max;
		this.sum = sum;
		this.sqrsum = sqrsum;
		this.n = n;
		this.nans = nans;
	}

	public float getMean() {
		return sum / n;
	}

	public float getVariance() {
		return (n * sqrsum - (sum * sum)) / (n * (n - 1));
	}

	public float getStandardDeviation() {
		return (float) Math.sqrt(getVariance());
	}

	/**
	 * @return the min, see {@link #min}
	 */
	public float getMin() {
		return min;
	}

	/**
	 * @return the max, see {@link #max}
	 */
	public float getMax() {
		return max;
	}

	/**
	 * @return the sum, see {@link #sum}
	 */
	public float getSum() {
		return sum;
	}

	/**
	 * @return the sqrsum, see {@link #sqrsum}
	 */
	public float getSqrSum() {
		return sqrsum;
	}

	/**
	 * @return the n, see {@link #n}
	 */
	public int getN() {
		return n;
	}

	/**
	 * @return the nans, see {@link #nans}
	 */
	public int getNaNs() {
		return nans;
	}

	public static FloatStatistics compute(IFloatIterator it) {
		float min = Float.POSITIVE_INFINITY;
		float max = Float.NEGATIVE_INFINITY;
		int n = 0;
		int nans = 0;
		float sum = 0;
		float sqrsum = 0;
		boolean any = false;
		while (it.hasNext()) {
			float v = it.nextPrimitive();
			if (Float.isNaN(v)) {
				nans++;
				continue;
			}
			n++;
			sum += v;
			sqrsum += v * v;
			if (v < min)
				min = v;
			if (max < v)
				max = v;
			any = true;
		}
		if (!any)
			return new FloatStatistics(Float.NaN, Float.NaN, 0, 0, 0, 0);
		return new FloatStatistics(min, max, sum, sqrsum, n, nans);
	}
}
