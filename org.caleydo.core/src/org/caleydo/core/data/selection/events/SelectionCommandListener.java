/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.selection.events;

import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.data.SelectionCommandEvent;
import org.caleydo.core.id.IDCategory;

/**
 * Listener for TriggerSelectionCommand events. This listener gets the payload from a
 * {@link SelectionCommandEvent} and calls a related {@link ISelectionCommandHandler}.
 *
 * @author Werner Puff
 * @author Alexander Lex
 */
public class SelectionCommandListener
 extends AEventListener<ISelectionHandler> {

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
