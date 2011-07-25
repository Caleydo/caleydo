package org.caleydo.core.manager.event.view.tablebased;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.manager.event.AEvent;

/**
 * Event to signal that the user's selection has been updated. Contains both a selection delta and information
 * about the selection in text form. Also contains information whether to scroll to the selection or not.
 * 
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class SelectionUpdateEvent
	extends AEvent {

	/** delta between old and new selection */
	private SelectionDelta selectionDelta;

	/** tells if the selection should be focused (centered, ...) by the receiver */
	private boolean scrollToSelection = true;

	/** additional information about the selection, e.g. to display in the info-box */
	private String info;

	public SelectionDelta getSelectionDelta() {
		return selectionDelta;
	}

	public void setSelectionDelta(SelectionDelta selectionDelta) {
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
