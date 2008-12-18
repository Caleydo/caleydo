package org.caleydo.core.manager.event.mediator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.data.selection.ISelectionDelta;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.manager.general.GeneralManager;

/**
 * Attention: Since Mediator is also a IMediatorReceiver care has to be taken
 * when registering a Mediator as Receiver.
 * 
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
	public Mediator(EMediatorType eType)
	{
		this.eType = eType;
		
		alReceiver = new ArrayList<IMediatorReceiver>();
		alSender = new ArrayList<IMediatorSender>();
	}

	@Override
	public final boolean register(IMediatorSender sender)
	{
		if (alSender.contains(sender))
		{
			return false;
		}

		alSender.add(sender);
		return true;
	}

	@Override
	public final boolean register(IMediatorReceiver receiver)
	{
		if (alReceiver.contains(receiver))
		{
			return false;
		}

		alReceiver.add(receiver);
		return true;
	}

	@Override
	public final boolean unregister(IMediatorSender sender)
	{
		return alSender.remove(sender);
	}

	@Override
	public final boolean unregister(IMediatorReceiver receiver)
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

	@Override
	public void triggerUpdate(IUniqueObject eventTrigger, 
			ISelectionDelta selectionDelta, Collection<SelectionCommand> colSelectionCommand)
	{
		for (IMediatorReceiver receiver : alReceiver)
		{
			// Prevent circular updates
			if (!receiver.equals(eventTrigger))
			{
				receiver.handleUpdate(eventTrigger, selectionDelta, colSelectionCommand, eType);
			}
		}
	}

	@Override
	public int getID()
	{
		// TODO Auto-generated method stub
		return 0;
	}
}
