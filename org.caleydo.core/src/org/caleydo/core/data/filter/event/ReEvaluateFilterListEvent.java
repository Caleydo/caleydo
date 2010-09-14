package org.caleydo.core.data.filter.event;

import java.util.ArrayList;

import org.caleydo.core.data.filter.Filter;
import org.caleydo.core.manager.event.AEvent;

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
