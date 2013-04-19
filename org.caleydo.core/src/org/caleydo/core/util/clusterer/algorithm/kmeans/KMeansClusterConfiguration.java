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
package org.caleydo.core.util.clusterer.algorithm.kmeans;

import org.caleydo.core.util.clusterer.initialization.AClusterAlgorithmConfiguration;

/**
 * @author Alexander Lex
 *
 */
public class KMeansClusterConfiguration extends AClusterAlgorithmConfiguration {

	private int numberOfClusters = -1;
	/**
	 * cache item vectors, faster but needs more memory
	 */
	private boolean cacheVectors = false;

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

	/**
	 * @return the cacheVectors, see {@link #cacheVectors}
	 */
	public boolean isCacheVectors() {
		return cacheVectors;
	}

	/**
	 * @param cacheVectors
	 *            setter, see {@link cacheVectors}
	 */
	public void setCacheVectors(boolean cacheVectors) {
		this.cacheVectors = cacheVectors;
	}

	@Override
	public String toString() {
		return "KMeans " + numberOfClusters + " Clusters";
	}
}
