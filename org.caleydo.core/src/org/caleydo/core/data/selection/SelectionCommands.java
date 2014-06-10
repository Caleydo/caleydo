/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.data.selection;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.data.SelectionCommandEvent;
import org.caleydo.core.manager.GeneralManager;

/**
 * Utility class for selection operations.
 * 
 * @author Christian Partl
 *
 */
public final class SelectionCommands {

	private SelectionCommands() {
	}

	public static void clearSelections() {
		SelectionCommand command = new SelectionCommand(ESelectionCommandType.CLEAR);
		command.setSelectionType(SelectionType.SELECTION);
		SelectionCommandEvent commandEvent = new SelectionCommandEvent();
		commandEvent.setSelectionCommand(command);
		
		EventPublisher.trigger(commandEvent);

		command = new SelectionCommand(ESelectionCommandType.CLEAR);
		command.setSelectionType(SelectionType.MOUSE_OVER);
		commandEvent = new SelectionCommandEvent();
		commandEvent.setSelectionCommand(command);
		GeneralManager r1 = GeneralManager.get();
		EventPublisher.trigger(commandEvent);
	}

}
