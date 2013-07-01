/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.clusterer.algorithm.kmeans;

import org.caleydo.core.util.clusterer.initialization.AClusterAlgorithmConfiguration;

/**
 * @author Alexander Lex
 *
 */
public class KMeansClusterConfiguration extends AClusterAlgorithmConfiguration {

	private int numberOfClusters = -1;

	public KMeansClusterConfiguration() {
		super("K-Means");
	}

	/**
	 * @param numberOfClusters
	 *            setter, see {@link #numberOfClusters}
	 */
	public void setNumberOfClusters(int numberOfClusters) {
		this.numberOfClusters = numberOfClusters;
	}

	/**
	 * @return the numberOfClusters, see {@link #numberOfClusters}
	 */
	public int getNumberOfClusters() {
		return numberOfClusters;
	}
}
