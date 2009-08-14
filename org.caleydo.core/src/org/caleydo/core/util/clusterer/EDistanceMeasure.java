package org.caleydo.core.util.clusterer;

public enum EDistanceMeasure {

	// self implemented
	EUCLIDEAN_DISTANCE,
	PEARSON_CORRELATION,

	/**
	 * Distance measure included in weka, only usable with {@link KMeansClusterer}
	 */
	MANHATTAHN_DISTANCE;
}
