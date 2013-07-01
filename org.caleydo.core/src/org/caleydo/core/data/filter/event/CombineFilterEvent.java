/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.filter.event;

import java.util.ArrayList;
import java.util.Collection;

import org.caleydo.core.data.filter.Filter;

/**
 * @author Thomas Geymayer
 */
public class CombineFilterEvent extends FilterEvent {

	private ArrayList<Filter> combineFilters = new ArrayList<>();

	public void addCombineFilter(Filter filter) {
		combineFilters.add(filter);
	}

	public Collection<Filter> getCombineFilters() {
		return combineFilters;
	}

	@Override
	public boolean checkIntegrity() {
		if (!super.checkIntegrity() || combineFilters.isEmpty())
			return false;

		return true;
	}
}
