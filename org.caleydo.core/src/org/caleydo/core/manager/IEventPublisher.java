package org.caleydo.core.manager;

import java.util.ArrayList;
import org.caleydo.core.manager.event.mediator.EMediatorType;
import org.caleydo.core.manager.event.mediator.EMediatorUpdateType;
import org.caleydo.core.manager.event.mediator.IMediator;
import org.caleydo.core.manager.event.mediator.IMediatorReceiver;
import org.caleydo.core.manager.event.mediator.IMediatorSender;

/**
 * Handle events using Publish subscriber design pattern.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public interface IEventPublisher
	extends IManager<IMediator>, IMediatorReceiver
{
	/**
	 * Creates a mediator and registers the senders and receivers to this
	 * mediator. mediatorUpdateType defines whether selection filtering is
	 * supported or not.
	 */
	public IMediator createMediator(ArrayList<Integer> arSenderIDs,
			ArrayList<Integer> arReceiverIDs, EMediatorType mediatorType,
			EMediatorUpdateType mediatorUpdateType);

	/**
	 * Adds a list of senders and or receivers to a Mediator.
	 * 
	 * @param newMediator
	 * @param arSenderIDs
	 * @param arReceiverIDs
	 * @param mediatorType
	 * @param mediatorUpdateType
	 */
	public void addSendersAndReceiversToMediator(IMediator newMediator,
			ArrayList<Integer> arSenderIDs, ArrayList<Integer> arReceiverIDs,
			EMediatorType mediatorType, EMediatorUpdateType mediatorUpdateType);

	/**
	 * Adds a sender to an existing mediator.
	 */
	public void registerSenderToMediator(int iMediatorId, IMediatorSender sender);

	/**
	 * Adds a sender to an existing mediator (using the sender ID).
	 */
	public void registerSenderToMediator(int iMediatorId, int iMediatorSenderId);

	/**
	 * Adds a receiver to an existing mediator.
	 */
	public void registerReceiverToMediator(int iMediatorId, IMediatorReceiver receiver);

	/**
	 * Adds a receiver to an existing mediator (using the receiver ID).
	 */
	public void registerReceiverToMediator(int iMediatorId, int iMediatorReceiverId);

	/**
	 * Removes a sender from a mediator.
	 */
	public void unregisterSenderToMediator(int iMediatorId, IMediatorSender sender);

	/**
	 * Removes a sender from a mediator (using the sender ID).
	 */
	public void unregisterSenderToMediator(int iMediatorId, int iMediatorSenderId);

	/**
	 * Removes a receiver from a mediator.
	 */
	public void unregisterReceiverToMediator(int iMediatorId, IMediatorReceiver receiver);

	/**
	 * Removes a receiver from a mediator (using the receiver ID).
	 */
	public void unregisterReceiverToMediator(int iMediatorId, int iMediatorReceiverId);

	/**
	 * Called only by Sender.
	 * 
	 * @param sender
	 */
	public void removeMediator(IMediatorSender sender);

	/**
	 * Test if there is a relation between sender and receiver.
	 * 
	 * @param sender
	 * @param receiver
	 * @return TRUE if sender is linked to receiver
	 */
	public boolean hasRelation(IMediatorSender sender, IMediatorReceiver receiver);

	public void registerSenderToMediatorGroup(EMediatorType mediatorType, 
			IMediatorSender sender);
	
	public void registerReceiverToMediatorGroup(EMediatorType mediatorType, 
			IMediatorReceiver receiver);
	
	public void removeReceiver(IMediatorReceiver receiver);
}
