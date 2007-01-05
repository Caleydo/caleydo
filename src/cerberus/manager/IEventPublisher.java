package cerberus.manager;

import java.util.ArrayList;

import cerberus.data.collection.ISet;
import cerberus.manager.event.mediator.IMediatorReceiver;
import cerberus.manager.event.mediator.IMediatorSender;

/**
 * Handle events using Publish subsrib design pattern.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * 
 */
public interface IEventPublisher 
extends IGeneralManager {

	public enum MediatorType {
		DATA_MEDIATOR,
		SELECTION_MEDIATOR,
		VIEWING_DATA_MEDIATOR //for future usage
	}
	
	/**
	 * Creates a mediator and registers the senders and receivers
	 * to this mediator.
	 */
	public void createMediator (int iMediatorId, 
			ArrayList<Integer> arSenderIDs, 
			ArrayList<Integer> arReceiverIDs,
			MediatorType mediatorType);
	
	/**
	 * Adds a sender to an existing mediator. 
	 */
	public void registerSenderToMediator (int iMediatorId, 
			IMediatorSender sender);
	
	/**
	 * Adds a sender to an existing mediator (using the sender ID).
	 */
	public void registerSenderToMediator (int iMediatorId,
			int iMediatorSenderId);
	
	/**
	 * Adds a receiver to an existing mediator.
	 */
	public void registerReceiverToMediator (int iMediatorId, 
			IMediatorReceiver receiver);
	
	/**
	 * Adds a receiver to an existing mediator (using the receiver ID).
	 */
	public void registerReceiverToMediator (int iMediatorId, 
			int iMediatorReceiverId);
	
	/**
	 * Removes a sender from a mediator.
	 */
	public void unregisterSenderToMediator (int iMediatorId, 
			IMediatorSender sender);
	
	/**
	 * Removes a sender from a mediator (using the sender ID).
	 */
	public void unregisterSenderToMediator (int iMediatorId,
			int iMediatorSenderId);
	
	/**
	 * Removes a receiver from a mediator.
	 */
	public void unregisterReceiverToMediator (int iMediatorId, 
			IMediatorReceiver receiver);

	/**
	 * Removes a receiver from a mediator (using the receiver ID).
	 */
	public void unregisterReceiverToMediator (int iMediatorId,
			int iMediatorReceiverId);
	
	/**
	 * Casts the event trigger object to ISender and looks up the Mediator for
	 * this sender. On the mediator object then the update method is called.
	 * 
	 * @param eventTrigger
	 */
	public void update(Object triggerObject);
	
	public void updateSelection(Object triggerObject, ISet selectionSet);
	
	/**
	 * Called only by Sender.
	 * 
	 * @param sender
	 */
	public void removeMediator( IMediatorSender sender );
	
	public boolean hasRelation( IMediatorSender sender,
			IMediatorReceiver receiver );
}
