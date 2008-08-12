/**
 * 
 */
package org.caleydo.core.manager.event.mediator;

import java.util.concurrent.atomic.AtomicBoolean;
import org.caleydo.core.data.AUniqueObject;
import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.data.selection.ISelectionDelta;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;

/**
 * Threadsafe Mediator receiver. Exchange update(Object) with
 * updateReceiver(Object) in derived classes.
 * 
 * @see org.caleydo.core.manager.event.mediator.IMediatorReceiver#handleUpdate(IUniqueObject)
 * @author Michael Kalkusch
 */
public abstract class ALockableMediatorReceiver
	extends AUniqueObject
	implements ILockableMediatorReceiver, IUniqueObject
{

	/**
	 * Thread safe boolean. TURE indicates the updates are stalled, FALSE
	 * indicates that updates are proecessed. Default is FALSE.
	 */
	protected AtomicBoolean bUpdateIsStalled;

	/**
	 * Constructor.
	 */
	protected ALockableMediatorReceiver()
	{
		super(GeneralManager.get().getIDManager().createID(EManagedObjectType.EVENT_MEDIATOR));
		
		bUpdateIsStalled = new AtomicBoolean(false);
	}

	/**
	 * @see org.caleydo.core.manager.event.mediator.IMediatorReceiver#hanleUpdate(IUniqueObject,
	 *      org.caleydo.core.data.collection.ISet)
	 */
	public final void hanleUpdate(IUniqueObject eventTrigger, ISelectionDelta selectionDelta)
	{

		if (!bUpdateIsStalled.get())
		{
			updateReceiverSpecialMediator(eventTrigger, selectionDelta);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.observer.mediator.IMediatorReceiver#updateStall()
	 */
	public synchronized final void updateStall()
	{

		bUpdateIsStalled.set(true);
	}

	/**
	 * Frees lock called by updateStall()
	 */
	public synchronized final void updateContinue()
	{

		bUpdateIsStalled.set(false);
	}

	/**
	 * Frees lock called by updateStall() and calls update() respectevly
	 * updateReceiver()
	 * 
	 * @see org.caleydo.core.manager.event.mediator.IMediatorReceiver#updateContinue(Object)
	 */
	public final void updateContinue(IUniqueObject eventTrigger)
	{

		bUpdateIsStalled.set(false);

		handleUpdate(eventTrigger);
	}

	/**
	 * @see org.caleydo.core.manager.event.mediator.IMediatorReceiver#isUpdateStalled()
	 */
	public final boolean isUpdateStalled()
	{

		return bUpdateIsStalled.get();
	}

	/**
	 * Derived classes must implement this method instead of update(Object).
	 * 
	 * @see org.caleydo.core.manager.event.mediator.IMediatorReceiver#handleUpdate(IUniqueObject)
	 * @param eventTrigger
	 */
	public abstract void handleUpdate(IUniqueObject eventTrigger);

	/**
	 * Called by org.caleydo.core.manager.event.mediator.IMediatorReceiver#
	 * updateReceiver(java.lang.Object, org.caleydo.core.data.collection.ISet)
	 * inside this abstract class.
	 * 
	 * @param eventTrigger
	 * @param updateSet
	 * @see org.caleydo.core.manager.event.mediator.IMediatorReceiver#hanleUpdate(IUniqueObject,
	 *      org.caleydo.core.data.collection.ISet)
	 */
	public abstract void updateReceiverSpecialMediator(IUniqueObject eventTrigger,
			ISelectionDelta selectionDelta);

}
