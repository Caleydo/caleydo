package org.caleydo.core.manager.event;

import java.util.Collection;
import org.caleydo.core.data.selection.ISelectionDelta;
import org.caleydo.core.data.selection.IVirtualArrayDelta;
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
	 * 
	 * @param eMediatorType TODO
	 * @param colSelectionCommand TODO
	 */
	public void triggerSelectionUpdate(EMediatorType eMediatorType,
			ISelectionDelta selectionDelta, Collection<SelectionCommand> colSelectionCommand);

	public void triggerVAUpdate(EMediatorType eMediatorType, IVirtualArrayDelta delta,
			Collection<SelectionCommand> colSelectionCommand);
}
