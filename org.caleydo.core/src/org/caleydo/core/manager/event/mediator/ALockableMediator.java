package org.caleydo.core.manager.event.mediator;

import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.exception.CaleydoRuntimeException;

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
	private final EMediatorType mediatorType;
	private final EMediatorUpdateType mediatorUpdateType;

	/**
	 * Constructor.
	 */
	protected ALockableMediator(final EMediatorType mediatorType,
			final EMediatorUpdateType mediatorUpdateType)
	{
		super();

		this.mediatorUpdateType = mediatorUpdateType;
		this.mediatorType = mediatorType;
	}

	public final EMediatorUpdateType getMediatorUpdateTypeType()
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
	
	
	public EMediatorType getType()
	{
		return mediatorType;
	}
}
