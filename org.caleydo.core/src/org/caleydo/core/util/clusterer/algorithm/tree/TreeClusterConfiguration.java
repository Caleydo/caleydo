/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.clusterer.algorithm.tree;

import org.caleydo.core.util.clusterer.initialization.AClusterAlgorithmConfiguration;

/**
 * @author Alexander Lex
 *
 */
public class TreeClusterConfiguration extends AClusterAlgorithmConfiguration {

	private ETreeClustererAlgo treeClustererAlgo;

	public TreeClusterConfiguration() {
		super("Tree Clusterer");
	}

	public void setTreeClustererAlgo(ETreeClustererAlgo treeClustererAlgo) {
		this.treeClustererAlgo = treeClustererAlgo;
	}

	public ETreeClustererAlgo getTreeClustererAlgo() {
		return treeClustererAlgo;
	}

}
