package org.caleydo.core.manager.event.mediator;

import org.caleydo.core.data.IUniqueObject;

/**
 * Object that shall receive an event.
 * 
 * @author Michael Kalkusch
 */
public interface ILockableMediatorReceiver
	extends IMediatorReceiver
{

	/**
	 * Blocks update() until method updateRunPipe() is called.
	 * 
	 * @see org.caleydo.core.manager.event.mediator.ILockableMediatorReceiver#handleUpdate(IUniqueObject)
	 * @see org.caleydo.core.manager.event.mediator.ILockableMediatorReceiver#updateContinue(Object)
	 * @param eventTrigger calling object, that created the update
	 */
	public void updateStall();

	/**
	 * Test, if currently updates are stalled.
	 * 
	 * @see org.caleydo.core.manager.event.mediator.ILockableMediatorReceiver#updateStall()
	 * @see org.caleydo.core.manager.event.mediator.ILockableMediatorReceiver#updateContinue(Object)
	 * @return TRUE if upate() methods are stalled, fales if update() methods
	 *         are preocessed
	 */
	public boolean isUpdateStalled();

	/**
	 * calls update() and frees lock from updateStall().
	 * 
	 * @see org.caleydo.core.manager.event.mediator.ILockableMediatorReceiver#handleUpdate(IUniqueObject)
	 * @see org.caleydo.core.manager.event.mediator.ILockableMediatorReceiver#updateStall()
	 * @param eventTrigger calling object, that created the update
	 */
	public void updateContinue(Object eventTrigger);

}
