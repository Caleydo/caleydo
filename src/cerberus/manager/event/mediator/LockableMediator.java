/**
 * 
 */
package cerberus.manager.event.mediator;

import java.util.ArrayList;
import java.util.Iterator;

import cerberus.data.collection.ISet;
import cerberus.manager.IEventPublisher;

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
			int iMediatorId) {

		super(iMediatorId);

		this.refEventPublisher = refEventPublisher;
		
		arReceiver = new ArrayList<IMediatorReceiver>();
		arSender = new ArrayList<IMediatorSender>();
	}

	/* (non-Javadoc)
	 * @see cerberus.observer.mediator.IMediator#destroyMediator(cerberus.observer.mediator.IMediatorSender)
	 */
	protected void destroyMediatorDerivedObject(final IMediatorSender sender) {
		
		updateStall();

		this.arReceiver.clear();
		this.arSender.clear();
	}

	/* (non-Javadoc)
	 * @see cerberus.observer.mediator.AThreadedMediatorReceiver#updateReceiver(cerberus.observer.mediator.IMediatorSender)
	 */
	@Override
	public void updateReceiver(Object eventTrigger) {
		assert eventTrigger != null : "can not handle null-pointer";
		
		Iterator<IMediatorReceiver> iter = arReceiver.iterator();

		while (iter.hasNext())
		{

			IMediatorReceiver currentReceiver = (IMediatorReceiver) iter.next();

			// Prevent circular updates
			if (!currentReceiver.getClass().equals(eventTrigger.getClass()))
			{
				currentReceiver.update(eventTrigger);
			}
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.manager.event.mediator.ALockableMediatorReceiver#updateReceiverSelection(java.lang.Object, cerberus.data.collection.ISet)
	 */
	@Override
	public void updateReceiverSelection(Object eventTrigger,
			ISet selectionSet) {
		assert eventTrigger != null : "can not handle eventTrigger null-pointer";
		assert selectionSet != null : "can not handle selectionSet null-pointer";
		
		Iterator<IMediatorReceiver> iter = arReceiver.iterator();

		while (iter.hasNext())
		{
			IMediatorReceiver currentReceiver = (IMediatorReceiver) iter.next();

			// Prevent circular updates
			if (!currentReceiver.getClass().equals(eventTrigger.getClass()))
			{
				currentReceiver.updateSelection(eventTrigger, selectionSet);
			}
		}
	}

	/* (non-Javadoc)
	 * @see cerberus.observer.mediator.IMediator#register(cerberus.observer.mediator.IMediatorSender)
	 */
	public boolean register(IMediatorSender sender) {
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
	public boolean register(IMediatorReceiver receiver) {
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
	public boolean unregister(IMediatorSender sender) {
		assert sender != null : "can not register null-pointer";
		
		return arSender.remove(sender);
	}

	/* (non-Javadoc)
	 * @see cerberus.observer.mediator.IMediator#unregister(cerberus.observer.mediator.IMediatorReceiver)
	 */
	public boolean unregister(IMediatorReceiver receiver) {
		assert receiver != null : "can not register null-pointer";
		
		return arReceiver.remove(receiver);
	}

}
