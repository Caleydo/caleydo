package org.caleydo.core.data.filter.event;

import org.caleydo.core.data.filter.Filter;
import org.caleydo.core.data.filter.FilterManager;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;

/**
 * @author Alexander Lex
 */
public class RemoveFilterListener<FilterType extends Filter<?, ?>>
	extends AEventListener<FilterManager<?, ?, FilterType, ?>> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof RemoveFilterEvent<?>) {
			RemoveFilterEvent<?> removeFilterEvent = (RemoveFilterEvent<?>) event;
			handler.handleRemoveFilter(removeFilterEvent.getFilter());

		}

	}
}
