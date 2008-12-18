package org.caleydo.core.manager.event.mediator;

import java.util.Collection;
import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.data.selection.ISelectionDelta;
import org.caleydo.core.data.selection.SelectionCommand;

/**
 * Object that shall receive an event.
 * 
 * @author Michael Kalkusch
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
	public void handleUpdate(IUniqueObject eventTrigger, ISelectionDelta selectionDelta, Collection<SelectionCommand> colSelectionCommand, EMediatorType eMediatorType);

}
