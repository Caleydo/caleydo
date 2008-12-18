package org.caleydo.core.manager.event.mediator;

import java.util.Collection;
import org.caleydo.core.data.selection.ISelectionDelta;
import org.caleydo.core.data.selection.SelectionCommand;

/**
 * Interface for a mediator sender
 * 
 * @author Michael Kalkusch
 * @author Alexander Lex
 * @author Marc Streit
 */
public interface IMediatorSender
{
	/**
	 * Trigger an update with the selection delta specified
	 * @param eMediatorType TODO
	 * @param colSelectionCommand TODO
	 */
	public void triggerUpdate(EMediatorType eMediatorType, ISelectionDelta selectionDelta, Collection<SelectionCommand> colSelectionCommand);
}
