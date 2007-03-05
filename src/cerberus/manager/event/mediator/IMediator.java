package cerberus.manager.event.mediator;

import cerberus.manager.event.mediator.IMediatorReceiver;

/**
 * 
 * @see
 * 
 * @author Michael Kalkusch
 * @author MArc Streit
 *
 */
public interface IMediator
extends IMediatorReceiver {	
	
	/**
	 * Register a new event sender to the mediator.
	 * 
	 * @param sender new event sender
	 * @return TRUE on success
	 */
	public boolean register( IMediatorSender sender );
	
	/**
	 * Register a new event receiver to the mediator.
	 * 
	 * @param receiver new event receiver
	 * @return TRUE on success
	 */
	public boolean register( IMediatorReceiver receiver );
	
	
	/**
	 * Unregister sender. 
	 * If it is last reference to Mediator, it removes the mediator from the
	 * 
	 * @param sender
	 */
	public boolean unregister( IMediatorSender sender );
	
	public boolean unregister( IMediatorReceiver receiver );
	
	public boolean hasReceiver( IMediatorReceiver receiver );
	
	public boolean hasSender( IMediatorSender sender );
	
	/**
	 * Called before destruction of Mediator.
	 * Only creator of teh Mediator my call this methode.
	 *
	 */
	public void destroyMediator(  final IMediatorSender sender );
	
}
