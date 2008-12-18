package org.caleydo.core.manager;

import java.util.Collection;
import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.data.selection.ISelectionDelta;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.manager.event.mediator.EMediatorType;
import org.caleydo.core.manager.event.mediator.IMediator;
import org.caleydo.core.manager.event.mediator.IMediatorReceiver;
import org.caleydo.core.manager.event.mediator.IMediatorSender;

/**
 * Handle events using Publish subscriber design pattern.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public interface IEventPublisher
	extends IManager<IMediator>
{
	public void triggerUpdate(EMediatorType eMediatorType,
			IUniqueObject eventTrigger, ISelectionDelta selectionDelta, Collection<SelectionCommand> colSelectionCommand);
	
	public void addSender(EMediatorType eMediatorType, IMediatorSender sender);
	
	public void addReceiver(EMediatorType eMediatorType, IMediatorReceiver receiver);

	public void removeSender(EMediatorType eMediatorType, IMediatorSender sender);
	
	public void removeReceiver(EMediatorType eMediatorType, IMediatorReceiver receiver);
	
	public void removeSenderFromAllGroups(IMediatorSender sender);
	
	public void removeReceiverFromAllGroups(IMediatorReceiver receiver);
}
