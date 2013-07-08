/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.rank.internal.event;

import org.caleydo.core.event.ADirectedEvent;
import org.caleydo.vis.rank.ui.column.OrderColumnUI;

/**
 * @author Samuel Gratzl
 *
 */
public class TriggerEditValuesEvent extends ADirectedEvent {

	private OrderColumnUI ranker;

	/**
	 * @param col
	 */
	public TriggerEditValuesEvent(OrderColumnUI ranker) {
		this.ranker = ranker;
	}

	/**
	 * @return the ranker, see {@link #ranker}
	 */
	public OrderColumnUI getRanker() {
		return ranker;
	}

	@Override
	public boolean checkIntegrity() {
		return ranker != null;
	}

}

