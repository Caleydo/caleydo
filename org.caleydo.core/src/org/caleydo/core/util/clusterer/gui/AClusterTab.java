/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.clusterer.gui;

import org.caleydo.core.util.clusterer.initialization.AClusterAlgorithmConfiguration;
import org.caleydo.core.util.clusterer.initialization.ClusterConfiguration;
import org.caleydo.core.util.clusterer.initialization.EDistanceMeasure;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/**
 * Base class for a tab containing clustering algorithm-specific configurations.
 * It is expected that a new {@link TabItem} is registered with the
 * <code>TabFolder</code> when the constructor is called. The algorithm-specific
 * instance of {@link ClusterConfiguration} is expected to be returned with all
 * algorithm-specific parameters set when {@link #getClusterConfiguration()} is
 * called.
 *
 * @author Alexander Lex
 *
 */
public abstract class AClusterTab {
	protected final TabFolder tabFolder;

	protected TabItem clusterTab;
	/**
	 *
	 */
	public AClusterTab(TabFolder tabFolder) {
		this.tabFolder = tabFolder;
	}

	/** Returns an algorithm-specific cluster configuration */
	public abstract AClusterAlgorithmConfiguration getClusterConfiguration();

	/**
	 * Returns the distance measures supported by this clustering algorithm. By
	 * default returns all measures, must be overriden to reduce the set of
	 * measures
	 */
	public String[] getSupportedDistanceMeasures() {
		return EDistanceMeasure.getNames();
	}

}
