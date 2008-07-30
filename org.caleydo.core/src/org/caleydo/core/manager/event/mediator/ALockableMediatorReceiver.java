/**
 * 
 */
package org.caleydo.core.manager.event.mediator;

import java.util.concurrent.atomic.AtomicBoolean;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.selection.ISelection;

/**
 * Threadsafe Mediator receiver. Exchange update(Object) with
 * updateReceiver(Object) in derived classes.
 * 
 * @see org.caleydo.core.manager.event.mediator.IMediatorReceiver#updateReceiver(Object)
 * @author Michael Kalkusch
 */
public abstract class ALockableMediatorReceiver
	implements ILockableMediatorReceiver
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

		bUpdateIsStalled = new AtomicBoolean(false);
	}

	/**
	 * @see org.caleydo.core.manager.event.mediator.IMediatorReceiver#updateReceiver(java.lang.Object,
	 *      org.caleydo.core.data.collection.ISet)
	 */
	public final void updateReceiver(Object eventTrigger, ISelection updatedSelection)
	{

		if (!bUpdateIsStalled.get())
		{
			updateReceiverSpecialMediator(eventTrigger, updatedSelection);
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
	public final void updateContinue(Object eventTrigger)
	{

		bUpdateIsStalled.set(false);

		updateReceiver(eventTrigger);
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
	 * @see org.caleydo.core.manager.event.mediator.IMediatorReceiver#updateReceiver(Object)
	 * @param eventTrigger
	 */
	public abstract void updateReceiver(Object eventTrigger);

	/**
	 * Called by org.caleydo.core.manager.event.mediator.IMediatorReceiver#
	 * updateReceiver(java.lang.Object, org.caleydo.core.data.collection.ISet)
	 * inside this abstract class.
	 * 
	 * @param eventTrigger
	 * @param updateSet
	 * @see org.caleydo.core.manager.event.mediator.IMediatorReceiver#updateReceiver(java.lang.Object,
	 *      org.caleydo.core.data.collection.ISet)
	 */
	public abstract void updateReceiverSpecialMediator(Object eventTrigger,
			ISelection updatedSelection);

}
