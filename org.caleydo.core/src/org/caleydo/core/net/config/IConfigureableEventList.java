package org.caleydo.core.net.config;

import java.util.Collection;

import org.caleydo.core.manager.event.AEvent;

/**
 * <p>
 * Interface for access to configureable event lists.
 * </p>
 * <p>
 * Used for example for configuring which events should be transmitted over the network.
 * </p>
 * 
 * @author Werner Puff
 */
public interface IConfigureableEventList {

	public Collection<Class<? extends AEvent>> getAllEventTypes();

	public Collection<Class<? extends AEvent>> getSelectedEventTypes();

}
