package cerberus.manager.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import cerberus.data.collection.ISet;
import cerberus.manager.IEventPublisher;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.base.AAbstractManager;
import cerberus.manager.event.mediator.IMediator;
import cerberus.manager.event.mediator.IMediatorReceiver;
import cerberus.manager.event.mediator.IMediatorSender;
import cerberus.manager.event.mediator.LockableMediator;
import cerberus.manager.event.mediator.LockableExclusivFilterMediator;
import cerberus.manager.event.mediator.LockableIngoreFilterMediator;
import cerberus.manager.event.mediator.MediatorUpdateType;
import cerberus.manager.type.ManagerObjectType;
import cerberus.manager.type.ManagerType;

import cerberus.util.exception.CerberusRuntimeExceptionType;
import cerberus.util.exception.CerberusRuntimeException;

public class EventPublisher 
extends AAbstractManager
implements IEventPublisher {

	protected HashMap<Integer, IMediator> hashMediatorId2Mediator;

	protected HashMap<IMediatorSender, ArrayList<IMediator>> hashSender2DataMediators;

	protected HashMap<IMediatorReceiver, ArrayList<IMediator>> hashReceiver2DataMediators;

	protected HashMap<IMediatorSender, ArrayList<IMediator>> hashSender2SelectionMediators;

	protected HashMap<IMediatorReceiver, ArrayList<IMediator>> hashReceiver2SelectionMediators;
	
	protected HashMap<IMediatorSender, ArrayList<IMediator>> hashSender2ViewMediators;

	protected HashMap<IMediatorReceiver, ArrayList<IMediator>> hashReceiver2ViewMediators;
	
	
	/**
	 * Constructor.
	 * 
	 * @param refGeneralManager
	 */
	public EventPublisher(final IGeneralManager refGeneralManager) {

		super( refGeneralManager,  
				IGeneralManager.iUniqueId_TypeOffset_EventPublisher,
				ManagerType.EVENT_PUBLISHER );
		
		/* Data */
		hashSender2DataMediators = 
			new HashMap<IMediatorSender, ArrayList<IMediator>>();
		hashReceiver2DataMediators = 
			new HashMap<IMediatorReceiver, ArrayList<IMediator>>();
		
		/* Selection */
		hashSender2SelectionMediators = 
			new HashMap<IMediatorSender, ArrayList<IMediator>>();
		hashReceiver2SelectionMediators = 
			new HashMap<IMediatorReceiver, ArrayList<IMediator>>();
		
		/* View */
		hashReceiver2ViewMediators =
			new HashMap<IMediatorReceiver, ArrayList<IMediator>> ();		
		hashSender2ViewMediators =
			new HashMap<IMediatorSender, ArrayList<IMediator>> ();
	}

	private synchronized void insertReceiver( HashMap<IMediatorReceiver, ArrayList<IMediator>> insertIntoHashMap, 
			IMediatorReceiver receiver,
			IMediator newMediator ) {
		assert insertIntoHashMap != null : "can not handle insertIntoHashMap null-pointer";
		assert receiver != null : "can not handle receiver null-pointer";
		assert newMediator != null : "can not handle newMediator null-pointer";
		
		if (!insertIntoHashMap.containsKey(receiver))
		{
			insertIntoHashMap.put(receiver, new ArrayList<IMediator>());
		}
		insertIntoHashMap.get(receiver).add(newMediator);	
	}
	
	private synchronized void insertSender( 
			HashMap<IMediatorSender, ArrayList<IMediator>> insertIntoHashMap, 
			IMediatorSender sender,
			IMediator newMediator ) {
		assert insertIntoHashMap != null : "can not handle insertIntoHashMap null-pointer";
		assert sender != null : "can not handle sender null-pointer";
		assert newMediator != null : "can not handle newMediator null-pointer";
		
		if (!insertIntoHashMap.containsKey(sender))
		{
			insertIntoHashMap.put(sender, new ArrayList<IMediator>());
		}
		
		
		ArrayList <IMediator> bufferArrayList = insertIntoHashMap.get(sender);
		
		if ( bufferArrayList.contains( sender ) ) {
			throw new CerberusRuntimeException("Try to insert an existing object! " + 
					sender.toString() + " ==> " + newMediator.toString() + 
					" inside map" + bufferArrayList.toString(),
					CerberusRuntimeExceptionType.OBSERVER);
		}
		
		bufferArrayList.add(newMediator);
		
//		//unchecked!
//		insertIntoHashMap.get(sender).add(newMediator);
	}
	
	public synchronized void createMediator(int iMediatorId, 
			ArrayList<Integer> arSenderIDs,
			ArrayList<Integer> arReceiverIDs, 
			MediatorType mediatorType,
			MediatorUpdateType mediatorUpdateType) {

		IMediator newMediator = null;
		
		switch (mediatorUpdateType) {
		case MEDIATOR_DEFAULT:
			newMediator = 
				new LockableMediator(this, 
						iMediatorId, 
						MediatorUpdateType.MEDIATOR_DEFAULT);
			break;
			
		case MEDIATOR_FILTER_ONLY_SET:
			newMediator = 
				new LockableExclusivFilterMediator(this, 
						iMediatorId, 
						null);
			break;
			
		case MEDIATOR_FILTER_ALL_EXPECT_SET:
			newMediator = 
				new LockableIngoreFilterMediator(this, 
						iMediatorId, 
						null);
			break;
			default:
				assert false : "unknown type";
			return;
		}
		

		Iterator<Integer> iterSenderIDs = arSenderIDs.iterator();

		boolean bHasValidSender = false;
		
		// Register sender
		while (iterSenderIDs.hasNext())
		{
			int iCurrentSenderId = iterSenderIDs.next();
			IMediatorSender sender = null;
			
			try
			{
				sender = (IMediatorSender) refGeneralManager
						.getItem(iCurrentSenderId);
			} 
			catch ( ClassCastException cce)
			{
				refSingelton.logMsg("EventPublisher.createMediator() failed because referenced sender object id=[" +
						iCurrentSenderId + 
						"] does not implement interface IMediatorSender " +
						refGeneralManager.getItem(iCurrentSenderId).getClass(),
						LoggerType.ERROR_ONLY);
				
				assert false : "receiver object does not implement interface IMediatorSender";
				break;
			}

			if ( sender == null ) {
				refSingelton.logMsg("EventPublisher: invalid SenderId=[" +
						iCurrentSenderId + "] => receiverId=" + 
						arReceiverIDs.toString() + 
						" ignore sender!",
						LoggerType.MINOR_ERROR);
			}
			else 
			{
				bHasValidSender = true;
				
				newMediator.register(sender);
				
				switch ( mediatorType ) {
				
				case DATA_MEDIATOR:
					
					//assert false : "test this code!";
					
					if (!hashSender2DataMediators.containsKey(sender))
					{
						hashSender2DataMediators.put(sender, 
								new ArrayList<IMediator>());
					}
					hashSender2DataMediators.get(sender).add(newMediator);
					
					insertSender(hashSender2DataMediators,sender,newMediator);
					break;
					
				case SELECTION_MEDIATOR:
					insertSender(hashSender2SelectionMediators,sender,newMediator);
					
					//BUG??
					insertSender(hashSender2SelectionMediators,sender,newMediator);
					
	//				if (!hashSender2SelectionMediators.containsKey(sender))
	//				{
	//					hashSender2SelectionMediators.put(sender, new ArrayList<IMediator>());
	//				}
	//				hashSender2SelectionMediators.get(sender).add(newMediator);
					break;
					
				case VIEW_MEDIATOR:
					insertSender(hashSender2ViewMediators,sender,newMediator);
	//				if (!hashSender2ViewMediators.containsKey(sender))
	//				{
	//					hashSender2ViewMediators.put(sender, new ArrayList<IMediator>());
	//				}
	//				hashSender2ViewMediators.get(sender).add(newMediator);
					break;
					
				default:
					throw new CerberusRuntimeException(
							"createMediator() unknown type sender: " + 
							mediatorType.toString(),
							CerberusRuntimeExceptionType.OBSERVER);
				
				} //switch ( mediatorType ) {
			
			} //if ( sender == null ) {...} else {..
			
		} //while (iterSenderIDs.hasNext())

		// are there any valid senders?
		if ( ! bHasValidSender ) 
		{
			refSingelton.logMsg("EventPublisher: all SenderId(s)=" +
					arSenderIDs.toString() + " are invalid; ignore all receivers=" +
					arReceiverIDs.toString() + " also!",
					LoggerType.MINOR_ERROR);
			return;
		}
		

		Iterator<Integer> iterReceiverIDs = arReceiverIDs.iterator();
		boolean bHasValidReceiver = false;
		
		// Register receiver
		while (iterReceiverIDs.hasNext())
		{
			int iCurrentReceiverId = iterReceiverIDs.next();
			
			IMediatorReceiver receiver = null;
			try 
			{
				receiver = (IMediatorReceiver) refGeneralManager
					.getItem(iCurrentReceiverId);
			} 
			catch ( ClassCastException cce)
			{
				refSingelton.logMsg("EventPublisher.createMediator() failed because referenced receiver object id=[" +
						iCurrentReceiverId + 
						"] does not implement interface IMediatorReceiver " +
						refGeneralManager.getItem(iCurrentReceiverId).getClass(),
						LoggerType.ERROR_ONLY);
				
				assert false : "receiver object does not implement interface IMediatorReceiver";
				break;
			}

			if ( receiver == null ) {
				refSingelton.logMsg("EventPublisher: invalid ReceiverId=[" +
						iCurrentReceiverId + "] <= sender(s)" +
						arSenderIDs.toString() + " ignore receiver!",
						LoggerType.MINOR_ERROR);
			}
			else 
			{
				bHasValidReceiver = true;
				
				newMediator.register(receiver);
	
				switch ( mediatorType ) {
				
				case DATA_MEDIATOR:
					if (!hashReceiver2DataMediators.containsKey(receiver))
					{
						hashReceiver2DataMediators.put(receiver, new ArrayList<IMediator>());
					}
					hashReceiver2DataMediators.get(receiver).add(newMediator);
					
					//assert false : "test this code!";
					
					insertReceiver( hashReceiver2DataMediators,receiver,newMediator);
					break;
				
				case SELECTION_MEDIATOR:
					insertReceiver( hashReceiver2SelectionMediators,receiver,newMediator);		
					break;
					
				case VIEW_MEDIATOR:
					insertReceiver( hashReceiver2SelectionMediators,receiver,newMediator);				
					break;
					
					default:
						throw new CerberusRuntimeException(
								"createMediator() unknown type receiver: " + 
								mediatorType.toString(),
								CerberusRuntimeExceptionType.OBSERVER);
				} // switch ( mediatorType ) {
			
				refSingelton.logMsg("EventPublisher: successful added senderId(s)" +
						arSenderIDs.toString() + " => [" +
						iCurrentReceiverId + "]",
						LoggerType.VERBOSE);
				
			} // if ( receiver == null ) {...} else {..
			
		} // while (iterReceiverIDs.hasNext())
		
		if ( ! bHasValidReceiver )
		{
			refSingelton.logMsg("EventPublisher: ignore command with senderId(s)=[" +
					arSenderIDs.toString() + "] and receiverId(s)=[" +
					arReceiverIDs.toString() + "] because no valid receiver was found!",
					LoggerType.MINOR_ERROR);
			return;
		}
		
		refSingelton.logMsg("EventPublisher: success registering senderId(s)=[" +
				arSenderIDs.toString() + "] and receiverId(s)=[" +
				arReceiverIDs.toString() + "]",
				LoggerType.VERBOSE);
	}

	public void registerSenderToMediator(int iMediatorId, IMediatorSender sender) {

		// TODO Auto-generated method stub

	}

	public void registerSenderToMediator(int iMediatorId, int iMediatorSenderId) {

		// TODO Auto-generated method stub

	}

	public void registerReceiverToMediator(int iMediatorId,
			IMediatorReceiver receiver) {

		// TODO Auto-generated method stub

	}

	public void registerReceiverToMediator(int iMediatorId,
			int iMediatorReceiverId) {

		// TODO Auto-generated method stub

	}

	public void unregisterSenderToMediator(int iMediatorId,
			IMediatorSender sender) {

		// TODO Auto-generated method stub

	}

	public void unregisterSenderToMediator(int iMediatorId,
			int iMediatorSenderId) {

		// TODO Auto-generated method stub

	}

	public void unregisterReceiverToMediator(int iMediatorId,
			IMediatorReceiver receiver) {

		// TODO Auto-generated method stub

	}

	public void unregisterReceiverToMediator(int iMediatorId,
			int iMediatorReceiverId) {

		// TODO Auto-generated method stub

	}

	/*
	 *  (non-Javadoc)
	 * @see cerberus.manager.IEventPublisher#update(java.lang.Object)
	 */
	public void update(Object eventTrigger) {

		// Prevent update during initialization of data.
		if (hashSender2DataMediators.isEmpty())
		{
			return;
		}

		// Dont't do anything if sender is not registered
		if (!hashSender2DataMediators.containsKey((IMediatorSender) eventTrigger))
		{
//			assert false : "Sender is not registered and calls update(); " +
//				eventTrigger.toString() + "]";
			/* work around! */
			
			return;
		}
		
		ArrayList<IMediator> arMediators = hashSender2DataMediators
				.get((IMediatorSender) eventTrigger);
		
		Iterator<IMediator> iterMediators = arMediators.iterator();

		IMediator tmpMediator;

		while (iterMediators.hasNext())
		{
			tmpMediator = iterMediators.next();

			if (tmpMediator != null)
			{
				tmpMediator.updateReceiver(eventTrigger);
			} else
			{
				// TODO: print message
				assert false : "Mediator is null while calling update(); " +
				eventTrigger.toString() + "]";
			}
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.manager.IEventPublisher#updateSelection(java.lang.Object, cerberus.data.collection.ISet)
	 */
	public void updateSelection(Object eventTrigger, ISet selectionSet) {
		
		// Prevent update during initialization of data.
		if (hashSender2SelectionMediators.isEmpty())
		{
			return;
		}

		ArrayList<IMediator> arMediators = hashSender2SelectionMediators
				.get((IMediatorSender) eventTrigger);
		
		Iterator<IMediator> iterMediators = arMediators.iterator();

		IMediator tmpMediator;

		while (iterMediators.hasNext())
		{
			tmpMediator = iterMediators.next();

			if (tmpMediator != null)
			{
				tmpMediator.updateReceiverSelection(
						eventTrigger, selectionSet);
			} else
			{
				// TODO: print message
			}
		}	
	}
	
	public void removeMediator(IMediatorSender sender) {

		// TODO Auto-generated method stub

	}

	public boolean hasRelation(IMediatorSender sender,
			IMediatorReceiver receiver) {

		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasItem(int iItemId) {

		// TODO Auto-generated method stub
		return false;
	}

	public Object getItem(int iItemId) {

		// TODO Auto-generated method stub
		return null;
	}

	public int size() {

		// TODO Auto-generated method stub
		return 0;
	}

	public boolean registerItem(Object registerItem, int iItemId,
			ManagerObjectType type) {

		// TODO Auto-generated method stub
		return false;
	}

	public boolean unregisterItem(int iItemId, ManagerObjectType type) {

		// TODO Auto-generated method stub
		return false;
	}
	
}
