/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.internal.event;

import org.caleydo.core.event.ADirectedEvent;

/**
 * simple generic event for filtering changes
 *
 * @author Samuel Gratzl
 *
 */
public class IntegerFilterEvent extends ADirectedEvent {
	private Integer min;
	private Integer max;

	/**
	 * @param filter
	 */
	public IntegerFilterEvent(Integer min, Integer max) {
		super();
		this.min = min;
		this.max = max;
	}

	/**
	 * @return the min, see {@link #min}
	 */
	public Integer getMin() {
		return min;
	}

	/**
	 * @return the max, see {@link #max}
	 */
	public Integer getMax() {
		return max;
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}
}
