package org.caleydo.core.manager.event.view.storagebased;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.manager.event.AEvent;

/**
 * Event to signal that the user's selection has been updated. Migration from EEventType.SELECTION_UPDATE
 * 
 * @author Werner Puff
 */
@XmlRootElement(name = "selectionUpdateEvent")
@XmlType(name = "SelectionUpdateEvent")
public class SelectionUpdateEvent
	extends AEvent {

	/** delta between old and new selection */
	private ISelectionDelta selectionDelta;

	/** tells if the selection should be focused (centered, ...) by the receiver */
	private boolean scrollToSelection = true;

	/** additional information about the selection, e.g. to display in the info-box */
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

	@Override
	public boolean checkIntegrity() {
		if (selectionDelta == null)
			throw new NullPointerException("selectionDelta was null");
		return true;
	}
}
