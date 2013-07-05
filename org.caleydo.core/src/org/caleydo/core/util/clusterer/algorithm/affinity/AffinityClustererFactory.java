/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.clusterer.algorithm.affinity;

import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.util.clusterer.IClustererFactory;
import org.caleydo.core.util.clusterer.gui.AClusterTab;
import org.caleydo.core.util.clusterer.initialization.ClusterConfiguration;
import org.caleydo.core.util.execution.SafeCallable;
import org.eclipse.swt.widgets.TabFolder;

/**
 * @author Samuel Gratzl
 *
 */
public class AffinityClustererFactory implements IClustererFactory {

	@Override
	public AClusterTab createClusterTab(TabFolder tabFolder) {
		return new AffinityTab(tabFolder);
	}

	@Override
	public SafeCallable<PerspectiveInitializationData> create(ClusterConfiguration config, int progressBarMultiplier,
			int progressBarOffset) {
		if (!(config.getClusterAlgorithmConfiguration() instanceof AffinityClusterConfiguration))
			return null;
		return new AffinityClusterer(config, progressBarMultiplier, progressBarOffset);
	}
}
