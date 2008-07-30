package org.caleydo.core.manager.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.caleydo.core.data.selection.ISelection;
import org.caleydo.core.manager.AManager;
import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.event.mediator.IMediator;
import org.caleydo.core.manager.event.mediator.IMediatorReceiver;
import org.caleydo.core.manager.event.mediator.IMediatorSender;
import org.caleydo.core.manager.event.mediator.LockableExclusivFilterMediator;
import org.caleydo.core.manager.event.mediator.LockableIgnoreFilterMediator;
import org.caleydo.core.manager.event.mediator.LockableMediator;
import org.caleydo.core.manager.event.mediator.MediatorUpdateType;
import org.caleydo.core.manager.type.EManagerType;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;

/**
 * Implements event mediator pattern.
 * 
 * @author Marc Streit
 * @author Michael Kalkusch
 */
public class EventPublisher
	extends AManager
	implements IEventPublisher
{

	protected HashMap<Integer, IMediator> hashMediatorId2Mediator;

	protected HashMap<IMediator, Integer> hashMediatorId2Mediator_reverse;

	protected HashMap<IMediatorSender, ArrayList<IMediator>> hashSender2DataMediators;

	protected HashMap<IMediatorReceiver, ArrayList<IMediator>> hashReceiver2DataMediators;

	protected HashMap<IMediatorSender, ArrayList<IMediator>> hashSender2SelectionMediators;

	protected HashMap<IMediatorReceiver, ArrayList<IMediator>> hashReceiver2SelectionMediators;

	protected HashMap<IMediatorSender, ArrayList<IMediator>> hashSender2ViewMediators;

	protected HashMap<IMediatorReceiver, ArrayList<IMediator>> hashReceiver2ViewMediators;

	/**
	 * Constructor.
	 * 
	 * @param generalManager
	 */
	public EventPublisher(final IGeneralManager generalManager)
	{

		super(generalManager, IGeneralManager.iUniqueId_TypeOffset_EventPublisher,
				EManagerType.EVENT_PUBLISHER);

		/* Data */
		hashSender2DataMediators = new HashMap<IMediatorSender, ArrayList<IMediator>>();
		hashReceiver2DataMediators = new HashMap<IMediatorReceiver, ArrayList<IMediator>>();

		/* Selection */
		hashSender2SelectionMediators = new HashMap<IMediatorSender, ArrayList<IMediator>>();
		hashReceiver2SelectionMediators = new HashMap<IMediatorReceiver, ArrayList<IMediator>>();

		/* View */
		hashReceiver2ViewMediators = new HashMap<IMediatorReceiver, ArrayList<IMediator>>();
		hashSender2ViewMediators = new HashMap<IMediatorSender, ArrayList<IMediator>>();

		hashMediatorId2Mediator = new HashMap<Integer, IMediator>();
		hashMediatorId2Mediator_reverse = new HashMap<IMediator, Integer>();
	}

	private synchronized void insertReceiver(
			HashMap<IMediatorReceiver, ArrayList<IMediator>> insertIntoHashMap,
			IMediatorReceiver receiver, IMediator newMediator)
	{

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
			IMediatorSender sender, IMediator newMediator)
	{

		assert insertIntoHashMap != null : "can not handle insertIntoHashMap null-pointer";
		assert sender != null : "can not handle sender null-pointer";
		assert newMediator != null : "can not handle newMediator null-pointer";

		if (!insertIntoHashMap.containsKey(sender))
		{
			insertIntoHashMap.put(sender, new ArrayList<IMediator>());
		}

		ArrayList<IMediator> bufferArrayList = insertIntoHashMap.get(sender);

		if (bufferArrayList.contains(sender))
		{
			throw new CaleydoRuntimeException("Try to insert an existing object! "
					+ sender.toString() + " ==> " + newMediator.toString() + " inside map"
					+ bufferArrayList.toString(), CaleydoRuntimeExceptionType.OBSERVER);
		}

		bufferArrayList.add(newMediator);

		// //unchecked!
		// insertIntoHashMap.get(sender).add(newMediator);
	}

	public synchronized void createMediator(int iMediatorId, ArrayList<Integer> arSenderIDs,
			ArrayList<Integer> arReceiverIDs, MediatorType mediatorType,
			MediatorUpdateType mediatorUpdateType)
	{

		IMediator newMediator = null;

		if (hashMediatorId2Mediator.containsKey(iMediatorId))
		{
			newMediator = hashMediatorId2Mediator.get(iMediatorId);

			// generalManager.logMsg("createMediator(" + iMediatorId +
			// ") mediator already exists. add Senders and Receivers now.",
			// LoggerType.VERBOSE);
		}
		else
		{ // if ( hashMediatorId2Mediator.containsKey(iMediatorId) ) {..} else

			switch (mediatorUpdateType)
			{
				case MEDIATOR_DEFAULT:
					newMediator = new LockableMediator(generalManager, iMediatorId,
							MediatorUpdateType.MEDIATOR_DEFAULT);
					break;

				case MEDIATOR_FILTER_ONLY_SET:
					newMediator = new LockableExclusivFilterMediator(generalManager,
							iMediatorId, null);
					break;

				case MEDIATOR_FILTER_ALL_EXPECT_SET:
					newMediator = new LockableIgnoreFilterMediator(generalManager,
							iMediatorId, null);
					break;
				default:
					assert false : "unknown type";
					return;
			} // switch (mediatorUpdateType) {

			hashMediatorId2Mediator.put(iMediatorId, newMediator);
			hashMediatorId2Mediator_reverse.put(newMediator, iMediatorId);

		} // if ( hashMediatorId2Mediator.containsKey(iMediatorId) ) {..} else
		// {..}

		// Iterator<Integer> iterSenderIDs = arSenderIDs.iterator();
		//
		// boolean bHasValidSender = false;
		//		
		// // Register sender
		// while (iterSenderIDs.hasNext())
		// {
		// int iCurrentSenderId = iterSenderIDs.next();
		// IMediatorSender sender = null;
		//			
		// try
		// {
		// sender = (IMediatorSender) generalManager
		// .getItem(iCurrentSenderId);
		// }
		// catch ( ClassCastException cce)
		// {
		// singelton.logMsg(
		// "EventPublisher.createMediator() failed because referenced sender object id=["
		// +
		// iCurrentSenderId +
		// "] does not implement interface IMediatorSender " +
		// generalManager.getItem(iCurrentSenderId).getClass(),
		// LoggerType.ERROR);
		//				
		// assert false :
		// "receiver object does not implement interface IMediatorSender";
		// break;
		// }
		//
		// if ( sender == null ) {
		// singelton.logMsg("EventPublisher: invalid SenderId=[" +
		// iCurrentSenderId + "] => receiverId=" +
		// arReceiverIDs.toString() +
		// " ignore sender!",
		// LoggerType.MINOR_ERROR);
		// }
		// else
		// {
		// bHasValidSender = true;
		//				
		// newMediator.register(sender);
		//				
		// switch ( mediatorType ) {
		//				
		// case DATA_MEDIATOR:
		//					
		// //assert false : "test this code!";
		//					
		// // if (!hashSender2DataMediators.containsKey(sender))
		// // {
		// // hashSender2DataMediators.put(sender,
		// // new ArrayList<IMediator>());
		// // }
		// // hashSender2DataMediators.get(sender).add(newMediator);
		//					
		// insertSender(hashSender2DataMediators,sender,newMediator);
		// break;
		//					
		// case SELECTION_MEDIATOR:
		// insertSender(hashSender2SelectionMediators,sender,newMediator);
		//					
		// // if (!hashSender2SelectionMediators.containsKey(sender))
		// // {
		// // hashSender2SelectionMediators.put(sender, new
		// ArrayList<IMediator>());
		// // }
		// // hashSender2SelectionMediators.get(sender).add(newMediator);
		// break;
		//					
		// case VIEW_MEDIATOR:
		// insertSender(hashSender2ViewMediators,sender,newMediator);
		// // if (!hashSender2ViewMediators.containsKey(sender))
		// // {
		// // hashSender2ViewMediators.put(sender, new ArrayList<IMediator>());
		// // }
		// // hashSender2ViewMediators.get(sender).add(newMediator);
		// break;
		//					
		// default:
		// throw new CaleydoRuntimeException(
		// "createMediator() unknown type sender: " +
		// mediatorType.toString(),
		// CaleydoRuntimeExceptionType.OBSERVER);
		//				
		// } //switch ( mediatorType ) {
		//			
		// } //if ( sender == null ) {...} else {..
		//			
		// } //while (iterSenderIDs.hasNext())

		addSendersAndReceiversToMediator(newMediator, arSenderIDs, arReceiverIDs,
				mediatorType, mediatorUpdateType);

		// generalManager.logMsg(
		// "EventPublisher: success registering senderId(s)=[" +
		// arSenderIDs.toString() + "] and receiverId(s)=[" +
		// arReceiverIDs.toString() + "]",
		// LoggerType.VERBOSE);
	}

	public void addSendersAndReceiversToMediator(IMediator newMediator,
			ArrayList<Integer> arSenderIDs, ArrayList<Integer> arReceiverIDs,
			MediatorType mediatorType, MediatorUpdateType mediatorUpdateType)
	{

		assert mediatorType != null : "can not handel no MediatorType (==null)";

		Iterator<Integer> iterSenderIDs = arSenderIDs.iterator();

		boolean bHasValidSender = false;

		// Register sender
		while (iterSenderIDs.hasNext())
		{
			int iCurrentSenderId = iterSenderIDs.next();
			IMediatorSender sender = null;

			try
			{
				sender = (IMediatorSender) generalManager.getViewGLCanvasManager().getItem(
						iCurrentSenderId); // TODO
				// :
				// change
				// to
				// getItem
				// from
				// generalManager
			}
			catch (ClassCastException cce)
			{
				// generalManager.logMsg(
				// "EventPublisher.createMediator() failed because referenced sender object id=["
				// +
				// iCurrentSenderId +
				// "] does not implement interface IMediatorSender " +
				// generalManager.getViewGLCanvasManager().getItem(
				// iCurrentSenderId).getClass(),
				// LoggerType.ERROR);

				assert false : "receiver object does not implement interface IMediatorSender";
				break;
			}

			if (sender == null)
			{
				// generalManager.logMsg("EventPublisher: invalid SenderId=[" +
				// iCurrentSenderId + "] => receiverId=" +
				// arReceiverIDs.toString() +
				// " ignore sender!",
				// LoggerType.MINOR_ERROR);
			}
			else
			{
				bHasValidSender = true;

				newMediator.register(sender);

				switch (mediatorType)
				{

					case DATA_MEDIATOR:

						// assert false : "test this code!";

						// if (!hashSender2DataMediators.containsKey(sender))
						// {
						// hashSender2DataMediators.put(sender,
						// new ArrayList<IMediator>());
						// }
						//hashSender2DataMediators.get(sender).add(newMediator);

						insertSender(hashSender2DataMediators, sender, newMediator);
						break;

					case SELECTION_MEDIATOR:
						insertSender(hashSender2SelectionMediators, sender, newMediator);

						// if
						// (!hashSender2SelectionMediators.containsKey(sender))
						// {
						// hashSender2SelectionMediators.put(sender, new
						// ArrayList<IMediator>());
						// }
						// hashSender2SelectionMediators.get(sender).add(
						// newMediator)
						// ;
						break;

					case VIEW_MEDIATOR:
						insertSender(hashSender2ViewMediators, sender, newMediator);
						// if (!hashSender2ViewMediators.containsKey(sender))
						// {
						// hashSender2ViewMediators.put(sender, new
						// ArrayList<IMediator>());
						// }
						//hashSender2ViewMediators.get(sender).add(newMediator);
						break;

					default:
						throw new CaleydoRuntimeException(
								"createMediator() unknown type sender: "
										+ mediatorType.toString(),
								CaleydoRuntimeExceptionType.OBSERVER);

				} // switch ( mediatorType ) {

			} // if ( sender == null ) {...} else {..

		} // while (iterSenderIDs.hasNext())

		// are there any valid senders?
		if (!bHasValidSender)
		{
			// generalManager.logMsg("EventPublisher: all SenderId(s)=" +
			// arSenderIDs.toString() + " are invalid; ignore all receivers=" +
			// arReceiverIDs.toString() + " also!",
			// LoggerType.MINOR_ERROR);
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
				receiver = (IMediatorReceiver) generalManager.getViewGLCanvasManager()
						.getItem(iCurrentReceiverId); // TODO
				// :
				// change
				// to
				// getItem
				// from
				// generalManager
			}
			catch (ClassCastException cce)
			{
				// generalManager.logMsg(
				// "EventPublisher.createMediator() failed because referenced receiver object id=["
				// +
				// iCurrentReceiverId +
				// "] does not implement interface IMediatorReceiver " +
				// generalManager.getViewGLCanvasManager().getItem(
				// iCurrentReceiverId).getClass(),
				// LoggerType.ERROR);

				assert false : "receiver object does not implement interface IMediatorReceiver";
				break;
			}

			if (receiver == null)
			{
				// generalManager.logMsg("EventPublisher: sender(s) " +
				// arSenderIDs.toString() + " ==> invalid ReceiverId=[" +
				// iCurrentReceiverId + "] ignore receiver!",
				// LoggerType.MINOR_ERROR);
			}
			else
			{
				bHasValidReceiver = true;

				newMediator.register(receiver);

				switch (mediatorType)
				{

					case DATA_MEDIATOR:
						// if
						// (!hashReceiver2DataMediators.containsKey(receiver))
						// {
						// hashReceiver2DataMediators.put(receiver, new
						// ArrayList<IMediator>());
						// }
						// hashReceiver2DataMediators.get(receiver).add(
						// newMediator);

						// assert false : "test this code!";

						insertReceiver(hashReceiver2DataMediators, receiver, newMediator);
						break;

					case SELECTION_MEDIATOR:
						insertReceiver(hashReceiver2SelectionMediators, receiver, newMediator);
						break;

					case VIEW_MEDIATOR:
						insertReceiver(hashReceiver2SelectionMediators, receiver, newMediator);
						break;

					default:
						throw new CaleydoRuntimeException(
								"createMediator() unknown type receiver: "
										+ mediatorType.toString(),
								CaleydoRuntimeExceptionType.OBSERVER);
				} // switch ( mediatorType ) {

				// generalManager.logMsg(
				// "EventPublisher: successful added senderId(s)" +
				// arSenderIDs.toString() + " => [" +
				// iCurrentReceiverId + "]",
				// LoggerType.VERBOSE);

			} // if ( receiver == null ) {...} else {..

		} // while (iterReceiverIDs.hasNext())

		if (!bHasValidReceiver)
		{
			// generalManager.logMsg(
			// "EventPublisher: ignore command with senderId(s)=[" +
			// arSenderIDs.toString() + "] and receiverId(s)=[" +
			// arReceiverIDs.toString() +
			// "] because no valid receiver was found!",
			// LoggerType.MINOR_ERROR);
			return;
		}

		// generalManager.logMsg("EventPublisher: Mediator " +
		// newMediator.toString() +
		// " success registering senderId(s)=[" +
		// arSenderIDs.toString() + "] and receiverId(s)=[" +
		// arReceiverIDs.toString() + "]",
		// LoggerType.VERBOSE);
	}

	public void registerSenderToMediator(int iMediatorId, IMediatorSender sender)
	{

		// TODO Auto-generated method stub

	}

	public void registerSenderToMediator(int iMediatorId, int iMediatorSenderId)
	{

		// TODO Auto-generated method stub

	}

	public void registerReceiverToMediator(int iMediatorId, IMediatorReceiver receiver)
	{

		// TODO Auto-generated method stub

	}

	public void registerReceiverToMediator(int iMediatorId, int iMediatorReceiverId)
	{

		// TODO Auto-generated method stub

	}

	public void unregisterSenderToMediator(int iMediatorId, IMediatorSender sender)
	{

		// TODO Auto-generated method stub

	}

	public void unregisterSenderToMediator(int iMediatorId, int iMediatorSenderId)
	{

		// TODO Auto-generated method stub

	}

	public void unregisterReceiverToMediator(int iMediatorId, IMediatorReceiver receiver)
	{

		// TODO Auto-generated method stub

	}

	public void unregisterReceiverToMediator(int iMediatorId, int iMediatorReceiverId)
	{

		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IEventPublisher#update(java.lang.Object)
	 */
	public void updateReceiver(Object eventTrigger)
	{

		// Prevent update during initialization of data.
		if (hashSender2DataMediators.isEmpty())
		{
			return;
		}

		// Dont't do anything if sender is not registered
		if (!hashSender2DataMediators.containsKey((IMediatorSender) eventTrigger))
		{
			// assert false : "Sender is not registered and calls update(); " +
			// eventTrigger.toString() + "]";
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
			}
			else
			{
				// TODO: print message
				assert false : "Mediator is null while calling update(); "
						+ eventTrigger.toString() + "]";
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.manager.IEventPublisher#updateReceiver(java.lang.Object,
	 * org.caleydo.core.data.collection.ISet)
	 */
	public void updateReceiver(Object eventTrigger, ISelection updatedSet)
	{

		// Prevent update during initialization of data.
		if (hashSender2SelectionMediators.isEmpty())
		{
			return;
		}

		ArrayList<IMediator> arMediators = hashSender2SelectionMediators
				.get((IMediatorSender) eventTrigger);

		if (arMediators == null)
		{
			assert false : "empty (ArrayList) arMediators from hashSender2SelectionMediators.get( eventTrigger)";
			return;
		}

		Iterator<IMediator> iterMediators = arMediators.iterator();

		IMediator tmpMediator;

		while (iterMediators.hasNext())
		{
			tmpMediator = iterMediators.next();

			if (tmpMediator != null)
			{
				tmpMediator.updateReceiver(eventTrigger, updatedSet);
			}
			else
			{
				// TODO: print message
			}
		}
	}

	public void removeMediator(IMediatorSender sender)
	{

		// TODO Auto-generated method stub

	}

	public boolean hasRelation(IMediatorSender sender, IMediatorReceiver receiver)
	{

		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasItem(int iItemId)
	{

		// TODO Auto-generated method stub
		return false;
	}

	public Object getItem(int iItemId)
	{

		return this.hashMediatorId2Mediator.get(iItemId);
	}

	public IMediator getItemMediator(int iItemId)
	{

		return this.hashMediatorId2Mediator.get(iItemId);
	}

	public int size()
	{

		return hashMediatorId2Mediator.size();
	}

	public boolean registerItem(Object registerItem, int iItemId)
	{

		// TODO Auto-generated method stub
		return false;
	}

	public boolean unregisterItem(int iItemId)
	{

		IMediator buffer = hashMediatorId2Mediator.get(iItemId);

		buffer.destroyMediator(this);

		return false;
	}

}
