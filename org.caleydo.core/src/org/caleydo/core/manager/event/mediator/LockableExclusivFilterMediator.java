/**
 * 
 */
package org.geneview.core.manager.event.mediator;

import org.geneview.core.data.collection.ISet;
import org.geneview.core.manager.IEventPublisher;
import org.geneview.core.manager.event.mediator.MediatorUpdateType;

/**
 * Exclusiv accept one SelectionSet and blockes all other SelectionSet's when calling
 * org.geneview.core.manager.event.mediator.IMediator#updateReceiverSelection(Object, ISet).
 * 
 * @see org.geneview.core.manager.event.mediator.IMediator#updateReceiverSelection(Object, ISet)
 * 
 * @author Michael Kalkusch
 *
 */
public class LockableExclusivFilterMediator 
extends LockableMediator {

	protected ISet refExclusiveSet;
	
	/**
	 * @param refEventPublisher
	 * @param iMediatorId
	 */
	public LockableExclusivFilterMediator(IEventPublisher refEventPublisher,
			int iMediatorId,
			final ISet setExclusiveSelectionSet) {

		super(refEventPublisher, iMediatorId, MediatorUpdateType.MEDIATOR_FILTER_ONLY_SET);
		
		this.refExclusiveSet = setExclusiveSelectionSet;
	}
	
	public ISet getExclusiveSet() {
		return this.refExclusiveSet;
	}
	
	public void setExclusiveSet(ISet setExclusiveSelectionSet) {
		this.refExclusiveSet = setExclusiveSelectionSet;
	}
	
	/**
	 * The update is only forwareded if the exclusiveSelectionSet (internal)
	 * is equal tio the calling selectionSet.
	 * 
	 * @see org.geneview.core.manager.event.mediator.LockableMediator#updateReceiverSpecialMediator(java.lang.Object, org.geneview.core.data.collection.ISet)
	 */
	public final void updateReceiverSpecialMediator(Object eventTrigger,
			ISet updatedSet) {
		
		assert updatedSet != null : "can not handle selectionSet null-pointer";
		
		if (refExclusiveSet == updatedSet) {
			super.updateReceiver(eventTrigger, updatedSet);
		}		
	}

}
