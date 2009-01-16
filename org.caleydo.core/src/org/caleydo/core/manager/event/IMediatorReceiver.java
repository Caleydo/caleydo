package org.caleydo.core.manager.event;

import java.util.Collection;
import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.data.selection.ISelectionDelta;
import org.caleydo.core.data.selection.IVirtualArrayDelta;
import org.caleydo.core.data.selection.SelectionCommand;

/**
 * Object that shall receive an event.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public interface IMediatorReceiver
{
	/**
	 * Update called by Mediator triggered by IMediatorSender.
	 * 
	 * @param eventTrigger Calling object, that created the update
	 * @param selectionDelta the differences in the selections
	 * @param colSelectionCommand TODO
	 * @param eMediatorType TODO
	 */
	public void handleUpdate(IUniqueObject eventTrigger, ISelectionDelta selectionDelta,
			Collection<SelectionCommand> colSelectionCommand, EMediatorType eMediatorType);

	/**
	 * Update concerning virtual arrays. The details about what to do with the
	 * update are specified in the delta.
	 * 
	 * @param eMediatorType for which mediator
	 * @param eventTrigger the caller
	 * @param delta the delta containing all operations to be executed
	 * @param colSelectionCommand TODO
	 */
	public void handleVAUpdate(IUniqueObject eventTrigger, IVirtualArrayDelta delta,
			Collection<SelectionCommand> colSelectionCommand, EMediatorType eMediatorType);

}
