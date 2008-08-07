package org.caleydo.core.manager.event.mediator;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.selection.ISelection;

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

	protected ISet ignoreSet;

	/**
	 * Constructor.
	 */
	public LockableIgnoreFilterMediator(int iMediatorId,
			final ISet setExclusiveSet)
	{

		super(iMediatorId, MediatorUpdateType.MEDIATOR_FILTER_ALL_EXPECT_SET);

		this.ignoreSet = setExclusiveSet;
	}

	public ISet getIgnoreSet()
	{

		return this.ignoreSet;
	}

	public void setIgnoreSet(ISet setIgnoreSet)
	{

		this.ignoreSet = setIgnoreSet;
	}

	/**
	 * The update is only forwareded if the ignoreSelectionSet (internal) is not
	 * equal the calling selectionSet.
	 * 
	 * @see org.caleydo.core.manager.event.mediator.LockableMediator#updateReceiverSpecialMediator(java.lang.Object,
	 *      org.caleydo.core.data.collection.ISet)
	 */
	public final void updateReceiverSpecialMediator(Object eventTrigger,
			ISelection updatedSelection)
	{

		assert updatedSelection != null : "can not handle selectionSet null-pointer";

		if (ignoreSet != updatedSelection)
		{
			super.updateReceiver(eventTrigger, updatedSelection);
		}
	}

}
