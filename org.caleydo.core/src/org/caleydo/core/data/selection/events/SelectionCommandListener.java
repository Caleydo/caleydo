/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
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
