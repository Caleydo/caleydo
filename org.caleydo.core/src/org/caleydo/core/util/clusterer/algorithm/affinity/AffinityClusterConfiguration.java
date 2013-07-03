/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.clusterer.algorithm.affinity;

import org.caleydo.core.util.clusterer.initialization.AClusterAlgorithmConfiguration;

/**
 * @author Alexander Lex
 *
 */
public class AffinityClusterConfiguration extends AClusterAlgorithmConfiguration {

	private float clusterFactor;

	public AffinityClusterConfiguration() {
		super("Affinty Propagation");
	}

	/**
	 * @param clusterFactor
	 *            setter, see {@link #clusterFactor}
	 */
	public void setClusterFactor(float clusterFactor) {
		this.clusterFactor = clusterFactor;
	}

	/**
	 * @return the clusterFactor, see {@link #clusterFactor}
	 */
	public float getClusterFactor() {
		return clusterFactor;
	}

}
