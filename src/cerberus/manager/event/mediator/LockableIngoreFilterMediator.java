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

	protected ISet refIgnoreSelectionSet;
	
	/**
	 * @param refEventPublisher
	 * @param iMediatorId
	 */
	public LockableIngoreFilterMediator(IEventPublisher refEventPublisher,
			int iMediatorId,
			final ISet setExclusiveSelectionSet) {

		super(refEventPublisher, iMediatorId, MediatorUpdateType.MEDIATOR_FILTER_ALL_EXPECT_SET);
		
		this.refIgnoreSelectionSet = setExclusiveSelectionSet;
	}
	
	public ISet getIgnoreSelectionSet() {
		return this.refIgnoreSelectionSet;
	}
	
	public void setIgnoreSelectionSet(ISet setIgnoreSelectionSet) {
		this.refIgnoreSelectionSet = setIgnoreSelectionSet;
	}
	
	/**
	 * The update is only forwareded if the ignoreSelectionSet (internal)
	 * is not equal the calling selectionSet.
	 */
	public void updateReceiverSelection(Object eventTrigger,
			ISet selectionSet) {
		
		assert selectionSet != null : "can not handle selectionSet null-pointer";
		
		if (refIgnoreSelectionSet != selectionSet) {
			super.updateReceiverSelection(eventTrigger, selectionSet);
		}		
	}

}
