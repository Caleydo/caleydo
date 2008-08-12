package org.caleydo.core.manager.event.mediator;

import org.caleydo.core.data.selection.ISelectionDelta;


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
	 * Trigger an empty update
	 */
	public void triggerUpdate();
	
	/**
	 * Trigger an update with the selection delta specified
	 * 
	 * @param selectionDelta the selection delta
	 */
	public void triggerUpdate(ISelectionDelta selectionDelta);
	
}
