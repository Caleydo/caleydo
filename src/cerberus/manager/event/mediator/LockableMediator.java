/**
 * 
 */
package cerberus.manager.event.mediator;

import java.util.ArrayList;
import java.util.Iterator;

import cerberus.data.collection.ISet;

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
	public LockableMediator(int iMediatorId) {

		super(iMediatorId);

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

		return arSender.remove(sender);
	}

	/* (non-Javadoc)
	 * @see cerberus.observer.mediator.IMediator#unregister(cerberus.observer.mediator.IMediatorReceiver)
	 */
	public boolean unregister(IMediatorReceiver receiver) {

		return arReceiver.remove(receiver);
	}

}
