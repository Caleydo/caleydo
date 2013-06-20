/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.data.collection;

/**
 * Creates a Histogram based on a vector. Calculates meta-data on the vector (currently min max, to be extended)
 *
 * @author Alexander Lex
 */
public class HistogramCreator {

	public static Histogram createHistogram(double[] vector) {

		int numberOfBuckets = (int) Math.sqrt(vector.length);
		Histogram histogram = new Histogram(numberOfBuckets);// private ArrayList<Integer>

		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;

		for (int count = 0; count < vector.length; count++)

		{
			if (vector[count] > max)
				max = vector[count];
			else if (vector[count] < min)
				min = vector[count];
		}
		histogram.setMax((float) max);
		histogram.setMin((float) min);

		for (double value : vector) {
			if (Double.isNaN(value)) {
				histogram.addNAN(0);
			} else {
				int iIndex = (int) (normalize(min, max, value) * numberOfBuckets);
				if (iIndex == numberOfBuckets)
					iIndex--;
				histogram.add(iIndex, 0);
			}
		}
		return histogram;
	}

	public static Histogram createHistogram(float[] vector) {

		int iNumberOfBuckets = (int) Math.sqrt(vector.length);
		Histogram histogram = new Histogram(iNumberOfBuckets);

		float min = Float.MAX_VALUE;
		float max = Float.MIN_VALUE;

		for (int count = 0; count < vector.length; count++) {
			if (vector[count] > max)
				max = vector[count];
			else if (vector[count] < min)
				min = vector[count];
		}
		// histogram.setMax(max);
		// histogram.setMin(min);

		for (float value : vector) {
			if (Float.isNaN(value)) {
				histogram.addNAN(0);
			} else {
				int iIndex = (int) (normalize(min, max, value) * iNumberOfBuckets);
				if (iIndex == iNumberOfBuckets)
					iIndex--;
				histogram.add(iIndex, 0);
			}
		}
		return histogram;
	}

	// private ArrayList<Integer>

	public static Histogram createLogHistogram(double vec[]) {
		return createHistogram(runLog(vec));
	}

	private static double[] runLog(double vec[]) {
		double[] logVec = new double[vec.length];

		for (int count = 0; count < vec.length; count++) {
			double value = Math.log(Math.abs(vec[count]));
			if (vec[count] < 0)
				value *= -1;
			logVec[count] = value;
		}

		return logVec;
	}

	public static double normalize(double min, double max, double value) {
		double difference = max - min;
		double normalizedValue = (value - min) / difference;
		return normalizedValue;
	}

}
