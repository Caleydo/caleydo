package org.caleydo.core.util.clusterer;

public class ClusterResult {

	ContentData contentResult;
	StorageData storageResult;

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

	public ContentData getContentResult() {
		return contentResult;
	}

	public StorageData getStorageResult() {
		return storageResult;
	}
}
