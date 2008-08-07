package org.caleydo.core.manager.event.mediator;

import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;

/**
 * Abstract class for the mediator that belongs to the event mechanism.
 * 
 * @see org.caleydo.core.manager.event.mediator.IMediator
 * @author Micheal Kalkusch
 * @author Marc Streit
 */
public abstract class ALockableMediator
	extends ALockableMediatorReceiver
{
	private final MediatorUpdateType mediatorUpdateType;

	public final int iMediatorId;

	/**
	 * Constructor.
	 */
	protected ALockableMediator(int iMediatorId,
			final MediatorUpdateType mediatorUpdateType)
	{
		super();

		this.iMediatorId = iMediatorId;

		if (mediatorUpdateType == null)
		{
			this.mediatorUpdateType = MediatorUpdateType.MEDIATOR_DEFAULT;
		}
		else
		{
			this.mediatorUpdateType = mediatorUpdateType;
		}
	}

	public final MediatorUpdateType getMediatorUpdateTypeType()
	{

		return mediatorUpdateType;
	}

	/**
	 * Implement cleanup inside this function.
	 * 
	 * @see org.caleydo.core.manager.event.mediator.ALockableMediator#destroyMediator(IEventPublisher)
	 * @param sender callling object
	 */
	protected abstract void destroyMediatorDerivedObject(final IEventPublisher sender);

	/**
	 * Test if caller is creator and calls
	 * destroyMediatorObject(IMediatorSender).
	 * 
	 * @see org.caleydo.core.manager.event.mediator.IMediator#destroyMediator()
	 * @see org.caleydo.core.manager.event.mediator.ALockableMediator#destroyMediatorDerivedObject(IMediatorSender)
	 */
	public final void destroyMediator(final IEventPublisher sender)
	{
		if (!GeneralManager.get().equals(sender))
		{
			throw new CaleydoRuntimeException(
					"IMediator.destroyMediator() may only be callled by its creator!");
		}

		destroyMediatorDerivedObject(sender);
	}

	/**
	 * @see org.caleydo.core.data.IUniqueObject#getID()
	 */
	public final int getID()
	{

		return iMediatorId;
	}

	/**
	 * Since the MediatorId is final this method must not be called.
	 * 
	 * @see org.caleydo.core.data.IUniqueObject#setId(int)
	 */
	public final void setId(int isetId)
	{

		throw new CaleydoRuntimeException("setId() must not be called.",
				CaleydoRuntimeExceptionType.OBSERVER);

		// this.iMediatorId = isetId;
	}
}
