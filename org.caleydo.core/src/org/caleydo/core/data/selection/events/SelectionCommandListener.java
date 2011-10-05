package org.caleydo.core.data.selection.events;

import org.caleydo.core.data.id.IDCategory;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.view.SelectionCommandEvent;

/**
 * Listener for TriggerSelectionCommand events. This listener gets the payload from a
 * {@link SelectionCommandEvent} and calls a related {@link ISelectionCommandHandler}.
 * 
 * @author Werner Puff
 * @author Alexander Lex
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
			IDCategory idCategory = selectionCommandEvent.getIdCategory();

			handler.handleSelectionCommand(idCategory, selectionCommand);
		}
	}

}
