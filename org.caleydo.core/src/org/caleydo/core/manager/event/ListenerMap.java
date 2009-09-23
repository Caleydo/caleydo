package org.caleydo.core.manager.event;

import java.util.Collection;
import java.util.HashMap;

/**
 * Map used within {@link EventPublisher}s that maps {@link AEvent}s to the {@link AEventListener}s registered
 * to the event.
 * 
 * @author Werner Puff
 */
public class ListenerMap
	extends HashMap<Class<? extends AEvent>, Collection<AEventListener<?>>> {

	/** version uid */
	public static final long serialVersionUID = 1L;

}
