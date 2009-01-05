package org.caleydo.core.manager;

import java.util.Collection;
import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.data.selection.ISelectionDelta;
import org.caleydo.core.data.selection.IVirtualArrayDelta;
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
	/**
	 * Trigger an update concerning selections. The details about what to do
	 * with the update are specified in the delta.
	 * 
	 * @param eMediatorType for which mediator
	 * @param eventTrigger the caller
	 * @param selectionDelta the delta containing all operations to be executed
	 * @param colSelectionCommand a command to be executed on the selection
	 *            manager (can be null if not necessary)
	 */
	public void triggerUpdate(EMediatorType eMediatorType, IUniqueObject eventTrigger,
			ISelectionDelta selectionDelta, Collection<SelectionCommand> colSelectionCommand);

	/**
	 * Trigger an update concerning virtual arrays. The details about what to do
	 * with the update are specified in the delta.
	 * 
	 * @param eMediatorType for which mediator
	 * @param eventTrigger the caller
	 * @param delta the delta containing all operations to be executed
	 */
	public void triggerVAUpdate(EMediatorType eMediatorType, IUniqueObject eventTrigger,
			IVirtualArrayDelta delta);

	/**
	 * Trigger an event, signaling that something has happened
	 * 
	 * TODO: interface is only rudimentary 
	 * 
	 * @param eventTrigger
	 * @param iID
	 */
	public void triggerEvent(IUniqueObject eventTrigger, int iID);

	public void addSender(EMediatorType eMediatorType, IMediatorSender sender);

	public void addReceiver(EMediatorType eMediatorType, IMediatorReceiver receiver);

	public void removeSender(EMediatorType eMediatorType, IMediatorSender sender);

	public void removeReceiver(EMediatorType eMediatorType, IMediatorReceiver receiver);

	public void removeSenderFromAllGroups(IMediatorSender sender);

	public void removeReceiverFromAllGroups(IMediatorReceiver receiver);
}
