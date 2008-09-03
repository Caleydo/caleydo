package org.caleydo.core.manager.event.mediator;

import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.manager.IEventPublisher;

/**
 * @see
 * @author Michael Kalkusch
 * @author MArc Streit
 */
public interface IMediator
	extends IMediatorReceiver, IUniqueObject
{

	/**
	 * Register a new event sender to the mediator.
	 * 
	 * @param sender new event sender
	 * @return TRUE on success
	 */
	public boolean register(IMediatorSender sender);

	/**
	 * Register a new event receiver to the mediator.
	 * 
	 * @param receiver new event receiver
	 * @return TRUE on success
	 */
	public boolean register(IMediatorReceiver receiver);

	/**
	 * Unregister sender. If it is last reference to Mediator, it removes the
	 * mediator from the
	 * 
	 * @param sender
	 */
	public boolean unregister(IMediatorSender sender);

	public boolean unregister(IMediatorReceiver receiver);

	public boolean hasReceiver(IMediatorReceiver receiver);

	public boolean hasSender(IMediatorSender sender);
	
	public EMediatorType getType();
	
	/**
	 * Called before destruction of Mediator. Only creator of the Mediator my
	 * call this method.
	 */
	public void destroyMediator(final IEventPublisher sender);

}
