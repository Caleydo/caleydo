/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.rank.internal.event;

import java.util.Calendar;

import org.caleydo.core.event.ADirectedEvent;

/**
 * simple generic event for filtering changes
 *
 * @author Samuel Gratzl
 *
 */
public class DateFilterEvent extends ADirectedEvent {
	private Calendar before;
	private Calendar after;

	/**
	 * @param filter
	 */
	public DateFilterEvent(Calendar before, Calendar after) {
		super();
		this.before = before;
		this.after = after;
	}

	/**
	 * @return the before, see {@link #before}
	 */
	public Calendar getBefore() {
		return before;
	}

	/**
	 * @return the after, see {@link #after}
	 */
	public Calendar getAfter() {
		return after;
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}
}
