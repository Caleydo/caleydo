
package cerberus.observer.mediator;

/**
 * Object that shall receive an event.
 * 
 * @author kalkusch
 *
 */
public interface IMediatorReceiver
{

	/**
	 * Update called by Mediator triggered by IMediatorSender.
	 * 
	 * @param eventTrigger calling object, that created the update
	 */
	public void update( IMediatorSender eventTrigger );
	
	/**
	 * Blocks update() until methode updateRunPipe() is called.
	 * 
	 * @see cerberus.observer.mediator.IMediatorReceiver#update(IMediatorSender)
	 * @see cerberus.observer.mediator.IMediatorReceiver#updateContinue(IMediatorSender)
	 * 
	 * @param eventTrigger calling object, that created the update
	 */
	public void updateStall( );
	
	/**
	 * Test, if currently updates are stalled.
	 * 
	 * @see cerberus.observer.mediator.IMediatorReceiver#updateStall()
	 * @see cerberus.observer.mediator.IMediatorReceiver#updateContinue(IMediatorSender)
	 * 	
	 * @return TRUE if upate() methodes are stalled, fales if update() methodes are preocessed
	 */
	public boolean isUpdateStalled();
	
	/**
	 * calls update() and frees lock from updateStall().
	 * 
	 * @see cerberus.observer.mediator.IMediatorReceiver#update(IMediatorSender)
	 * @see cerberus.observer.mediator.IMediatorReceiver#updateStall()
 	 * 
	 * @param eventTrigger calling object, that created the update
	 */
	public void updateContinue( IMediatorSender eventTrigger );
	
}
