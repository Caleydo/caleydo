package org.caleydo.core.manager.event.mediator;

import java.util.Collection;
import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.data.selection.ISelectionDelta;
import org.caleydo.core.data.selection.IVirtualArrayDelta;
import org.caleydo.core.data.selection.SelectionCommand;

/**
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public interface IMediator
	extends IUniqueObject
{
	/**
	 * Register a new event sender to the mediator.
	 * 
	 * @param sender new event sender
	 * @return TRUE on success
	 */
	public boolean register(IMediatorSender sender);

	/**
	 * Register a new event receiver to the mediator.
	 * 
	 * @param receiver new event receiver
	 * @return TRUE on success
	 */
	public boolean register(IMediatorReceiver receiver);

	/**
	 * Unregister sender. If it is last reference to Mediator, it removes the
	 * mediator from the
	 * 
	 * @param sender
	 */
	public boolean unregister(IMediatorSender sender);

	public boolean unregister(IMediatorReceiver receiver);

	public boolean hasReceiver(IMediatorReceiver receiver);

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
	 */
	public void triggerVAUpdate(IUniqueObject eventTrigger,
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
}
