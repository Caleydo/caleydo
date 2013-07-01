/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.clusterer;

import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;

public class ClusterResult {

	private PerspectiveInitializationData recordResult;
	private PerspectiveInitializationData dimensionResult;

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
