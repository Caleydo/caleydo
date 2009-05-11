package org.caleydo.core.manager;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;

/**
 * EventPublishers are the central event distributing entities. {@link IEventListener}s with their related
 * {@link AEvent}s are registered to instances of this class. When an event is triggered, the handleEvent()
 * method to registered listeners are invoked.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 * @author Werner Puff
 */
public interface IEventPublisher {

	/**
	 * adds a receiver to the list of event handlers
	 * 
	 * @param eventClass
	 *            event type to register the handler to
	 * @param listener
	 *            IMediatorReceiver to handle events
	 */
	public void addListener(Class<? extends AEvent> eventClass, AEventListener<?> listener);

	/**
	 * removes a contained receiver from the list of event handlers
	 * 
	 * @param eventClass
	 *            event type to remove the handler from
	 * @param listener
	 *            IMediatorReceiver to handle events
	 */
	public void removeListener(Class<? extends AEvent> eventClass, AEventListener<?> listener);

	/**
	 * removes a listener from all events in this event-publisher
	 * 
	 * @param listener
	 *            listener to remove
	 */
	public void removeListener(AEventListener<?> listener);

	/**
	 * Central event handling and distribution method. The prohibition of sending events back to its sender is
	 * done within {@link AEventListener}. Furthermore an integrity check is performed.
	 * 
	 * @param event
	 *            event to distribute to the listeners
	 */
	public void triggerEvent(AEvent event);

}
