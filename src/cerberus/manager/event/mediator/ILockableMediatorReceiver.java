
package cerberus.manager.event.mediator;

import cerberus.data.collection.ISet;


/**
 * Object that shall receive an event.
 * 
 * @author kalkusch
 *
 */
public interface ILockableMediatorReceiver
extends IMediatorReceiver {

	/**
	 * Update called by Mediator triggered by IMediatorSender.
	 * 
	 * @param eventTrigger calling object, that created the update
	 */
	public void update( Object eventTrigger );
	
	public void updateSelection(Object eventTrigger, ISet selectionSet);
	
	/**
	 * Blocks update() until methode updateRunPipe() is called.
	 * 
	 * @see cerberus.manager.event.mediator.ILockableMediatorReceiver#update(Object)
	 * @see cerberus.manager.event.mediator.ILockableMediatorReceiver#updateContinue(Object)
	 * 
	 * @param eventTrigger calling object, that created the update
	 */
	public void updateStall( );
	
	/**
	 * Test, if currently updates are stalled.
	 * 
	 * @see cerberus.manager.event.mediator.ILockableMediatorReceiver#updateStall()
	 * @see cerberus.manager.event.mediator.ILockableMediatorReceiver#updateContinue(Object)
	 * 	
	 * @return TRUE if upate() methodes are stalled, fales if update() methodes are preocessed
	 */
	public boolean isUpdateStalled();
	
	/**
	 * calls update() and frees lock from updateStall().
	 * 
	 * @see cerberus.manager.event.mediator.ILockableMediatorReceiver#update(Object)
	 * @see cerberus.manager.event.mediator.ILockableMediatorReceiver#updateStall()
 	 * 
	 * @param eventTrigger calling object, that created the update
	 */
	public void updateContinue( Object eventTrigger );
	
}
