/**
 * 
 */
package org.geneview.core.manager.event.mediator;

import java.util.concurrent.atomic.AtomicBoolean;

import org.geneview.core.data.collection.ISet;

/**
 * Threadsafe Mediator receiver.
 * Exchange update(Object) with updateReceiver(Object)
 * in derived classes.
 * 
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
	 * 
	 * @see cerberus.manager.event.mediator.IMediatorReceiver#updateReceiver(java.lang.Object, cerberus.data.collection.ISet)
	 */
	public final void updateReceiver(Object eventTrigger, 
			ISet updatedSet) {
		
		if ( ! bUpdateIsStalled.get() ) {
			updateReceiverSpecialMediator(eventTrigger, updatedSet);
		}
	}
	
	
	/* (non-Javadoc)
	 * @see cerberus.observer.mediator.IMediatorReceiver#updateStall()
	 */
	public synchronized final void updateStall() {
		
		bUpdateIsStalled.set( true );
	}
	
	/**
	 * Frees lock called by updateStall()
	 *
	 */
	public synchronized final void updateContinue() {
		
		bUpdateIsStalled.set( false );
	}

	/**
	 * Frees lock called by updateStall() and
	 * calls update() respectevly updateReceiver()
	 * 
	 * @see cerberus.manager.event.mediator.IMediatorReceiver#updateContinue(Object)
	 */
	public final void updateContinue(Object eventTrigger) {
		
		bUpdateIsStalled.set( false );

		updateReceiver( eventTrigger );
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
	 * @see cerberus.manager.event.mediator.IMediatorReceiver#updateReceiver(Object)
	 * 	 
	 * @param eventTrigger
	 */
	public abstract void updateReceiver(Object eventTrigger);
	
	/**
	 * Called by cerberus.manager.event.mediator.IMediatorReceiver#updateReceiver(java.lang.Object, cerberus.data.collection.ISet) 
	 * inside this abstract class.
	 *  
	 * @param eventTrigger
	 * @param updateSet
	 * 
	 * @see cerberus.manager.event.mediator.IMediatorReceiver#updateReceiver(java.lang.Object, cerberus.data.collection.ISet)
	 */
	public abstract void updateReceiverSpecialMediator(Object eventTrigger,
			ISet updateSet);

}
