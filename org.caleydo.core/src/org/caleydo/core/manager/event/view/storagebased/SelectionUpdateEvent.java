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
	private ISelectionDelta selectionDelta;

	/** tells if the selection should be focused (centered, ...) by the receiver */
	private boolean scrollToSelection = true;
	
	/**	additional information about the selection, e.g. to display in the info-box */
	private String info;
	
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

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
}
