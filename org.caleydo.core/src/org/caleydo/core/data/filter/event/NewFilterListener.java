package org.caleydo.core.data.filter.event;

import org.caleydo.core.data.filter.Filter;
import org.caleydo.core.data.filter.FilterManager;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;

/**
 * 
 * @author Alexander Lex
 */
public abstract class NewFilterListener<FilterType extends Filter<?>, FilterManagerType extends FilterManager<?, ?, FilterType, ?>>
	extends AEventListener<FilterManagerType> {

	@SuppressWarnings("unchecked")
	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof NewFilterEvent<?>) {
			handler.addFilter(((NewFilterEvent<FilterType>) event).getFilter());
		}
	}
}
