package org.caleydo.core.manager.event.mediator;

import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.data.selection.ISelectionDelta;

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
	 */
	public void handleUpdate(IUniqueObject eventTrigger, ISelectionDelta selectionDelta);

}
