/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
public class ReEvaluateFilterListEvent
	extends AEvent {

	ArrayList<Filter> filterList = null;

	/**
	 * List of filters that have been modified.
	 *
	 * @param filter
	 */
	public void addFilter(Filter filter) {
		if (filterList == null)
			filterList = new ArrayList<Filter>(3);
		filterList.add(filter);
	}

	/**
	 * Returns the list of modified filters, or null if none were specified
	 *
	 * @return
	 */
	public ArrayList<Filter> getFilterList() {
		return filterList;
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}

}
