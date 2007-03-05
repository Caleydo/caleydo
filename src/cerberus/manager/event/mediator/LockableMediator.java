/**
 * 
 */
package cerberus.manager.event.mediator;

import java.util.ArrayList;
import java.util.Iterator;

import cerberus.data.collection.ISet;
import cerberus.manager.IEventPublisher;
import cerberus.manager.event.mediator.MediatorUpdateType;

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

	protected final IEventPublisher refEventPublisher;
	
	protected ArrayList<IMediatorReceiver> arReceiver;

	protected ArrayList<IMediatorSender> arSender;

	/**
	 * 
	 */
	public LockableMediator(final IEventPublisher refEventPublisher,
			int iMediatorId,
			final MediatorUpdateType mediatorType ) {

		super(iMediatorId, mediatorType);

		this.refEventPublisher = refEventPublisher;
		
		arReceiver = new ArrayList<IMediatorReceiver>();
		arSender = new ArrayList<IMediatorSender>();
	}

	/* (non-Javadoc)
	 * @see cerberus.observer.mediator.IMediator#destroyMediator(cerberus.observer.mediator.IMediatorSender)
	 */
	protected final void destroyMediatorDerivedObject(final IMediatorSender sender) {
		
		updateStall();

		this.arReceiver.clear();
		this.arSender.clear();
	}
	
	/* (non-Javadoc)
	 * @see cerberus.observer.mediator.IMediator#register(cerberus.observer.mediator.IMediatorSender)
	 */
	public final boolean register(IMediatorSender sender) {
		assert sender != null : "can not register null-pointer";
		
		if (arSender.contains(sender))
		{
			//throw new CerberusRuntimeException("LockableMediator.register() receiver that is already registered!");
			return false;
		}

		arSender.add(sender);
		return true;
	}

	/* (non-Javadoc)
	 * @see cerberus.observer.mediator.IMediator#register(cerberus.observer.mediator.IMediatorReceiver)
	 */
	public final boolean register(IMediatorReceiver receiver) {
		assert receiver != null : "can not register null-pointer";
		
		if (arReceiver.contains(receiver))
		{
			//throw new CerberusRuntimeException("LockableMediator.register() receiver that is already registered!");
			return false;
		}

		arReceiver.add(receiver);
		return true;
	}

	/* (non-Javadoc)
	 * @see cerberus.observer.mediator.IMediator#unregister(cerberus.observer.mediator.IMediatorSender)
	 */
	public final boolean unregister(IMediatorSender sender) {
		assert sender != null : "can not register null-pointer";
		
		return arSender.remove(sender);
	}

	/* (non-Javadoc)
	 * @see cerberus.observer.mediator.IMediator#unregister(cerberus.observer.mediator.IMediatorReceiver)
	 */
	public final boolean unregister(IMediatorReceiver receiver) {
		assert receiver != null : "can not register null-pointer";
		
		return arReceiver.remove(receiver);
	}

	/**
	 * @see cerberus.manager.event.mediator.IMediator#hasReceiver(cerberus.manager.event.mediator.IMediatorReceiver)
	 */
	public final boolean hasReceiver( IMediatorReceiver receiver ) {
		assert receiver != null : "can not handle null-pointer";
		return arReceiver.contains(receiver);
	}
	
	/**
	 * @see cerberus.manager.event.mediator.IMediator#hasSender(cerberus.manager.event.mediator.IMediatorSender)
	 */
	public final boolean hasSender( IMediatorSender sender ) {
		assert sender != null : "can not handle null-pointer";
		return arSender.contains(sender);
	}



	/**
	 * Notification of update events.
	 * Calles updateReceiver(IMediatorSender) internal if updates are not stalled.
	 * 
	 * @see cerberus.manager.event.mediator.IMediatorReceiver#updateReceiver(Object)
	 * @see cerberus.observer.mediator.AThreadedMediatorReceiver#updateReceiver(cerberus.observer.mediator.IMediatorSender)
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
	 * @see cerberus.manager.event.mediator.LockableExclusivFilterMediator
	 * @see cerberus.manager.event.mediator.LockableIgnoreFilterMediator
	 * @see cerberus.manager.event.mediator.ALockableMediatorReceiver#updateReceiverSelection(java.lang.Object, cerberus.data.collection.ISet)
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

			// Prevent circular updates
			if (!currentReceiver.getClass().equals(eventTrigger.getClass()))
			{
				currentReceiver.updateReceiver(eventTrigger, updatedSet);
			}
		}
	}
	
}
