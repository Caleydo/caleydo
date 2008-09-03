package org.caleydo.core.manager.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.data.selection.ISelectionDelta;
import org.caleydo.core.manager.AManager;
import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.event.mediator.EMediatorType;
import org.caleydo.core.manager.event.mediator.EMediatorUpdateType;
import org.caleydo.core.manager.event.mediator.IMediator;
import org.caleydo.core.manager.event.mediator.IMediatorReceiver;
import org.caleydo.core.manager.event.mediator.IMediatorSender;
import org.caleydo.core.manager.event.mediator.LockableMediator;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;

/**
 * Implements event mediator pattern.
 * 
 * @author Marc Streit
 * @author Michael Kalkusch
 */
public class EventPublisher
	extends AManager<IMediator>
	implements IEventPublisher
{	
	protected HashMap<IMediatorSender, ArrayList<IMediator>> hashSender2DataMediators;
	protected HashMap<IMediatorReceiver, ArrayList<IMediator>> hashReceiver2DataMediators;
	protected HashMap<IMediatorSender, ArrayList<IMediator>> hashSender2SelectionMediators;
	protected HashMap<IMediatorReceiver, ArrayList<IMediator>> hashReceiver2SelectionMediators;
	protected HashMap<IMediatorSender, ArrayList<IMediator>> hashSender2ViewMediators;
	protected HashMap<IMediatorReceiver, ArrayList<IMediator>> hashReceiver2ViewMediators;

	/**
	 * Constructor.
	 * 
	 */
	public EventPublisher()
	{
		hashSender2DataMediators = new HashMap<IMediatorSender, ArrayList<IMediator>>();
		hashReceiver2DataMediators = new HashMap<IMediatorReceiver, ArrayList<IMediator>>();

		hashSender2SelectionMediators = new HashMap<IMediatorSender, ArrayList<IMediator>>();
		hashReceiver2SelectionMediators = new HashMap<IMediatorReceiver, ArrayList<IMediator>>();

		hashReceiver2ViewMediators = new HashMap<IMediatorReceiver, ArrayList<IMediator>>();
		hashSender2ViewMediators = new HashMap<IMediatorSender, ArrayList<IMediator>>();
	}

	private synchronized void insertReceiver(
			HashMap<IMediatorReceiver, ArrayList<IMediator>> insertIntoHashMap,
			IMediatorReceiver receiver, IMediator newMediator)
	{
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
	}

	public synchronized IMediator createMediator(ArrayList<Integer> arSenderIDs,
			ArrayList<Integer> arReceiverIDs, EMediatorType mediatorType,
			EMediatorUpdateType mediatorUpdateType)
	{
		IMediator newMediator = null;

		switch (mediatorUpdateType)
		{
			case MEDIATOR_DEFAULT:
				newMediator = new LockableMediator(mediatorType, mediatorUpdateType);
				break;
			// case MEDIATOR_FILTER_ONLY_SET:
			// newMediator = new LockableExclusivFilterMediator(null);
			// break;
			//
			// case MEDIATOR_FILTER_ALL_EXPECT_SET:
			// newMediator = new LockableIgnoreFilterMediator(null);
			// break;
			default:
				throw new CaleydoRuntimeException("Unknown mediator type " + mediatorUpdateType,
						CaleydoRuntimeExceptionType.EVENT);
		}

		registerItem(newMediator);

		addSendersAndReceiversToMediator(newMediator, arSenderIDs, arReceiverIDs,
				mediatorType, mediatorUpdateType);

		return newMediator;
	}

	public void addSendersAndReceiversToMediator(IMediator newMediator,
			ArrayList<Integer> arSenderIDs, ArrayList<Integer> arReceiverIDs,
			EMediatorType mediatorType, EMediatorUpdateType mediatorUpdateType)
	{
		Iterator<Integer> iterSenderIDs = arSenderIDs.iterator();

		// Register sender
		while (iterSenderIDs.hasNext())
		{
			int iCurrentSenderId = iterSenderIDs.next();
			IMediatorSender sender = (IMediatorSender) GeneralManager.get()
					.getViewGLCanvasManager().getGLEventListener(iCurrentSenderId);

			if (sender == null)
			{
				sender = (IMediatorSender) GeneralManager.get().getViewGLCanvasManager()
						.getItem(iCurrentSenderId);
			}

			newMediator.register(sender);

			switch (mediatorType)
			{

				case DATA_MEDIATOR:
					insertSender(hashSender2DataMediators, sender, newMediator);
					break;

				case SELECTION_MEDIATOR:
					insertSender(hashSender2SelectionMediators, sender, newMediator);
					break;

				case VIEW_MEDIATOR:
					insertSender(hashSender2ViewMediators, sender, newMediator);
					break;

				default:
					throw new CaleydoRuntimeException("createMediator() unknown type sender: "
							+ mediatorType.toString(), CaleydoRuntimeExceptionType.OBSERVER);

			}
		}

		Iterator<Integer> iterReceiverIDs = arReceiverIDs.iterator();

		// Register receiver
		while (iterReceiverIDs.hasNext())
		{
			int iCurrentReceiverId = iterReceiverIDs.next();

			IMediatorReceiver receiver = null;

			receiver = (IMediatorReceiver) GeneralManager.get().getViewGLCanvasManager()
					.getGLEventListener(iCurrentReceiverId);

			newMediator.register(receiver);

			switch (mediatorType)
			{
				case DATA_MEDIATOR:
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
			}
		}
	}

	public void registerSenderToMediatorGroup(EMediatorType mediatorType, 
			IMediatorSender sender)
	{
		for (IMediator mediator : hashItems.values())
		{
			switch(mediatorType)
			{
				case DATA_MEDIATOR:
					mediator.register(sender);
					break;
				case SELECTION_MEDIATOR:
					mediator.register(sender);
					break;
				case VIEW_MEDIATOR:
					mediator.register(sender);
					break;
			}
		}
	}
	
	public void registerReceiverToMediatorGroup(EMediatorType mediatorType, 
			IMediatorReceiver receiver)
	{
		for (IMediator mediator : hashItems.values())
		{
			switch(mediatorType)
			{
				case DATA_MEDIATOR:
					mediator.register(receiver);
					break;
				case SELECTION_MEDIATOR:
					mediator.register(receiver);
					break;
				case VIEW_MEDIATOR:
					mediator.register(receiver);
					break;
			}
		}
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

	@Override
	public void handleUpdate(IUniqueObject eventTrigger)
	{

		// Prevent update during initialization of data.
		if (hashSender2DataMediators.isEmpty())
		{
			return;
		}

		// Dont't do anything if sender is not registered
		if (!hashSender2DataMediators.containsKey(eventTrigger))
		{
			// assert false : "Sender is not registered and calls update(); " +
			// eventTrigger.toString() + "]";
			/* work around! */

			return;
		}

		ArrayList<IMediator> arMediators = hashSender2DataMediators.get(eventTrigger);

		Iterator<IMediator> iterMediators = arMediators.iterator();

		IMediator tmpMediator;

		while (iterMediators.hasNext())
		{
			tmpMediator = iterMediators.next();

			if (tmpMediator != null)
			{
				tmpMediator.handleUpdate(eventTrigger);
			}
			else
			{
				// TODO: print message
				assert false : "Mediator is null while calling update(); "
						+ eventTrigger.toString() + "]";
			}
		}
	}

	@Override
	public void handleUpdate(IUniqueObject eventTrigger, ISelectionDelta selectionDelta)
	{

		// Prevent update during initialization of data.
		if (hashSender2SelectionMediators.isEmpty())
		{
			return;
		}

		ArrayList<IMediator> arMediators = hashSender2SelectionMediators.get(eventTrigger);

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
				tmpMediator.handleUpdate(eventTrigger, selectionDelta);
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
}
