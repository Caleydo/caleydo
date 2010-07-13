package org.caleydo.core.manager.event;

import java.util.Collection;
import java.util.HashMap;

/**
 * Map used within {@link EventPublisher}s that maps {@link AEvent}s to a hash map, that maps the
 * DataDomainType string to concrete {@link AEventListener}s registered to the event. For listeners not using
 * a dataDomainType string, the null key for the second hash map is used instead of a string.
 * 
 * @author Werner Puff
 * @author Alexander Lex
 */
public class ListenerMap
	extends HashMap<Class<? extends AEvent>, HashMap<String, Collection<AEventListener<?>>>> {

	/** version uid */
	public static final long serialVersionUID = 1L;

}
