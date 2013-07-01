/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.rank.internal.event;

import org.caleydo.core.event.ADirectedEvent;

/**
 * @author Samuel Gratzl
 *
 */
public class WeightsChangedEvent extends ADirectedEvent {
	private final float[] weights;

	public WeightsChangedEvent(float[] weights) {
		this.weights = weights;
	}

	/**
	 * @return the weights, see {@link #weights}
	 */
	public float[] getWeights() {
		return weights;
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}


}

