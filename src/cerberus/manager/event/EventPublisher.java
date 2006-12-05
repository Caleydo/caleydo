package cerberus.manager.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import cerberus.data.collection.selection.ISelectionSet;
import cerberus.manager.IEventPublisher;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ISingelton;
import cerberus.manager.event.mediator.IMediator;
import cerberus.manager.event.mediator.IMediatorReceiver;
import cerberus.manager.event.mediator.IMediatorSender;
import cerberus.manager.event.mediator.LockableMediator;
import cerberus.manager.type.ManagerObjectType;

public class EventPublisher 
implements IEventPublisher {
	
	protected IGeneralManager refGeneralManager;

	protected HashMap<Integer, IMediator> hashMediatorId2Mediator;

	protected HashMap<IMediatorSender, ArrayList<IMediator>> hashSender2DataMediators;

	protected HashMap<IMediatorReceiver, ArrayList<IMediator>> hashReceiver2DataMediators;

	protected HashMap<IMediatorSender, ArrayList<IMediator>> hashSender2SelectionMediators;

	protected HashMap<IMediatorReceiver, ArrayList<IMediator>> hashReceiver2SelectionMediators;

	/**
	 * Constructor.
	 * 
	 * @param refGeneralManager
	 */
	public EventPublisher(IGeneralManager refGeneralManager) {

		this.refGeneralManager = refGeneralManager;

		hashSender2DataMediators = 
			new HashMap<IMediatorSender, ArrayList<IMediator>>();
		hashReceiver2DataMediators = 
			new HashMap<IMediatorReceiver, ArrayList<IMediator>>();
		hashSender2SelectionMediators = 
			new HashMap<IMediatorSender, ArrayList<IMediator>>();
		hashReceiver2SelectionMediators = 
			new HashMap<IMediatorReceiver, ArrayList<IMediator>>();
	}

	public void createMediator(int iMediatorId, ArrayList<Integer> arSenderIDs,
			ArrayList<Integer> arReceiverIDs, MediatorType mediatorType) {

		IMediator newMediator = new LockableMediator(iMediatorId);

		Iterator<Integer> iterSenderIDs = arSenderIDs.iterator();
		Iterator<Integer> iterReceiverIDs = arReceiverIDs.iterator();

		// Register sender
		while (iterSenderIDs.hasNext())
		{
			IMediatorSender sender = (IMediatorSender) refGeneralManager
					.getItem(iterSenderIDs.next());

			newMediator.register(sender);
			
			if (mediatorType == MediatorType.DATA_MEDIATOR)
			{
				if (!hashSender2DataMediators.containsKey(sender))
				{
					hashSender2DataMediators.put(sender, new ArrayList<IMediator>());
				}
				hashSender2DataMediators.get(sender).add(newMediator);
			}
			else if (mediatorType == MediatorType.SELECTION_MEDIATOR)
			{
				if (!hashSender2SelectionMediators.containsKey(sender))
				{
					hashSender2SelectionMediators.put(sender, new ArrayList<IMediator>());
				}
				hashSender2SelectionMediators.get(sender).add(newMediator);
			}
		}

		// Register receiver
		while (iterReceiverIDs.hasNext())
		{
			IMediatorReceiver receiver = (IMediatorReceiver) refGeneralManager
					.getItem(iterReceiverIDs.next());

			newMediator.register(receiver);

			if (mediatorType == MediatorType.DATA_MEDIATOR)
			{
				if (!hashReceiver2DataMediators.containsKey(receiver))
				{
					hashReceiver2DataMediators.put(receiver, new ArrayList<IMediator>());
				}
				hashReceiver2DataMediators.get(receiver).add(newMediator);
			}
			else if (mediatorType == MediatorType.SELECTION_MEDIATOR)
			{
				if (!hashReceiver2SelectionMediators.containsKey(receiver))
				{
					hashReceiver2SelectionMediators.put(receiver, new ArrayList<IMediator>());
				}
				hashReceiver2SelectionMediators.get(receiver).add(newMediator);				
			}
		}
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
	public void updateSelection(Object eventTrigger, ISelectionSet selectionSet) {
		
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

	public ManagerObjectType getManagerType() {

		// TODO Auto-generated method stub
		return null;
	}

	public IGeneralManager getGeneralManager() {

		// TODO Auto-generated method stub
		return null;
	}

	public ISingelton getSingelton() {

		// TODO Auto-generated method stub
		return null;
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

	public int createNewId(ManagerObjectType setNewBaseType) {

		// TODO Auto-generated method stub
		return 0;
	}

	public IGeneralManager getManagerByBaseType(ManagerObjectType managerType) {

		// TODO Auto-generated method stub
		return null;
	}
}
