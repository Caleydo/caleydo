/**
 * 
 */
package org.caleydo.core.manager.event.mediator;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.selection.ISelection;
import org.caleydo.core.manager.IGeneralManager;

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

	protected ISet ignoreSet;
	
	/**
	 * @param eventPublisher
	 * @param iMediatorId
	 */
	public LockableIngoreFilterMediator(final IGeneralManager generalManager,
			int iMediatorId,
			final ISet setExclusiveSet) {

		super(generalManager, iMediatorId, MediatorUpdateType.MEDIATOR_FILTER_ALL_EXPECT_SET);
		
		this.ignoreSet = setExclusiveSet;
	}
	
	public ISet getIgnoreSet() {
		return this.ignoreSet;
	}
	
	public void setIgnoreSet(ISet setIgnoreSet) {
		this.ignoreSet = setIgnoreSet;
	}
	

	/**
	 * The update is only forwareded if the ignoreSelectionSet (internal)
	 * is not equal the calling selectionSet.
	 * 	 
	 * @see org.caleydo.core.manager.event.mediator.LockableMediator#updateReceiverSpecialMediator(java.lang.Object, org.caleydo.core.data.collection.ISet)
	 */
	public final void updateReceiverSpecialMediator(Object eventTrigger,
			ISelection updatedSelection) {
		
		assert updatedSelection != null : "can not handle selectionSet null-pointer";
		
		if (ignoreSet != updatedSelection) {
			super.updateReceiver(eventTrigger, updatedSelection);
		}		
	}

}
