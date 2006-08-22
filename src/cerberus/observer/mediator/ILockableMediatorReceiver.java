
package cerberus.observer.mediator;

import cerberus.observer.mediator.IMediatorReceiver;

/**
 * Object that shall receive an event.
 * 
 * @author kalkusch
 *
 */
public interface ILockableMediatorReceiver
extends IMediatorReceiver
{

	/**
	 * Update called by Mediator triggered by IMediatorSender.
	 * 
	 * @param eventTrigger calling object, that created the update
	 */
	public void update( Object eventTrigger );
	
	/**
	 * Blocks update() until methode updateRunPipe() is called.
	 * 
	 * @see cerberus.observer.mediator.ILockableMediatorReceiver#update(Object)
	 * @see cerberus.observer.mediator.ILockableMediatorReceiver#updateContinue(Object)
	 * 
	 * @param eventTrigger calling object, that created the update
	 */
	public void updateStall( );
	
	/**
	 * Test, if currently updates are stalled.
	 * 
	 * @see cerberus.observer.mediator.ILockableMediatorReceiver#updateStall()
	 * @see cerberus.observer.mediator.ILockableMediatorReceiver#updateContinue(Object)
	 * 	
	 * @return TRUE if upate() methodes are stalled, fales if update() methodes are preocessed
	 */
	public boolean isUpdateStalled();
	
	/**
	 * calls update() and frees lock from updateStall().
	 * 
	 * @see cerberus.observer.mediator.ILockableMediatorReceiver#update(Object)
	 * @see cerberus.observer.mediator.ILockableMediatorReceiver#updateStall()
 	 * 
	 * @param eventTrigger calling object, that created the update
	 */
	public void updateContinue( Object eventTrigger );
	
}
