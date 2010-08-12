package org.caleydo.core.view.opengl.canvas.listener;

import org.caleydo.core.data.mapping.IDCategory;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.manager.event.IListenerOwner;

/**
 * Interface for classes that handle {@link SelectionCommand}s.
 * 
 * @author Werner Puff
 * @author Alexander Lex
 */
public interface ISelectionCommandHandler
	extends IListenerOwner {

	/**
	 * Handler method to be called when a TriggerSelectionCommand event is caught that should trigger a
	 * content-selection-command by a related. by a related {@link SelectionCommandListener}.
	 * 
	 * @param selectionCommands
	 */
	public void handleSelectionCommand(IDCategory idCategory, SelectionCommand selectionCommand);
}
