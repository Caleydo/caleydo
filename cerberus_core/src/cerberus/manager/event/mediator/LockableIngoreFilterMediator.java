/**
 * 
 */
package cerberus.manager.event.mediator;

import cerberus.data.collection.ISet;
import cerberus.manager.IEventPublisher;
import cerberus.manager.event.mediator.MediatorUpdateType;

/**
 * Ignore one special SelectionSet and let all other SelectionSet's pass when calling
 * cerberus.manager.event.mediator.IMediator#updateReceiverSelection(Object, ISet)
 * 
 * @see cerberus.manager.event.mediator.IMediator#updateReceiverSelection(Object, ISet)
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
	 * @see cerberus.manager.event.mediator.LockableMediator#updateReceiverSpecialMediator(java.lang.Object, cerberus.data.collection.ISet)
	 */
	public final void updateReceiverSpecialMediator(Object eventTrigger,
			ISet updatedSet) {
		
		assert updatedSet != null : "can not handle selectionSet null-pointer";
		
		if (refIgnoreSet != updatedSet) {
			super.updateReceiver(eventTrigger, updatedSet);
		}		
	}

}
