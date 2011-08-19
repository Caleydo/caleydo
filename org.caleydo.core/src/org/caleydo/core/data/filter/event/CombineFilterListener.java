package org.caleydo.core.data.filter.event;

import java.util.Collection;

import org.caleydo.core.data.filter.Filter;
import org.caleydo.core.data.filter.FilterManager;
import org.caleydo.core.data.filter.RecordFilter;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;

/**
 * Listener for {@link CombineFilterEvent}s.
 * 
 * @author Thomas Geymayer
 */
public abstract class CombineFilterListener<FilterType extends Filter<?>>
	extends AEventListener<FilterManager<?, ?, FilterType, ?>> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof CombineFilterEvent<?>) {
			CombineFilterEvent<?> filterEvent = (CombineFilterEvent<?>) event;
			handler.handleCombineFilter(filterEvent.getFilter(),
				(Collection<? extends RecordFilter>) filterEvent.getCombineFilters());
		}
	}
}
