/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.clusterer.initialization;

import org.caleydo.core.util.base.ILabelProvider;

/**
 * @author alexsb
 *
 */
public class AClusterAlgorithmConfiguration implements ILabelProvider {

	/**
	 * The name of the clustering algorithm, must be overridden in implementing
	 * classes
	 */
	private final String label;

	/**
	 *
	 */
	public AClusterAlgorithmConfiguration(String clusterAlgorithmName) {
		this.label = clusterAlgorithmName;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public String getProviderName() {
		return null;
	}

}
