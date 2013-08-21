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
public class SearchEvent extends ADirectedEvent {
	private Object search;
	private boolean isWrapSearch;
	private boolean isForward;
	/**
	 * @param filter
	 */
	public SearchEvent(Object search, boolean isWrapSearch, boolean forward) {
		super();
		this.search = search;
		this.isWrapSearch = isWrapSearch;
		this.isForward = forward;
	}

	/**
	 * @return the isForward, see {@link #isForward}
	 */
	public boolean isForward() {
		return isForward;
	}
	/**
	 * @return the isWrapSearch, see {@link #isWrapSearch}
	 */
	public boolean isWrapSearch() {
		return isWrapSearch;
	}

	/**
	 * @return the filter, see {@link #search}
	 */
	public Object getSearch() {
		return search;
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}
}
