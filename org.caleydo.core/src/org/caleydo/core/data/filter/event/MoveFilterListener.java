package org.caleydo.core.data.filter.event;

import org.caleydo.core.data.filter.Filter;
import org.caleydo.core.data.filter.FilterManager;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;

/**
 * Listener for {@link MoveFilterEvent}s.
 * 
 * @author Thomas Geymayer
 */
public abstract class MoveFilterListener<FilterType extends Filter<?>>
	extends AEventListener<FilterManager<?, ?, FilterType, ?>> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof MoveFilterEvent<?>) {
			MoveFilterEvent<?> moveFilterEvent = (MoveFilterEvent<?>) event;
			handler.handleMoveFilter
			(
				moveFilterEvent.getFilter(),
				moveFilterEvent.getOffset()
			);
		}
	}
}
