package cerberus.manager.event.mediator;


public interface IMediator
extends IMediatorReceiver
{	
	
	public boolean register( IMediatorSender sender );
	
	public boolean register( IMediatorReceiver receiver );
	
	
	/**
	 * Unregister sender. 
	 * If it is last reference to Mediator, it removes the mediator from the
	 * 
	 * @param sender
	 */
	public boolean unregister( IMediatorSender sender );
	
	public boolean unregister( IMediatorReceiver receiver );
	
	/**
	 * Called before destruction of Mediator.
	 * Only creator of teh Mediator my call this methode.
	 *
	 */
	public void destroyMediator(  final IMediatorSender sender );
	
	public void updateReceiver( Object eventTrigger );
}
