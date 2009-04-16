package org.caleydo.core.manager.event;

import java.util.Collection;
import java.util.HashMap;

public class ListenerMap
	extends HashMap<Class<? extends AEvent>, Collection<IEventListener>> {

	/** version uid */
	public static final long serialVersionUID = 1L;

}
