package org.caleydo.core.util.clusterer;

import org.caleydo.core.data.collection.table.RecordData;
import org.caleydo.core.data.collection.table.DimensionData;

public class ClusterResult {

	RecordData contentResult;
	DimensionData dimensionResult;

	/**
	 * Determines group information for virtual array. Used by affinity propagation and kMeans.
	 * 
	 * @param VAId
	 *            Id of virtual array
	 */
	void finish() {
		if (contentResult != null)
			contentResult.finish();
		if (dimensionResult != null)
			dimensionResult.finish();
	}

	public RecordData getContentResult() {
		return contentResult;
	}

	public DimensionData getDimensionResult() {
		return dimensionResult;
	}
}
