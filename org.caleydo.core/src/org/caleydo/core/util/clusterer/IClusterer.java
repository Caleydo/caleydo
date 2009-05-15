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
	 * @return Id of the sorted VirtualArray
	 */
	public Integer getSortedVAId(ISet set, Integer iVAIdContent, Integer iVAIdStorage,
		ClusterState clusterState);

	public void cancel();

}
