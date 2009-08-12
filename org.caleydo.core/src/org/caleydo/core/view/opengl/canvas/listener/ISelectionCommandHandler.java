package org.caleydo.core.view.opengl.canvas.listener;

import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.manager.event.IListenerOwner;

/**
 * TODO javadoc; what are TriggerSelectionCommands good for?
 * 
 * @author Werner Puff
 */
public interface ISelectionCommandHandler
	extends IListenerOwner {

	/**
	 * Handler method to be called when a TriggerSelectionCommand event is caught that should trigger a
	 * content-selection-command by a related. by a related {@link SelectionCommandListener}.
	 * 
	 * @param selectionCommands
	 */
	public void handleContentTriggerSelectionCommand(EIDCategory category, SelectionCommand selectionCommand);

	/**
	 * Handler method to be called when a TriggerSelectionCommand event is caught that should trigger a
	 * storage-selection-command by a related. by a related {@link SelectionCommandListener}.
	 * 
	 * @param selectionCommands
	 */
	public void handleStorageTriggerSelectionCommand(EIDCategory category, SelectionCommand selectionCommand);

}
