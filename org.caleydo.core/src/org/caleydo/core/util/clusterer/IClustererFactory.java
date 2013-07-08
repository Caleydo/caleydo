/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.clusterer;

import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.util.clusterer.gui.AClusterTab;
import org.caleydo.core.util.clusterer.initialization.ClusterConfiguration;
import org.caleydo.core.util.execution.SafeCallable;
import org.eclipse.swt.widgets.TabFolder;

/**
 * Interface class for all clustering algorithms.
 *
 * @author Bernhard Schlegl
 */
public interface IClustererFactory {

	/**
	 * returns the visual component to config this kind of clusterer
	 *
	 * @param tabFolder
	 * @return
	 */
	AClusterTab createClusterTab(TabFolder tabFolder);

	/**
	 * Clusters a given set and returns the Id of the new generated virtual array with sorted indexes
	 * according to the cluster result. If an error occurs or an user aborts the cluster process a negative
	 * value will be returned.
	 *
	 * @param set
	 *            Set
	 * @param clusterState
	 *            Container for cluster info (algo, type, ...)
	 * @param iProgressBarOffsetValue
	 *            Offset value needed for overall progress bar while bi clustering. During the first run the
	 *            value is 0 and during the second run 50.
	 * @param iProgressBarMultiplier
	 *            multiplier needed for overall progress bar. In case of bi clustering the value is 1. In case
	 *            of normal clustering the value is 2.
	 * @return Sorted VirtualArray.
	 */
	public SafeCallable<PerspectiveInitializationData> create(ClusterConfiguration config,
			int progressBarMultiplier, int progressBarOffset);
}
