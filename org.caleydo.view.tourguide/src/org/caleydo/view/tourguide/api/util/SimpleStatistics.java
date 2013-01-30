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
package org.caleydo.view.tourguide.api.util;


/**
 * simple statistics for floats
 *
 * @author Samuel Gratzl
 *
 */
public class SimpleStatistics {
	private float min;
	private float max;
	private float mean;
	private float sd;
	private int n;
	private int n_na;

	public SimpleStatistics(float min, float max, float mean, float sd, int n, int n_na) {
		this.min = min;
		this.max = max;
		this.mean = mean;
		this.sd = sd;
		this.n = n;
		this.n_na = n_na;
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

	public float getRange() {
		return max - min;
	}

	public float normalize(float v) {
		return (v - min) / (max - min);
	}

	public float meanNormalize(float v) {
		return v - mean;
	}

	/**
	 * @return the mean, see {@link #mean}
	 */
	public float getMean() {
		return mean;
	}

	/**
	 * @return the sd, see {@link #sd}
	 */
	public float getStandardDeviation() {
		return sd;
	}

	/**
	 * @return the n, see {@link #n}
	 */
	public int getCount() {
		return n;
	}

	/**
	 * @return the n_na, see {@link #n_na}
	 */
	public int getNACount() {
		return n_na;
	}

	/**
	 * builder pattern for the {@link SimpleStatistics}
	 *
	 * @author Samuel Gratzl
	 *
	 */
	public static class Builder {
		private float min = Float.MAX_VALUE;
		private float max = Float.MIN_VALUE;
		private float sum = 0;
		private float sqsum = 0;
		private int n = 0;
		private int n_na = 0;

		public void add(float f) {
			if (Float.isNaN(f)) {
				n_na++;
			} else {
				if (f < min)
					min = f;
				if (f > max)
					max = f;
				sum += f;
				sqsum += f * f;
				n++;
			}
		}

		public SimpleStatistics build() {
			float mean;
			float sd;
			if (n == 0) {
				min = Float.NaN;
				max = Float.NaN;
				mean = Float.NaN;
				sd = 0;
			} else {
				mean = sum / n;
				sd = (float) Math.sqrt(sqsum / n - (sum / n) * (sum / n));
			}

			return new SimpleStatistics(min, max, mean, sd, n + n_na, n_na);
		}
	}

	public static SimpleStatistics of(float[] arr) {
		Builder b = new Builder();
		for (float a : arr)
			b.add(a);
		return b.build();
	}
}
