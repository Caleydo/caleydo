package org.caleydo.core.data.collection;

/**
 * Creates a Histogram based on a vector. Calculates meta-data on the vector (currently min max, to be
 * extended)
 * 
 * @author Alexander Lex
 */
public class HistogramCreator {

	public static Histogram createHistogram(double[] vector) {

		int iNumberOfBuckets = (int) Math.sqrt(vector.length);
		Histogram histogram = new Histogram(iNumberOfBuckets);

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

		for (int count = 0; count < iNumberOfBuckets; count++) {

			histogram.add(0);
		}

		for (double value : vector) {
			int iIndex = (int) (normalize(min, max, value) * iNumberOfBuckets);
			if (iIndex == iNumberOfBuckets)
				iIndex--;
			Integer iNumOccurences = histogram.get(iIndex);
			histogram.set(iIndex, ++iNumOccurences);
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
		histogram.setMax((float) max);
		histogram.setMin((float) min);

		for (int count = 0; count < iNumberOfBuckets; count++) {

			histogram.add(0);
		}

		for (float value : vector) {
			int iIndex = (int) (normalize(min, max, value) * iNumberOfBuckets);
			if (iIndex == iNumberOfBuckets)
				iIndex--;
			Integer iNumOccurences = histogram.get(iIndex);
			histogram.set(iIndex, ++iNumOccurences);
		}
		return histogram;
	}

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
