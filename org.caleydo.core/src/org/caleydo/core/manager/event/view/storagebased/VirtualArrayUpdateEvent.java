package org.caleydo.core.manager.event.view.storagebased;

import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.manager.event.AEvent;

/**
 * Event to signal that ???
 * Migration from EEventType.SELECTION_UPDATE 
 * @author Werner Puff
 */
public class VirtualArrayUpdateEvent
	extends AEvent {
	
	/** delta between old and new selection */
	private ISelectionDelta selectionDelta;

	public ISelectionDelta getSelectionDelta() {
		return selectionDelta;
	}

	public void setSelectionDelta(ISelectionDelta selectionDelta) {
		this.selectionDelta = selectionDelta;
	}
}
