/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.rank.internal.event;

import java.util.Date;

import org.caleydo.core.event.ADirectedEvent;

/**
 * simple generic event for filtering changes
 *
 * @author Samuel Gratzl
 *
 */
public class DateFilterEvent extends ADirectedEvent {
	private Date before;
	private Date after;

	/**
	 * @param filter
	 */
	public DateFilterEvent(Date before, Date after) {
		super();
		this.before = before;
		this.after = after;
	}

	/**
	 * @return the before, see {@link #before}
	 */
	public Date getBefore() {
		return before;
	}
	
	/**
	 * @return the after, see {@link #after}
	 */
	public Date getAfter() {
		return after;
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}
}
