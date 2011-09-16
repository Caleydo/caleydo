package org.caleydo.core.util.clusterer;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.PerspectiveInitializationData;
import org.caleydo.core.event.IListenerOwner;
import org.caleydo.core.util.clusterer.initialization.ClusterConfiguration;

/**
 * Interface class for all clustering algorithms.
 * 
 * @author Bernhard Schlegl
 */
public interface IClusterer
	extends IListenerOwner {

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
	public PerspectiveInitializationData getSortedVA(ATableBasedDataDomain dataDomain, ClusterConfiguration clusterState, int iProgressBarOffsetValue,
		int iProgressBarMultiplier);

	public void cancel();

}
