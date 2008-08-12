package org.caleydo.core.manager.event.mediator;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.selection.ISelection;
import org.caleydo.core.data.selection.ISelectionDelta;

/**
 * Ignore one special SelectionSet and let all other SelectionSet's pass when
 * calling
 * org.caleydo.core.manager.event.mediator.IMediator#updateReceiverSelection
 * (Object, ISet)
 * 
 * @author Michael Kalkusch
 */
public class LockableIgnoreFilterMediator
	extends LockableMediator
{

	protected ISelection ignoreSelection;

	/**
	 * Constructor.
	 */
	public LockableIgnoreFilterMediator(final ISet setExclusiveSet)
	{

		super(MediatorUpdateType.MEDIATOR_FILTER_ALL_EXPECT_SET);

		this.ignoreSelection = setExclusiveSet;
	}

	public ISelection getIgnoreSet()
	{

		return this.ignoreSelection;
	}

	public void setIgnoreSet(ISet setIgnoreSet)
	{

		this.ignoreSelection = setIgnoreSet;
	}

	/**
	 * The update is only forwareded if the ignoreSelectionSet (internal) is not
	 * equal the calling selectionSet.
	 * 
	 * @see org.caleydo.core.manager.event.mediator.LockableMediator#updateReceiverSpecialMediator(java.lang.Object,
	 *      org.caleydo.core.data.collection.ISet)
	 */
	public final void updateReceiverSpecialMediator(Object eventTrigger,
			ISelectionDelta selectionDelta)
	{
			super.updateReceiver(eventTrigger, selectionDelta);
	}

}
