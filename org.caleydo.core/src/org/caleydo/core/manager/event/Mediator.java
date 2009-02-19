package org.caleydo.core.manager.event;

import java.util.ArrayList;
import org.caleydo.core.data.IUniqueObject;

/**
 * Implementation of {@link IMediator}
 * 
 * @author Alexander Lex
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class Mediator
	implements IMediator, IUniqueObject
{
	private EMediatorType eType;

	protected ArrayList<IMediatorReceiver> alReceiver;
	protected ArrayList<IMediatorSender> alSender;

	/**
	 * Constructor.
	 */
	Mediator()
	{
		alReceiver = new ArrayList<IMediatorReceiver>();
		alSender = new ArrayList<IMediatorSender>();
	}

	/**
	 * Constructor.
	 */
	Mediator(EMediatorType eType)
	{
		this();
		this.eType = eType;
	}

	@Override
	public EMediatorType getType()
	{
		return eType;
	}

	@Override
	public final boolean addSender(IMediatorSender sender)
	{
		if (sender == null)
			throw new IllegalArgumentException("Sender to be registered was null");

		if (alSender.contains(sender))
		{
			return false;
		}

		alSender.add(sender);
		return true;
	}

	@Override
	public final boolean addReceiver(IMediatorReceiver receiver)
	{
		if (receiver == null)
			throw new IllegalArgumentException("Receiver to be registered was null");

		if (alReceiver.contains(receiver))
		{
			return false;
		}

		alReceiver.add(receiver);
		return true;
	}

	@Override
	public final boolean removeSender(IMediatorSender sender)
	{
		return alSender.remove(sender);
	}

	@Override
	public final boolean removeReceiver(IMediatorReceiver receiver)
	{
		return alReceiver.remove(receiver);
	}

	@Override
	public final boolean hasReceiver(IMediatorReceiver receiver)
	{
		return alReceiver.contains(receiver);
	}

	@Override
	public final boolean hasSender(IMediatorSender sender)
	{
		return alSender.contains(sender);
	}

	// @Override
	// public void triggerUpdate(IUniqueObject eventTrigger, ISelectionDelta
	// selectionDelta,
	// Collection<SelectionCommand> colSelectionCommand)
	// {
	// for (IMediatorReceiver receiver : alReceiver)
	// {
	// // Prevent circular updates
	// if (!receiver.equals(eventTrigger))
	// {
	// receiver
	// .handleSelectionUpdate(eventTrigger, selectionDelta, colSelectionCommand,
	// eType);
	// }
	// }
	// }

	// @Override
	// public void triggerVAUpdate(IUniqueObject eventTrigger,
	// IVirtualArrayDelta delta,
	// Collection<SelectionCommand> colSelectionCommand)
	// {
	// for (IMediatorReceiver receiver : alReceiver)
	// {
	// // Prevent circular updates
	// if (!receiver.equals(eventTrigger))
	// {
	// receiver.handleVAUpdate(eType, eventTrigger, delta, colSelectionCommand);
	// }
	// }
	// }
	//
	@Override
	public int getID()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void triggerEvent(IUniqueObject eventTrigger, IEventContainer eventContainer)
	{
		for (IMediatorReceiver receiver : alReceiver)
		{
			// Prevent circular updates
			if (!receiver.equals(eventTrigger))
			{
				if (receiver instanceof IMediatorReceiver)
				{
					IMediatorReceiver eventReceiver = (IMediatorReceiver) receiver;
					eventReceiver.handleExternalEvent(eventTrigger, eventContainer, eType);
				}
			}
		}

	}
}
