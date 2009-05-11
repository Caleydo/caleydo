package org.caleydo.core.view.opengl.canvas.listener;

import java.util.List;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.manager.event.IListenerOwner;

/**
 * TODO javadoc; what are TriggerSelectionCommands good for?
 * 
 * @author Werner Puff
 */
public interface ITriggerSelectionCommandHandler
	extends IListenerOwner {

	/**
	 * Handler method to be called when a TriggerSelectionCommand event is catched that should trigger a
	 * content-selection-command by a related. by a related {@link TriggerSelectionCommandListener}.
	 * 
	 * @param selectionCommands
	 */
	public void handleContentTriggerSelectionCommand(EIDType type, List<SelectionCommand> selectionCommands);

	/**
	 * Handler method to be called when a TriggerSelectionCommand event is catched that should trigger a
	 * storage-selection-command by a related. by a related {@link TriggerSelectionCommandListener}.
	 * 
	 * @param selectionCommands
	 */
	public void handleStorageTriggerSelectionCommand(EIDType type, List<SelectionCommand> selectionCommands);

}
