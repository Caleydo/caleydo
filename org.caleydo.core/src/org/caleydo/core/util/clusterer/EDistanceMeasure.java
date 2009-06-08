package org.caleydo.core.util.clusterer;

public enum EDistanceMeasure {

	// self implemented
	EUCLIDEAN_DISTANCE,
	PEARSON_CORRELATION,

	// distance measure included in weka, only usable with kMeans
	MANHATTAHN_DISTANCE;
}
