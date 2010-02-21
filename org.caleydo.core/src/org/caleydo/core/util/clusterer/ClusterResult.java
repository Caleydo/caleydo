package org.caleydo.core.util.clusterer;

public class ClusterResult {

	ContentClusterResult contentResult;
	StorageClusterResult storageResult;

	/**
	 * Determines group information for virtual array. Used by affinity propagation and kMeans.
	 * 
	 * @param VAId
	 *            Id of virtual array
	 */
	void finish() {
		if (contentResult != null)
			contentResult.finish();
		if (storageResult != null)
			storageResult.finish();
	}

	public ContentClusterResult getContentResult() {
		return contentResult;
	}

	public StorageClusterResult getStorageResult() {
		return storageResult;
	}
}
