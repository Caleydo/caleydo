package org.caleydo.core.util.clusterer;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.event.IListenerOwner;

public interface IClusterer
	extends IListenerOwner {

	/**
	 * Clusters a given set and returns the Id of the new generated virtual array with sorted indexes
	 * according to the cluster result
	 * 
	 * @param set
	 *            Set
	 * @param iVAIdContent
	 *            ID of the content VA
	 * @param iVAIdStorage
	 *            Id of the storage VA
	 * @param clusterState
	 *            Container for cluster info (algo, type, ...)
	 * @param iProgressBarOffsetValue
	 *            Offset value needed for overall progress bar while bi clustering. During the first run the
	 *            value is 0 and during the second run 50.
	 * @param iProgressBarMultiplier
	 *            multiplier needed for overall progress bar. In case of bi clustering the value is 1. In case
	 *            of normal clustering the value is 2.
	 * @return Id of the sorted VirtualArray
	 */
	public Integer getSortedVAId(ISet set, Integer iVAIdContent, Integer iVAIdStorage,
		ClusterState clusterState, int iProgressBarOffsetValue, int iProgressBarMultiplier);

	public void cancel();

}
