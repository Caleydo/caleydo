package org.caleydo.core.view.opengl.canvas.listener;

import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.SelectionCommandEvent;

/**
 * Listener for TriggerSelectionCommand events. This listener gets the payload from a
 * {@link SelectionCommandEvent} and calls a related {@link ISelectionCommandHandler}.
 * 
 * @author Werner Puff
 */
public class SelectionCommandListener
	extends AEventListener<ISelectionCommandHandler> {

	/**
	 * Handles {@link SelectionCommandEvent}s by extracting the events payload and calling the related handler
	 * 
	 * @param event
	 *            {@link SelectionCommandEvent} to handle, other events will be ignored
	 */
	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof SelectionCommandEvent) {
			SelectionCommandEvent selectionCommandEvent = (SelectionCommandEvent) event;
			SelectionCommand selectionCommand = selectionCommandEvent.getSelectionCommand();
			EIDCategory category = selectionCommandEvent.getCategory();

			handler.handleSelectionCommand(category, selectionCommand);
		}
	}

}
