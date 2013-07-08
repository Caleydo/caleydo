/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
