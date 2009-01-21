package org.caleydo.core.manager.event;

import java.util.Collection;
import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.data.selection.ISelectionDelta;
import org.caleydo.core.data.selection.IVirtualArrayDelta;
import org.caleydo.core.data.selection.SelectionCommand;

/**
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Alexander Lex
 */
public interface IMediator
	extends IUniqueObject
{
	/**
	 * Register a new event sender to the mediator.
	 * 
	 * @param sender new event receiver
	 * @return true, when the instance was added, false when it was already
	 *         registered
	 */
	public boolean addSender(IMediatorSender sender);

	/**
	 * Register a new event receiver to the mediator.
	 * 
	 * @param receiver new event receiver
	 * @return true, when the instance was added, false when it was already
	 *         registered
	 */
	public boolean addReceiver(IMediatorReceiver receiver);

	/**
	 * Remove sender from mediator
	 * 
	 * @param sender the sender to be removed
	 * @return true if the mediator contained the instance
	 */
	public boolean removeSender(IMediatorSender sender);

	/**
	 * Remove receiver from mediator
	 * 
	 * @param receiver the receiver to be removed
	 * @return true if the mediator contained the instance
	 */
	public boolean removeReceiver(IMediatorReceiver receiver);

	/**
	 * Checks whether the instance is registered as a receiver
	 * 
	 * @param receiver the instance to be checked
	 * @return true if already registered
	 */
	public boolean hasReceiver(IMediatorReceiver receiver);

	/**
	 * Checks whether the instance is registered as a sender
	 * 
	 * @param sender the instance to be checked
	 * @return true if already registered
	 */
	public boolean hasSender(IMediatorSender sender);

	/**
	 * Trigger an update concerning selections. The details about what to do
	 * with the update are specified in the delta.
	 * 
	 * @param eventTrigger the caller
	 * @param selectionDelta the delta containing all operations to be executed
	 * @param colSelectionCommand a command to be executed on the selection
	 *            manager (can be null if not necessary)
	 */
	public void triggerUpdate(IUniqueObject eventTrigger, ISelectionDelta selectionDelta,
			Collection<SelectionCommand> colSelectionCommand);

	/**
	 * Trigger an update concerning virtual arrays. The details about what to do
	 * with the update are specified in the delta.
	 * 
	 * @param eventTrigger the caller
	 * @param delta the delta containing all operations to be executed
	 * @param colSelectionCommand a command to be executed on the manager (can
	 *            be null if not necessary)
	 */
	public void triggerVAUpdate(IUniqueObject eventTrigger, IVirtualArrayDelta delta,
			Collection<SelectionCommand> colSelectionCommand);

	/**
	 * Triggers an event, signals that something has happened and sends data
	 * along
	 * 
	 * @param eventTrigger the caller
	 * @param eventContainer containing the information on the type of the event
	 *            {@link EEventType} and possibly data associated
	 */
	public void triggerEvent(IUniqueObject eventTrigger, IEventContainer eventContainer);
}
