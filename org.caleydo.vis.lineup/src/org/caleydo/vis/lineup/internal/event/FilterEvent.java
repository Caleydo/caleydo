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
public class FilterEvent extends ADirectedEvent {
	private Object filter;
	private boolean filterGlobally;
	private boolean filterNA;

	/**
	 * @param filter
	 */
	public FilterEvent(Object filter) {
		super();
		this.filter = filter;
	}

	public FilterEvent(Object filter, boolean filterNA, boolean filterGlobally) {
		super();
		this.filterNA = filterNA;
		this.filter = filter;
		this.filterGlobally = filterGlobally;
	}

	/**
	 * @return the filterGlobally, see {@link #filterGlobally}
	 */
	public boolean isFilterGlobally() {
		return filterGlobally;
	}

	/**
	 * @return
	 */
	public boolean isFilterNA() {
		return filterNA;
	}

	/**
	 * @return the filter, see {@link #filter}
	 */
	public Object getFilter() {
		return filter;
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}
}
