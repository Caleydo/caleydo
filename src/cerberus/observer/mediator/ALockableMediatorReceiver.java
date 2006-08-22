/**
 * 
 */
package cerberus.observer.mediator;

import java.util.concurrent.atomic.AtomicBoolean;

import cerberus.observer.mediator.ILockableMediatorReceiver;

/**
 * Threadsafe Mediator receiver.
 * Exchange update(Object) with updateReceiver(Object)
 * in derived classes.
 * 
 * @see cerberus.observer.mediator.IMediatorReceiver#update(Object)
 * @see cerberus.observer.mediator.IMediatorReceiver#updateReceiver(Object)
 * 
 * @author kalkusch
 *
 */
public abstract class ALockableMediatorReceiver 
implements ILockableMediatorReceiver
{
	/**
	 * Thread safe boolean.
	 * TURE indicates the updates are stelled,
	 * FALSE indicates that upates are proecessed.
	 * Default is FALSE.
	 */
	protected AtomicBoolean bUpdateIsStalled;
	
	
	/**
	 * 
	 */
	protected ALockableMediatorReceiver()
	{
		bUpdateIsStalled = new AtomicBoolean( false );
	}

	/**
	 * Notification of update events.
	 * Calles updateReceiver(IMediatorSender) internal if updates are not stalled.
	 * 
	 * @see cerberus.observer.mediator.IMediatorReceiver#updateReceiver(Object)
	 * @see cerberus.observer.mediator.IMediatorReceiver#update(Object)
	 */
	public final void update(Object eventTrigger)
	{		
		if ( bUpdateIsStalled.get() ) {
			updateReceiver( eventTrigger );
		}
	}

	/* (non-Javadoc)
	 * @see cerberus.observer.mediator.IMediatorReceiver#updateStall()
	 */
	public final void updateStall()
	{
		bUpdateIsStalled.set( true );
	}

	/**
	 * Frees lock called by updateStall() and
	 * calls update() respectevly updateReceiver()
	 * 
	 * @see cerberus.observer.mediator.IMediatorReceiver#updateContinue(Object)
	 */
	public final void updateContinue(Object eventTrigger)
	{
		bUpdateIsStalled.set( false );

		update( eventTrigger );
	}
	
	/**
	 * @see cerberus.observer.mediator.IMediatorReceiver#isUpdateStalled()
	 */
	public final boolean isUpdateStalled() 
	{
		return bUpdateIsStalled.get();
	}
	
	
	/**
	 * Derived classes must implement this methode instead of update(Object).
	 * 
	 * @see cerberus.observer.mediator.IMediatorReceiver#update(Object)
	 * 	 
	 * @param eventTrigger
	 */
	public abstract void updateReceiver(Object eventTrigger);

}
