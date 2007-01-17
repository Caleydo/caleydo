package cerberus.manager.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import cerberus.data.collection.ISet;
import cerberus.manager.IEventPublisher;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ISingelton;
import cerberus.manager.base.AAbstractManager;
import cerberus.manager.event.mediator.IMediator;
import cerberus.manager.event.mediator.IMediatorReceiver;
import cerberus.manager.event.mediator.IMediatorSender;
import cerberus.manager.event.mediator.LockableMediator;
import cerberus.manager.type.ManagerObjectType;
import cerberus.manager.type.ManagerType;

import cerberus.util.exception.CerberusExceptionType;
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
	public EventPublisher(IGeneralManager refGeneralManager) {

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

	private void insertReceiver( HashMap<IMediatorReceiver, ArrayList<IMediator>> insertIntoHashMap, 
			IMediatorReceiver receiver,
			IMediator newMediator ) {
	
		if (!insertIntoHashMap.containsKey(receiver))
		{
			insertIntoHashMap.put(receiver, new ArrayList<IMediator>());
		}
		insertIntoHashMap.get(receiver).add(newMediator);	
	}
	
	private void insertSender( HashMap<IMediatorSender, ArrayList<IMediator>> insertIntoHashMap, 
			IMediatorSender sender,
			IMediator newMediator ) {
	
		if (!insertIntoHashMap.containsKey(sender))
		{
			insertIntoHashMap.put(sender, new ArrayList<IMediator>());
		}
		
		
		ArrayList <IMediator> bufferArrayList = insertIntoHashMap.get(sender);
		
		if ( bufferArrayList.contains( sender ) ) {
			throw new CerberusRuntimeException("Try to insert an existing object! " + 
					sender.toString() + " ==> " + newMediator.toString() + 
					" inside map" + bufferArrayList.toString() );
		}
		
		bufferArrayList.add(newMediator);
		
//		//unchecked!
//		insertIntoHashMap.get(sender).add(newMediator);
	}
	
	public void createMediator(int iMediatorId, 
			ArrayList<Integer> arSenderIDs,
			ArrayList<Integer> arReceiverIDs, 
			MediatorType mediatorType) {

		IMediator newMediator = new LockableMediator(iMediatorId);

		Iterator<Integer> iterSenderIDs = arSenderIDs.iterator();
		Iterator<Integer> iterReceiverIDs = arReceiverIDs.iterator();

		// Register sender
		while (iterSenderIDs.hasNext())
		{
			IMediatorSender sender = (IMediatorSender) refGeneralManager
					.getItem(iterSenderIDs.next());

			newMediator.register(sender);
			
			switch ( mediatorType ) {
			
			case DATA_MEDIATOR:
				
				//assert false : "test this code!";
				
				if (!hashSender2DataMediators.containsKey(sender))
				{
					hashSender2DataMediators.put(sender, new ArrayList<IMediator>());
				}
				hashSender2DataMediators.get(sender).add(newMediator);
				
				insertSender(hashSender2DataMediators,sender,newMediator);
				break;
				
			case SELECTION_MEDIATOR:
				insertSender(hashSender2SelectionMediators,sender,newMediator);
				
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
				throw new CerberusRuntimeException("createMediator() unknown type sender: " + mediatorType.toString() );
			
			} //switch ( mediatorType ) {
			
		} //while (iterSenderIDs.hasNext())

		// Register receiver
		while (iterReceiverIDs.hasNext())
		{
			IMediatorReceiver receiver = (IMediatorReceiver) refGeneralManager
					.getItem(iterReceiverIDs.next());

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
					throw new CerberusRuntimeException("createMediator() unknown type receiver: " + mediatorType.toString() );
			} // switch ( mediatorType ) {
			
		} // while (iterReceiverIDs.hasNext())
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
			return;
		
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
