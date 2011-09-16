package org.caleydo.core.data.filter.event;

import org.caleydo.core.data.filter.Filter;
import org.caleydo.core.event.AEvent;

/**
 * Base class for events signaling a new Filter
 * 
 * @author Alexander Lex
 * @param <FilterType>
 */
public abstract class NewFilterEvent<FilterType extends Filter<?>>
	extends AEvent {

	FilterType filter = null;

	public void setFilter(FilterType filter) {
		this.filter = filter;
	}

	public FilterType getFilter() {
		return filter;
	}

	@Override
	public boolean checkIntegrity() {
		if (filter == null)
			return false;

		return true;
	}

}
