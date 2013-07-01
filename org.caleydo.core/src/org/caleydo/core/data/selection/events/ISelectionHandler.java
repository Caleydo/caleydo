/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.selection.events;

import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.event.IListenerOwner;
import org.caleydo.core.id.IDCategory;

/**
 * Interface for views, mediator or manager classes that needs to get selection-update information.
 * Implementation of this interface are called by {@link SelectionUpdateListener}s.
 *
 * @author Werner Puff
 */
public interface ISelectionHandler
	extends IListenerOwner {

	/**
	 * Handler method to be called when a selection event is caught by a related
	 * {@link SelectionUpdateListener}.
	 *
	 * @param selectionDelta
	 *            difference in the old and new selection
	 */
	public void handleSelectionUpdate(SelectionDelta selectionDelta);

	/**
	 * Handler method to be called when a TriggerSelectionCommand event is caught that should trigger a
	 * content-selection-command by a related. by a related {@link SelectionCommandListener}.
	 *
	 * @param selectionCommands
	 */
	public void handleSelectionCommand(IDCategory idCategory, SelectionCommand selectionCommand);

}
