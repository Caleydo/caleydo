package org.caleydo.core.util.clusterer;

import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.data.perspective.RecordPerspective;

public class ClusterResult {

	TempResult recordResult;
	TempResult dimensionResult;

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

	public TempResult getRecordResult() {
		return recordResult;
	}

	public TempResult getDimensionResult() {
		return dimensionResult;
	}

	/**
	 * @param recordResult
	 *            setter, see {@link #recordResult}
	 */
	public void setRecordResult(TempResult recordResult) {
		this.recordResult = recordResult;
	}

	/**
	 * @param dimensionResult
	 *            setter, see {@link #dimensionResult}
	 */
	public void setDimensionResult(TempResult dimensionResult) {
		this.dimensionResult = dimensionResult;
	}
}
