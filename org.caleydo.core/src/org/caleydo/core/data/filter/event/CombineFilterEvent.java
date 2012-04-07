package org.caleydo.core.data.filter.event;

import java.util.ArrayList;
import java.util.Collection;

import org.caleydo.core.data.filter.Filter;

/**
 * @author Thomas Geymayer
 */
public class CombineFilterEvent<FilterType extends Filter<?>>
	extends FilterEvent<FilterType> {

	private ArrayList<FilterType> combineFilters = new ArrayList<FilterType>();

	public void addCombineFilter(FilterType filter) {
		combineFilters.add(filter);
	}

	public Collection<FilterType> getCombineFilters() {
		return combineFilters;
	}

	@Override
	public boolean checkIntegrity() {
		if (!super.checkIntegrity() || combineFilters.isEmpty())
			return false;

		return true;
	}
}
