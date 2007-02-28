/**
 * 
 */
package cerberus.manager.event.mediator;

import cerberus.data.collection.ISet;
import cerberus.manager.IEventPublisher;
import cerberus.manager.event.mediator.MediatorUpdateType;

/**
 * Exclusiv accept one SelectionSet and blockes all other SelectionSet's when calling
 * cerberus.manager.event.mediator.IMediator#updateReceiverSelection(Object, ISet).
 * 
 * @see cerberus.manager.event.mediator.IMediator#updateReceiverSelection(Object, ISet)
 * 
 * @author Michael Kalkusch
 *
 */
public class LockableExclusivFilterMediator 
extends LockableMediator {

	protected ISet refExclusiveSelectionSet;
	
	/**
	 * @param refEventPublisher
	 * @param iMediatorId
	 */
	public LockableExclusivFilterMediator(IEventPublisher refEventPublisher,
			int iMediatorId,
			final ISet setExclusiveSelectionSet) {

		super(refEventPublisher, iMediatorId, MediatorUpdateType.MEDIATOR_FILTER_ONLY_SET);
		
		this.refExclusiveSelectionSet = setExclusiveSelectionSet;
	}
	
	public ISet getExclusiveSelectionSet() {
		return this.refExclusiveSelectionSet;
	}
	
	public void setExclusiveSelectionSet(ISet setExclusiveSelectionSet) {
		this.refExclusiveSelectionSet = setExclusiveSelectionSet;
	}
	
	/**
	 * The update is only forwareded if the exclusiveSelectionSet (internal)
	 * is equal tio the calling selectionSet.
	 */
	public void updateReceiverSelection(Object eventTrigger,
			ISet selectionSet) {
		
		assert selectionSet != null : "can not handle selectionSet null-pointer";
		
		if (refExclusiveSelectionSet == selectionSet) {
			super.updateReceiverSelection(eventTrigger, selectionSet);
		}		
	}

}
