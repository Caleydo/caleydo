package org.caleydo.core.util.clusterer;

import org.caleydo.core.data.collection.table.DimensionPerspective;
import org.caleydo.core.data.collection.table.RecordPerspective;

public class ClusterResult {

	RecordPerspective recordResult;
	DimensionPerspective dimensionResult;

	/**
	 * Determines group information for virtual array. Used by affinity propagation and kMeans.
	 * 
	 * @param VAId
	 *            Id of virtual array
	 */
	void finish() {
		if (recordResult != null)
			recordResult.finish();
		if (dimensionResult != null)
			dimensionResult.finish();
	}

	public RecordPerspective getRecordResult() {
		return recordResult;
	}

	public DimensionPerspective getDimensionResult() {
		return dimensionResult;
	}
}
