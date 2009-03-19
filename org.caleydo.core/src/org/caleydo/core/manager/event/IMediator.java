package org.caleydo.core.manager.event;

import org.caleydo.core.data.IUniqueObject;

/**
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Alexander Lex
 */
public interface IMediator
	extends IUniqueObject {

	/**
	 * Get the type of the mediator. If mediator is used as a private mediator this method returns null
	 * 
	 * @return the type of the mediator, of null if private
	 */
	public EMediatorType getType();

	/**
	 * Register a new event sender to the mediator.
	 * 
	 * @param sender
	 *            new event receiver
	 * @return true, when the instance was added, false when it was already registered
	 */
	public boolean addSender(IMediatorSender sender);

	/**
	 * Register a new event receiver to the mediator.
	 * 
	 * @param receiver
	 *            new event receiver
	 * @return true, when the instance was added, false when it was already registered
	 */
	public boolean addReceiver(IMediatorReceiver receiver);

	/**
	 * Remove sender from mediator
	 * 
	 * @param sender
	 *            the sender to be removed
	 * @return true if the mediator contained the instance
	 */
	public boolean removeSender(IMediatorSender sender);

	/**
	 * Remove receiver from mediator
	 * 
	 * @param receiver
	 *            the receiver to be removed
	 * @return true if the mediator contained the instance
	 */
	public boolean removeReceiver(IMediatorReceiver receiver);

	/**
	 * Checks whether the instance is registered as a receiver
	 * 
	 * @param receiver
	 *            the instance to be checked
	 * @return true if already registered
	 */
	public boolean hasReceiver(IMediatorReceiver receiver);

	/**
	 * Checks whether the instance is registered as a sender
	 * 
	 * @param sender
	 *            the instance to be checked
	 * @return true if already registered
	 */
	public boolean hasSender(IMediatorSender sender);

	/**
	 * Triggers an event, signals that something has happened and sends data along
	 * 
	 * @param eMediatorType
	 *            the type of mediator that is used to process this event
	 * @param eventTrigger
	 *            the caller
	 * @param eventContainer
	 *            containing the information on the type of the event {@link EEventType} and possibly data
	 *            associated
	 */
	public void triggerEvent(IUniqueObject eventTrigger, IEventContainer eventContainer);
}
