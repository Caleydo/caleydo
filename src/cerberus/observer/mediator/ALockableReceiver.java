/**
 * 
 */
package cerberus.observer.mediator;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Threadsafe Mediator receiver.
 * Exchange update(IMediatorSender) with updateReceiver(IMediatorSender)
 * in derived classes.
 * 
 * @see cerberus.observer.mediator.IMediatorReceiver#update(IMediatorSender)
 * @see cerberus.observer.mediator.IMediatorReceiver#updateReceiver(IMediatorSender)
 * 
 * @author kalkusch
 *
 */
public abstract class ALockableReceiver 
implements IMediatorReceiver
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
	protected ALockableReceiver()
	{
		bUpdateIsStalled = new AtomicBoolean( false );
	}

	/**
	 * Notification of update events.
	 * Calles updateReceiver(IMediatorSender) internal if updates are not stalled.
	 * 
	 * @see cerberus.observer.mediator.IMediatorReceiver#updateReceiver(IMediatorSender)
	 * @see cerberus.observer.mediator.IMediatorReceiver#update(cerberus.observer.mediator.IMediatorSender)
	 */
	public final void update(IMediatorSender eventTrigger)
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
	 * @see cerberus.observer.mediator.IMediatorReceiver#updateContinue(cerberus.observer.mediator.IMediatorSender)
	 */
	public final void updateContinue(IMediatorSender eventTrigger)
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
	 * Derived classes must implement this methode instead of update(IMediatorSender).
	 * 
	 * @see cerberus.observer.mediator.IMediatorReceiver#update(IMediatorSender)
	 * 	 
	 * @param eventTrigger
	 */
	public abstract void updateReceiver(IMediatorSender eventTrigger);

}
