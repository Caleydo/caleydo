package org.caleydo.core.data.filter.event;

import org.caleydo.core.data.filter.Filter;
import org.caleydo.core.manager.event.AEvent;

/**
 * @author Alexander Lex
 *
 */
public class FilterEvent<FilterType extends Filter<?>>
	extends AEvent {

	private FilterType filter = null;

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
