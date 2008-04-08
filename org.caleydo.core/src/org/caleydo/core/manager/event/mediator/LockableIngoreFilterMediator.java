/**
 * 
 */
package org.caleydo.core.manager.event.mediator;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.event.mediator.MediatorUpdateType;

/**
 * Ignore one special SelectionSet and let all other SelectionSet's pass when calling
 * org.caleydo.core.manager.event.mediator.IMediator#updateReceiverSelection(Object, ISet)
 * 
 * @see org.caleydo.core.manager.event.mediator.IMediator#updateReceiverSelection(Object, ISet)
 * 
 * @author Michael Kalkusch
 *
 */
public class LockableIngoreFilterMediator 
extends LockableMediator {

	protected ISet refIgnoreSet;
	
	/**
	 * @param refEventPublisher
	 * @param iMediatorId
	 */
	public LockableIngoreFilterMediator(IEventPublisher refEventPublisher,
			int iMediatorId,
			final ISet setExclusiveSet) {

		super(refEventPublisher, iMediatorId, MediatorUpdateType.MEDIATOR_FILTER_ALL_EXPECT_SET);
		
		this.refIgnoreSet = setExclusiveSet;
	}
	
	public ISet getIgnoreSet() {
		return this.refIgnoreSet;
	}
	
	public void setIgnoreSet(ISet setIgnoreSet) {
		this.refIgnoreSet = setIgnoreSet;
	}
	

	/**
	 * The update is only forwareded if the ignoreSelectionSet (internal)
	 * is not equal the calling selectionSet.
	 * 	 
	 * @see org.caleydo.core.manager.event.mediator.LockableMediator#updateReceiverSpecialMediator(java.lang.Object, org.caleydo.core.data.collection.ISet)
	 */
	public final void updateReceiverSpecialMediator(Object eventTrigger,
			ISet updatedSet) {
		
		assert updatedSet != null : "can not handle selectionSet null-pointer";
		
		if (refIgnoreSet != updatedSet) {
			super.updateReceiver(eventTrigger, updatedSet);
		}		
	}

}
