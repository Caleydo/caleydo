package org.caleydo.core.manager.event.view.storagebased;

import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.manager.event.AEvent;

/**
 * Event to signal that the user's selection has been updated.
 * Migration from EEventType.SELECTION_UPDATE 
 * @author Werner Puff
 */
public class SelectionUpdateEvent
	extends AEvent {
	
	/** delta between old and new selection */
	ISelectionDelta selectionDelta;

	/** tells if the selection should be focused (centered, ...) by the receiver */
	boolean scrollToSelection = true;
	
	public ISelectionDelta getSelectionDelta() {
		return selectionDelta;
	}

	public void setSelectionDelta(ISelectionDelta selectionDelta) {
		this.selectionDelta = selectionDelta;
	}

	public boolean isScrollToSelection() {
		return scrollToSelection;
	}

	public void setScrollToSelection(boolean scrollToSelection) {
		this.scrollToSelection = scrollToSelection;
	}
}
