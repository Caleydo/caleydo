package org.caleydo.core.util.clusterer;

import org.caleydo.core.data.collection.table.RecordData;
import org.caleydo.core.data.collection.table.DimensionData;

public class ClusterResult {

	RecordData contentResult;
	DimensionData storageResult;

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

	public RecordData getContentResult() {
		return contentResult;
	}

	public DimensionData getStorageResult() {
		return storageResult;
	}
}
