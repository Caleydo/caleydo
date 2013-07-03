/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.listener;

import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.event.IListenerOwner;

/**
 * Interface for views, mediator or manager classes that needs to get
 * selection-update information. Implementation of this interface are called by
 * {@link ExtendedSelectionUpdateListener}s.
 * 
 * @author Marc Streit
 */
public interface IExtendedSelectionUpdateHandler extends IListenerOwner {

	/**
	 * Handler method to be called when a selection event is caught by a related
	 * {@link ExtendedSelectionUpdateListener}.
	 * 
	 * @param selectionDelta
	 *            difference in the old and new selection
	 * @param scrollToSelection
	 *            tells if the receiver should move/scroll its visible area to
	 *            the new selection
	 * @param info
	 *            info about the selection (e.g. the name of triggering view to
	 *            display in the info-box)
	 */
	public void handleSelectionUpdate(SelectionDelta selectionDelta, String dataDomainID);

}
