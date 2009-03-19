package org.caleydo.core.manager.event;

import org.caleydo.core.data.IUniqueObject;

/**
 * Object that shall receive an event.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public interface IMediatorReceiver {
	/**
	 * Interface for objects that are event receivers. Updates of all sorts are transmitted via this interface
	 * 
	 * @param eventTrigger
	 *            the id of the object that triggered the event
	 * @param eventContainer
	 *            the container that holds all the information on the event
	 * @param eMediatorType
	 *            the type of the mediator that was used to transmit this event. Can be null in case of a
	 *            private mediator.
	 */
	public void handleExternalEvent(IUniqueObject eventTrigger, IEventContainer eventContainer,
		EMediatorType eMediatorType);

}
