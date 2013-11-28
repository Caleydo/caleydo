/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.internal.event;

import org.caleydo.core.event.ADirectedEvent;
import org.caleydo.core.util.base.ICallback;

/**
 * simple generic event for filtering changes
 *
 * @author Samuel Gratzl
 *
 */
public class SearchEvent extends ADirectedEvent {
	private final Object search;
	private final boolean isWrapSearch;
	private final boolean isForward;
	private final ICallback<SearchResult> callback;
	/**
	 * @param filter
	 */
	public SearchEvent(Object search, boolean isWrapSearch, boolean forward, ICallback<SearchResult> callback) {
		super();
		this.search = search;
		this.isWrapSearch = isWrapSearch;
		this.isForward = forward;
		this.callback = callback;
	}

	/**
	 * @return the callback, see {@link #callback}
	 */
	public ICallback<SearchResult> getCallback() {
		return callback;
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

	public final static class SearchResult {
		private final int hits;

		public SearchResult(int hits) {
			this.hits = hits;
		}

		/**
		 * @return the hits, see {@link #hits}
		 */
		public int getHits() {
			return hits;
		}
	}
}
