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
package org.caleydo.core.data.filter.event;

import java.util.ArrayList;
import org.caleydo.core.data.filter.Filter;
import org.caleydo.core.event.AEvent;

/**
 * <p>
 * Event that signals that the properties of a filter have changed, and that the VA therefore needs to be
 * reevaluated.
 * </p>
 * <p>
 * By specifying one or more filters that have been modified
 * </p>
 * 
 * @author Alexander Lex
 */
public abstract class ReEvaluateFilterListEvent<FilterType extends Filter<?>>
	extends AEvent {

	ArrayList<FilterType> filterList = null;

	/**
	 * List of filters that have been modified.
	 * 
	 * @param filter
	 */
	public void addFilter(FilterType filter) {
		if (filterList == null)
			filterList = new ArrayList<FilterType>(3);
		filterList.add(filter);
	}

	/**
	 * Returns the list of modified filters, or null if none were specified
	 * 
	 * @return
	 */
	public ArrayList<FilterType> getFilterList() {
		return filterList;
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}

}
