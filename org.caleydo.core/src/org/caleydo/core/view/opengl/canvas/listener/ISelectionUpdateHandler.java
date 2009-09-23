package org.caleydo.core.view.opengl.canvas.listener;

import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.manager.event.IListenerOwner;

/**
 * Interface for views, mediator or manager classes that needs to get selection-update information.
 * Implementation of this interface are called by {@link SelectionUpdateListener}s.
 * 
 * @author Werner Puff
 */
public interface ISelectionUpdateHandler
	extends IListenerOwner {

	/**
	 * Handler method to be called when a selection event is catched by a related
	 * {@link SelectionUpdateListener}.
	 * 
	 * @param selectionDelta
	 *            difference in the old and new selection
	 * @param scrollToSelection
	 *            tells if the receiver should move/scroll its visible area to the new selection
	 * @param info
	 *            info about the selection (e.g. the name of triggering view to display in the info-box)
	 */
	public void handleSelectionUpdate(ISelectionDelta selectionDelta, boolean scrollToSelection, String info);

}
