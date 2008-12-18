package org.caleydo.core.manager.event.mediator;

import java.util.Collection;
import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.data.selection.ISelectionDelta;
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

	public void triggerUpdate(IUniqueObject eventTrigger, ISelectionDelta selectionDelta,
			Collection<SelectionCommand> colSelectionCommand);
}
