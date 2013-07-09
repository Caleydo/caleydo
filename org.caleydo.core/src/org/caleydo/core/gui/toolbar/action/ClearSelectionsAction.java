/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.gui.toolbar.action;

import org.caleydo.core.data.selection.SelectionCommands;
import org.caleydo.core.gui.SimpleAction;

public class ClearSelectionsAction extends SimpleAction {

	public static final String LABEL = "Clear Selections";
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

		SelectionCommands.clearSelections();
	}
}
