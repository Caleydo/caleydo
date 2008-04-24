/**
 * 
 */
package org.caleydo.core.manager.event.mediator;

import java.util.ArrayList;
import java.util.Iterator;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.ILoggerManager.LoggerType;
import org.caleydo.core.manager.event.mediator.MediatorUpdateType;

/**
 * Attention: Since Mediator is also a IMediatorReceiver care 
 * has to be taken when registering a Mediator as Receiver.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class LockableMediator 
extends ALockableMediator 
implements IMediator {
	
	protected ArrayList<IMediatorReceiver> arReceiver;

	protected ArrayList<IMediatorSender> arSender;

	/**
	 * 
	 */
	public LockableMediator(final IEventPublisher refEventPublisher,
			int iMediatorId,
			final MediatorUpdateType mediatorType ) {

		super(refEventPublisher, 
				iMediatorId, 
				mediatorType);
		
		arReceiver = new ArrayList<IMediatorReceiver>();
		arSender = new ArrayList<IMediatorSender>();
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.observer.mediator.IMediator#destroyMediator(org.caleydo.core.observer.mediator.IMediatorSender)
	 */
	protected final void destroyMediatorDerivedObject(final IEventPublisher sender) {
		
		updateStall();

		this.arReceiver.clear();
		this.arSender.clear();
	}
	
	/* (non-Javadoc)
	 * @see org.caleydo.core.observer.mediator.IMediator#register(org.caleydo.core.observer.mediator.IMediatorSender)
	 */
	public final boolean register(IMediatorSender sender) {
		assert sender != null : "can not register null-pointer";
		
		if (arSender.contains(sender))
		{
			//throw new CaleydoRuntimeException("LockableMediator.register() receiver that is already registered!");
			return false;
		}

		arSender.add(sender);
		System.out.println("LockableMediator.register( Sender " + sender.toString() + " )");
		return true;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.observer.mediator.IMediator#register(org.caleydo.core.observer.mediator.IMediatorReceiver)
	 */
	public final boolean register(IMediatorReceiver receiver) {
		assert receiver != null : "can not register null-pointer";
		
		if (arReceiver.contains(receiver))
		{
			//throw new CaleydoRuntimeException("LockableMediator.register() receiver that is already registered!");
			return false;
		}

		arReceiver.add(receiver);
		System.out.println("LockableMediator.register( Receiver " + receiver.toString() + " )");
		return true;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.observer.mediator.IMediator#unregister(org.caleydo.core.observer.mediator.IMediatorSender)
	 */
	public final boolean unregister(IMediatorSender sender) {
		assert sender != null : "can not register null-pointer";
		
		return arSender.remove(sender);
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.observer.mediator.IMediator#unregister(org.caleydo.core.observer.mediator.IMediatorReceiver)
	 */
	public final boolean unregister(IMediatorReceiver receiver) {
		assert receiver != null : "can not register null-pointer";
		
		return arReceiver.remove(receiver);
	}

	/**
	 * @see org.caleydo.core.manager.event.mediator.IMediator#hasReceiver(org.caleydo.core.manager.event.mediator.IMediatorReceiver)
	 */
	public final boolean hasReceiver( IMediatorReceiver receiver ) {
		assert receiver != null : "can not handle null-pointer";
		return arReceiver.contains(receiver);
	}
	
	/**
	 * @see org.caleydo.core.manager.event.mediator.IMediator#hasSender(org.caleydo.core.manager.event.mediator.IMediatorSender)
	 */
	public final boolean hasSender( IMediatorSender sender ) {
		assert sender != null : "can not handle null-pointer";
		return arSender.contains(sender);
	}



	/**
	 * Notification of update events.
	 * Calles updateReceiver(IMediatorSender) internal if updates are not stalled.
	 * 
	 * @see org.caleydo.core.manager.event.mediator.IMediatorReceiver#updateReceiver(Object)
	 * @see org.caleydo.core.observer.mediator.AThreadedMediatorReceiver#updateReceiver(org.caleydo.core.observer.mediator.IMediatorSender)
	 */
	@Override
	public void updateReceiver(Object eventTrigger) {
		assert eventTrigger != null : "can not handle null-pointer";
		
		if ( bUpdateIsStalled.get() ) {
			Iterator<IMediatorReceiver> iter = arReceiver.iterator();
	
			while (iter.hasNext())
			{
	
				IMediatorReceiver currentReceiver = (IMediatorReceiver) iter.next();
	
				// Prevent circular updates
				if (!currentReceiver.getClass().equals(eventTrigger.getClass()))
				{
					currentReceiver.updateReceiver(eventTrigger);
				}
			} //while (iter.hasNext())
			
		} //if ( bUpdateIsStalled.get() ) {
	}
	
	
	/**
	 * Base implementation.
	 * 
	 * @see org.caleydo.core.manager.event.mediator.LockableExclusivFilterMediator
	 * @see org.caleydo.core.manager.event.mediator.LockableIgnoreFilterMediator
	 * @see org.caleydo.core.manager.event.mediator.ALockableMediatorReceiver#updateReceiverSelection(java.lang.Object, org.caleydo.core.data.collection.ISet)
	 */
	@Override
	public void updateReceiverSpecialMediator(Object eventTrigger,
			ISet updatedSet) {
		
		assert eventTrigger != null : "can not handle eventTrigger null-pointer";
		assert updatedSet != null : "can not handle selectionSet null-pointer";
		
		Iterator<IMediatorReceiver> iter = arReceiver.iterator();

		while (iter.hasNext())
		{
			IMediatorReceiver currentReceiver = (IMediatorReceiver) iter.next();

			/*  Prevent circular updates */
			if (!currentReceiver.equals(eventTrigger))
			{
				currentReceiver.updateReceiver(eventTrigger, updatedSet);
			} 
			else 
			{
				refEventPublisher.getSingleton().logMsg(
						this.getClass().toString() + 
						".updateReceiverSpecialMediator(Object eventTrigger=[" +
						eventTrigger.toString() +
						"]) is also a receiver! Cycle detected!",
						LoggerType.MINOR_ERROR);
			}
		}
	}
	
}
