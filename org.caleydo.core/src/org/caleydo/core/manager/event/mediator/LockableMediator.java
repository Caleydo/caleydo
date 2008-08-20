package org.caleydo.core.manager.event.mediator;

import java.util.ArrayList;
import java.util.Iterator;
import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.data.selection.ISelectionDelta;
import org.caleydo.core.manager.IEventPublisher;

/**
 * Attention: Since Mediator is also a IMediatorReceiver care has to be taken
 * when registering a Mediator as Receiver.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class LockableMediator
	extends ALockableMediator
	implements IMediator, IUniqueObject
{
	protected ArrayList<IMediatorReceiver> arReceiver;

	protected ArrayList<IMediatorSender> arSender;

	/**
	 * Constructor.
	 */
	public LockableMediator(final MediatorUpdateType mediatorType)
	{
		super(mediatorType);

		arReceiver = new ArrayList<IMediatorReceiver>();
		arSender = new ArrayList<IMediatorSender>();
	}

	@Override
	protected final void destroyMediatorDerivedObject(final IEventPublisher sender)
	{

		updateStall();

		this.arReceiver.clear();
		this.arSender.clear();
	}

	@Override
	public final boolean register(IMediatorSender sender)
	{
		if (arSender.contains(sender))
		{
//			 throw new CaleydoRuntimeException(
//			 "LockableMediator.register() receiver that is already registered!", 
//			 CaleydoRuntimeExceptionType.EVENT);
			
			 return false;
		}

		arSender.add(sender);
		return true;
	}

	@Override
	public final boolean register(IMediatorReceiver receiver)
	{

		assert receiver != null : "can not register null-pointer";

		if (arReceiver.contains(receiver))
		{
			// throw new CaleydoRuntimeException(
			// "LockableMediator.register() receiver that is already registered!"
			// );
			return false;
		}

		arReceiver.add(receiver);
		System.out
				.println("LockableMediator.register( Receiver " + receiver.toString() + " )");
		return true;
	}

	@Override
	public final boolean unregister(IMediatorSender sender)
	{

		assert sender != null : "can not register null-pointer";

		return arSender.remove(sender);
	}

	@Override
	public final boolean unregister(IMediatorReceiver receiver)
	{

		assert receiver != null : "can not register null-pointer";

		return arReceiver.remove(receiver);
	}

	/**
	 * @see org.caleydo.core.manager.event.mediator.IMediator#hasReceiver(org.caleydo.core.manager.event.mediator.IMediatorReceiver)
	 */
	public final boolean hasReceiver(IMediatorReceiver receiver)
	{

		assert receiver != null : "can not handle null-pointer";
		return arReceiver.contains(receiver);
	}

	/**
	 * @see org.caleydo.core.manager.event.mediator.IMediator#hasSender(org.caleydo.core.manager.event.mediator.IMediatorSender)
	 */
	public final boolean hasSender(IMediatorSender sender)
	{

		assert sender != null : "can not handle null-pointer";
		return arSender.contains(sender);
	}

	/**
	 * Notification of update events. Calles updateReceiver(IMediatorSender)
	 * internal if updates are not stalled.
	 * 
	 * @see org.caleydo.core.manager.event.mediator.IMediatorReceiver#handleUpdate(IUniqueObject)
	 * @see org.caleydo.core.observer.mediator.AThreadedMediatorReceiver#handleUpdate(IUniqueObject)
	 */
	@Override
	public void handleUpdate(IUniqueObject eventTrigger)
	{

		assert eventTrigger != null : "can not handle null-pointer";

		if (bUpdateIsStalled.get())
		{
			Iterator<IMediatorReceiver> iter = arReceiver.iterator();

			while (iter.hasNext())
			{

				IMediatorReceiver currentReceiver = (IMediatorReceiver) iter.next();

				// Prevent circular updates
				if (!currentReceiver.getClass().equals(eventTrigger.getClass()))
				{
					currentReceiver.handleUpdate(eventTrigger);
				}
			} // while (iter.hasNext())

		} // if ( bUpdateIsStalled.get() ) {
	}

	/**
	 * Base implementation.
	 * 
	 * @see org.caleydo.core.manager.event.mediator.LockableExclusivFilterMediator
	 * @see org.caleydo.core.manager.event.mediator.LockableIgnoreFilterMediator
	 * @see org.caleydo.core.manager.event.mediator.ALockableMediatorReceiver#updateReceiverSelection(java.lang.Object,
	 *      org.caleydo.core.data.collection.ISet)
	 */
	@Override
	public void updateReceiverSpecialMediator(IUniqueObject eventTrigger, ISelectionDelta selectionDelta)
	{
		Iterator<IMediatorReceiver> iter = arReceiver.iterator();

		while (iter.hasNext())
		{
			IMediatorReceiver currentReceiver = (IMediatorReceiver) iter.next();

			/* Prevent circular updates */
			if (!currentReceiver.equals(eventTrigger))
			{
				currentReceiver.handleUpdate(eventTrigger, selectionDelta);
			}
			else
			{
				// generalManager.logMsg(
				// this.getClass().toString() +
				// ".updateReceiverSpecialMediator(Object eventTrigger=[" +
				// eventTrigger.toString() +
				// "]) is also a receiver! Cycle detected!",
				// LoggerType.MINOR_ERROR);
			}
		}
	}


	@Override
	public void updateContinue(Object eventTrigger)
	{
		// TODO Auto-generated method stub
		
	}
}
