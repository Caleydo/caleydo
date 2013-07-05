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
public class OrderByMeEvent extends ADirectedEvent {
	private boolean cloneAndAddNext;

	public OrderByMeEvent() {
		this(false);
	}

	public OrderByMeEvent(boolean cloneAndAddNext) {
		this.cloneAndAddNext = cloneAndAddNext;
	}

	/**
	 * @return the cloneAndAddNext, see {@link #cloneAndAddNext}
	 */
	public boolean isCloneAndAddNext() {
		return cloneAndAddNext;
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}
}

