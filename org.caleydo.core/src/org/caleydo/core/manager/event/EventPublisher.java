package org.caleydo.core.manager.event;

import java.util.HashMap;
import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.manager.IEventPublisher;

/**
 * Implementation of {@link IEventPublisher}
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public class EventPublisher
	implements IEventPublisher
{
	private HashMap<EMediatorType, IMediator> hashMediatorType2Mediator;

	/**
	 * Constructor.
	 * 
	 */
	public EventPublisher()
	{
		hashMediatorType2Mediator = new HashMap<EMediatorType, IMediator>();
	}

	@Override
	public IMediator getPrivateMediator()
	{
		return new Mediator();
	}

	@Override
	public void addSender(EMediatorType eMediatorType, IMediatorSender sender)
	{
		// Lazy mediator creation
		if (!hashMediatorType2Mediator.containsKey(eMediatorType))
			hashMediatorType2Mediator.put(eMediatorType, new Mediator(eMediatorType));

		hashMediatorType2Mediator.get(eMediatorType).addSender(sender);

	}

	@Override
	public void addReceiver(EMediatorType eMediatorType, IMediatorReceiver receiver)
	{
		// Lazy mediator creation
		if (!hashMediatorType2Mediator.containsKey(eMediatorType))
			hashMediatorType2Mediator.put(eMediatorType, new Mediator(eMediatorType));

		hashMediatorType2Mediator.get(eMediatorType).addReceiver(receiver);

	}

	// @Override
	// public void triggerSelectionUpdate(EMediatorType eMediatorType,
	// IUniqueObject eventTrigger, ISelectionDelta selectionDelta,
	// Collection<SelectionCommand> colSelectionCommand)
	// {
	// if (!hashMediatorType2Mediator.containsKey(eMediatorType))
	// {
	// if (eMediatorType != EMediatorType.ALL_REGISTERED)
	// throw new IllegalStateException("Sender " + eventTrigger.getID()
	// + " is not a sender in the mediator group " + eMediatorType);
	// }
	// if (eMediatorType == EMediatorType.ALL_REGISTERED)
	// {
	// for (EMediatorType eTempMediatorType :
	// hashMediatorType2Mediator.keySet())
	// {
	// if (hashMediatorType2Mediator.get(eTempMediatorType).hasSender(
	// (IMediatorSender) eventTrigger))
	// {
	// hashMediatorType2Mediator.get(eTempMediatorType).triggerUpdate(
	// eventTrigger, selectionDelta, colSelectionCommand);
	// }
	// }
	// }
	// else
	// {
	// hashMediatorType2Mediator.get(eMediatorType).triggerUpdate(eventTrigger,
	// selectionDelta, colSelectionCommand);
	// }
	// }
	//
	// @Override
	// public void triggerVAUpdate(EMediatorType eMediatorType, IUniqueObject
	// eventTrigger,
	// IVirtualArrayDelta delta, Collection<SelectionCommand>
	// colSelectionCommand)
	// {
	// if (!hashMediatorType2Mediator.containsKey(eMediatorType))
	// {
	// if (eMediatorType != EMediatorType.ALL_REGISTERED)
	// throw new IllegalStateException("Sender " + eventTrigger.getID()
	// + " is not a sender in the mediator group " + eMediatorType);
	// }
	// if (eMediatorType == EMediatorType.ALL_REGISTERED)
	// {
	// for (EMediatorType eTempMediatorType :
	// hashMediatorType2Mediator.keySet())
	// {
	// if (hashMediatorType2Mediator.get(eTempMediatorType).hasSender(
	// (IMediatorSender) eventTrigger))
	// {
	// hashMediatorType2Mediator.get(eTempMediatorType).triggerVAUpdate(
	// eventTrigger, delta, colSelectionCommand);
	// }
	// }
	// }
	// else
	// {
	// hashMediatorType2Mediator.get(eMediatorType).triggerVAUpdate(eventTrigger,
	// delta,
	// colSelectionCommand);
	// }
	// }

	@Override
	public void triggerEvent(EMediatorType eMediatorType, IUniqueObject eventTrigger,
			IEventContainer eventContainer)
	{
		// TODO FIXME add support for ALL etc

		if (!(eventTrigger instanceof IMediatorSender))
		{
			throw new IllegalArgumentException(
					"triggerEvent called by an object which does not implement IMediatorSender");
		}
		for (EMediatorType eTempMediatorType : hashMediatorType2Mediator.keySet())
		{
			IMediator tempMediator = hashMediatorType2Mediator.get(eTempMediatorType);
			if (tempMediator.hasSender((IMediatorSender) eventTrigger))
			{
				tempMediator.triggerEvent(eventTrigger, eventContainer);
			}
		}
	}

	@Override
	public void removeSender(EMediatorType eMediatorType, IMediatorSender sender)
	{
		if (!hashMediatorType2Mediator.containsKey(eMediatorType))
			return;

		hashMediatorType2Mediator.get(eMediatorType).removeSender(sender);
	}

	@Override
	public void removeReceiver(EMediatorType eMediatorType, IMediatorReceiver receiver)
	{
		if (!hashMediatorType2Mediator.containsKey(eMediatorType))
			return;

		hashMediatorType2Mediator.get(eMediatorType).removeReceiver(receiver);
	}

	@Override
	public void removeSenderFromAllGroups(IMediatorSender sender)
	{
		for (IMediator mediator : hashMediatorType2Mediator.values())
		{
			mediator.removeSender(sender);
		}
	}

	@Override
	public void removeReceiverFromAllGroups(IMediatorReceiver receiver)
	{
		for (IMediator mediator : hashMediatorType2Mediator.values())
		{
			mediator.removeReceiver(receiver);
		}
	}
}
