/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.vis.rank.internal.event;

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
