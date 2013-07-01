/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.gui.toolbar.action;

import org.caleydo.core.data.selection.ESelectionCommandType;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.event.data.SelectionCommandEvent;
import org.caleydo.core.gui.SimpleAction;
import org.caleydo.core.manager.GeneralManager;

public class ClearSelectionsAction extends SimpleAction {

	public static final String LABEL = "Clear all selections";
	public static final String ICON = "resources/icons/view/tablebased/clear_selections.png";

	public ClearSelectionsAction() {
		super(LABEL, ICON);
	}

	@Override
	public void run() {
		super.run();



		// Was needed for matchmaker that created the selection types
		// dynamically
		// RemoveManagedSelectionTypesEvent resetSelectionTypesEvent = new
		// RemoveManagedSelectionTypesEvent();
		// resetSelectionTypesEvent.setSender(this);
		// GeneralManager.get().getEventPublisher().triggerEvent(resetSelectionTypesEvent);

		SelectionCommand command = new SelectionCommand(ESelectionCommandType.CLEAR);
		command.setSelectionType(SelectionType.SELECTION);
		SelectionCommandEvent commandEvent = new SelectionCommandEvent();
		commandEvent.setSelectionCommand(command);
		GeneralManager.get().getEventPublisher().triggerEvent(commandEvent);

		command = new SelectionCommand(ESelectionCommandType.CLEAR);
		command.setSelectionType(SelectionType.MOUSE_OVER);
		commandEvent = new SelectionCommandEvent();
		commandEvent.setSelectionCommand(command);
		GeneralManager.get().getEventPublisher().triggerEvent(commandEvent);
	}
}
