package org.caleydo.core.manager.event;

import java.util.ArrayList;
import java.util.Collection;

import org.caleydo.core.manager.IEventPublisher;

/**
 * Implementation of {@link IEventPublisher}
 * 
 * @author Marc Streit
 * @author Alexander Lex
 * @author Werner Puff
 */
public class EventPublisher
	implements IEventPublisher {

	/** map of events (=key) to the listeners (=value, a collection of listeners) registered to it */
	private ListenerMap listenerMap;

	/**
	 * Constructor.
	 */
	public EventPublisher() {
		listenerMap = new ListenerMap();
	}

	@Override
	public void addListener(Class<? extends AEvent> eventClass, AEventListener<?> listener) {
		Collection<AEventListener<?>> listeners = listenerMap.get(eventClass);
		if (listeners == null) {
			listeners = new ArrayList<AEventListener<?>>();
			listenerMap.put(eventClass, listeners);
		}
		listeners.add(listener);
	}

	@Override
	public void removeListener(Class<? extends AEvent> eventClass, AEventListener<?> listener) {
		Collection<AEventListener<?>> listeners = listenerMap.get(eventClass);
		listeners.remove(listener);
	}

	@Override
	public void removeListener(AEventListener<?> listener) {
		for (Collection<AEventListener<?>> listeners : listenerMap.values()) {
			listeners.remove(listener);			
		}
	}

	@Override
	public void triggerEvent(AEvent event) {
		Collection<AEventListener<?>> listeners = listenerMap.get(event.getClass());
		if (listeners != null) {
			for (AEventListener<?> receiver : listeners) {
				receiver.handleEventFiltered(event);
			}
		}
	}
}
