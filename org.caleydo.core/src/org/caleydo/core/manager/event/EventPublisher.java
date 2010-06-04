package org.caleydo.core.manager.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

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
	public synchronized void addListener(Class<? extends AEvent> eventClass, AEventListener<?> listener) {
		listener.checkIntegrity();
		HashMap<String, Collection<AEventListener<?>>> allListeners = listenerMap.get(eventClass);
		if (allListeners == null) {
			allListeners = new HashMap<String, Collection<AEventListener<?>>>();
			listenerMap.put(eventClass, allListeners);
		}
		Collection<AEventListener<?>> domainSpecificListeners =
			allListeners.get(listener.getDataDomainType());
		if (domainSpecificListeners == null) {
			domainSpecificListeners = new ArrayList<AEventListener<?>>();
			allListeners.put(listener.getDataDomainType(), domainSpecificListeners);
		}

		domainSpecificListeners.add(listener);
	}

	@Override
	public synchronized void removeListener(Class<? extends AEvent> eventClass, AEventListener<?> listener) {
		Collection<AEventListener<?>> listeners =
			listenerMap.get(eventClass).get(listener.getDataDomainType());
		listeners.remove(listener);
	}

	@Override
	public synchronized void removeListener(AEventListener<?> listener) {
		for (HashMap<String, Collection<AEventListener<?>>> allListeners : listenerMap.values()) {
			allListeners.get(listener.getDataDomainType()).remove(listener);
		}
	}

	@Override
	public synchronized void triggerEvent(AEvent event) {
		if (!event.checkIntegrity()) {
			throw new IllegalStateException("Event " + event + " has failed integrity check");
		}
		String dataDomainType = event.getDataDomainType();
		HashMap<String, Collection<AEventListener<?>>> dataDomainToListenersMap =
			listenerMap.get(event.getClass());
		if (dataDomainToListenersMap == null)
			return;
		if (dataDomainType != null)
			triggerEvents(event, dataDomainToListenersMap.get(null));

		triggerEvents(event, dataDomainToListenersMap.get(dataDomainType));
		// Collection<AEventListener<?>> listeners = listenerMap.get(event.getClass()).get();

	}

	public ListenerMap getListenerMap() {
		return listenerMap;
	}

	private void triggerEvents(AEvent event, Collection<AEventListener<?>> listeners) {
		if (listeners != null) {
			for (AEventListener<?> receiver : listeners) {
				receiver.queueEvent(event);
			}
		}
	}

}
