package org.caleydo.core.util.clusterer;

import org.caleydo.core.data.perspective.PerspectiveInitializationData;

public class ClusterResult {

	PerspectiveInitializationData recordResult;
	PerspectiveInitializationData dimensionResult;

	/**
	 * Determines group information for virtual array. Used by affinity propagation and kMeans.
	 * 
	 * @param VAId
	 *            Id of virtual array
	 */
	// void finish() {
	// if (recordResult != null)
	// recordResult.finish();
	// if (dimensionResult != null)
	// dimensionResult.finish();
	// }

	public PerspectiveInitializationData getRecordResult() {
		return recordResult;
	}

	public PerspectiveInitializationData getDimensionResult() {
		return dimensionResult;
	}

	/**
	 * @param recordResult
	 *            setter, see {@link #recordResult}
	 */
	public void setRecordResult(PerspectiveInitializationData recordResult) {
		this.recordResult = recordResult;
	}

	/**
	 * @param dimensionResult
	 *            setter, see {@link #dimensionResult}
	 */
	public void setDimensionResult(PerspectiveInitializationData dimensionResult) {
		this.dimensionResult = dimensionResult;
	}
}
