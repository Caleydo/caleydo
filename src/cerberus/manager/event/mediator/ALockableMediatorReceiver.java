/**
 * 
 */
package cerberus.manager.event.mediator;

import java.util.concurrent.atomic.AtomicBoolean;

import cerberus.data.collection.selection.SetSelection;

/**
 * Threadsafe Mediator receiver.
 * Exchange update(Object) with updateReceiver(Object)
 * in derived classes.
 * 
 * @see cerberus.manager.event.mediator.IMediatorReceiver#update(Object)
 * @see cerberus.manager.event.mediator.IMediatorReceiver#updateReceiver(Object)
 * 
 * @author Michael Kalkusch
 *
 */
public abstract class ALockableMediatorReceiver 
implements ILockableMediatorReceiver {
	
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
	protected ALockableMediatorReceiver() {
		
		bUpdateIsStalled = new AtomicBoolean( false );
	}

	/**
	 * Notification of update events.
	 * Calles updateReceiver(IMediatorSender) internal if updates are not stalled.
	 * 
	 * @see cerberus.manager.event.mediator.IMediatorReceiver#updateReceiver(Object)
	 * @see cerberus.manager.event.mediator.IMediatorReceiver#update(Object)
	 */
	public final void update(Object eventTrigger)
	{		
		if ( bUpdateIsStalled.get() ) {
			updateReceiver( eventTrigger );
		}
	}

	public final void updateSelection(Object eventTrigger, 
			SetSelection selectionSet) {
		
		if ( bUpdateIsStalled.get() ) {
			updateReceiverSelection(eventTrigger, selectionSet);
		}
	}
	
	
	/* (non-Javadoc)
	 * @see cerberus.observer.mediator.IMediatorReceiver#updateStall()
	 */
	public synchronized final void updateStall() {
		
		bUpdateIsStalled.set( true );
	}

	/**
	 * Frees lock called by updateStall() and
	 * calls update() respectevly updateReceiver()
	 * 
	 * @see cerberus.manager.event.mediator.IMediatorReceiver#updateContinue(Object)
	 */
	public final void updateContinue(Object eventTrigger) {
		
		bUpdateIsStalled.set( false );

		update( eventTrigger );
	}
	
	/**
	 * @see cerberus.manager.event.mediator.IMediatorReceiver#isUpdateStalled()
	 */
	public final boolean isUpdateStalled() {
		
		return bUpdateIsStalled.get();
	}
	
	
	/**
	 * Derived classes must implement this method instead of update(Object).
	 * 
	 * @see cerberus.manager.event.mediator.IMediatorReceiver#update(Object)
	 * 	 
	 * @param eventTrigger
	 */
	public abstract void updateReceiver(Object eventTrigger);
	

	public abstract void updateReceiverSelection(Object eventTrigger,
			SetSelection selectionSet);

}
