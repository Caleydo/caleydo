package org.caleydo.core.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * EventPublishers are the central event distributing entities. {@link IEventListener}s with their related
 * {@link AEvent}s are registered to instances of this class. When an event is triggered, the handleEvent()
 * method to registered listeners are invoked.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 * @author Werner Puff
 */
public class EventPublisher {

	/** map of events (=key) to the listeners (=value, a collection of listeners) registered to it */
	private ListenerMap listenerMap;

	/**
	 * Constructor.
	 */
	public EventPublisher() {
		listenerMap = new ListenerMap();
	}

	/**
	 * adds a receiver to the list of event handlers
	 * 
	 * @param eventClass
	 *            event type to register the handler to
	 * @param listener
	 *            IMediatorReceiver to handle events
	 */
	public synchronized void addListener(Class<? extends AEvent> eventClass, AEventListener<?> listener) {
		listener.checkIntegrity();
		HashMap<String, Collection<AEventListener<?>>> allListeners = listenerMap.get(eventClass);
		if (allListeners == null) {
			allListeners = new HashMap<String, Collection<AEventListener<?>>>();
			listenerMap.put(eventClass, allListeners);
		}
		Collection<AEventListener<?>> domainSpecificListeners = allListeners.get(listener.getDataDomainID());
		if (domainSpecificListeners == null) {
			domainSpecificListeners = new ArrayList<AEventListener<?>>();
			allListeners.put(listener.getDataDomainID(), domainSpecificListeners);
		}

		domainSpecificListeners.add(listener);
	}

	/**
	 * removes a contained receiver from the list of event handlers
	 * 
	 * @param eventClass
	 *            event type to remove the handler from
	 * @param listener
	 *            IMediatorReceiver to handle events
	 */
	public synchronized void removeListener(Class<? extends AEvent> eventClass, AEventListener<?> listener) {
		Collection<AEventListener<?>> listeners = listenerMap.get(eventClass).get(listener.getDataDomainID());
		listeners.remove(listener);
	}

	/**
	 * removes a listener from all events in this event-publisher
	 * 
	 * @param listener
	 *            listener to remove
	 */
	public synchronized void removeListener(AEventListener<?> listener) {
		for (HashMap<String, Collection<AEventListener<?>>> allListeners : listenerMap.values()) {

			Collection<AEventListener<?>> listeners = allListeners.get(listener.getDataDomainID());
			if (listeners == null)
				continue;
			listeners.remove(listener);
		}
	}

	/**
	 * Central event handling and distribution method. The prohibition of sending events back to its sender is
	 * done within {@link AEventListener}. Furthermore an integrity check is performed.
	 * 
	 * @param event
	 *            event to distribute to the listeners
	 */
	public synchronized void triggerEvent(AEvent event) {
		if (!event.checkIntegrity()) {
			throw new IllegalStateException("Event " + event + " has failed integrity check");
		}
		String dataDomainID = event.getDataDomainID();
		HashMap<String, Collection<AEventListener<?>>> dataDomainToListenersMap =
			listenerMap.get(event.getClass());
		if (dataDomainToListenersMap == null)
			return;
		// we also want to notify those listeners that did not register for a dataDomain
		triggerEvents(event, dataDomainToListenersMap.get(null));
		// if the data domain is specified in the event we call those listeners now
		if (dataDomainID != null)
			triggerEvents(event, dataDomainToListenersMap.get(dataDomainID));
		// Collection<AEventListener<?>> listeners = listenerMap.get(event.getClass()).get();

	}

	public ListenerMap getListenerMap() {
		return listenerMap;
	}

	private void triggerEvents(AEvent event, Collection<AEventListener<?>> listeners) {
		if (listeners != null) {
			for (AEventListener<?> receiver : listeners) {
				// check if a receiver wants events that are not if his data domain
				if (event.getDataDomainID() == null && receiver.isExclusiveDataDomain()) {
				}
				else
					receiver.queueEvent(event);
			}
		}
	}
}
