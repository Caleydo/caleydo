package org.caleydo.core.data.selection;

import org.caleydo.core.manager.event.AEvent;

public class AddSelectionTypeEvent
	extends AEvent {

	private SelectionType selectionType;

	public AddSelectionTypeEvent() {
	}

	public AddSelectionTypeEvent(SelectionType selectionType) {
		this.selectionType = selectionType;
	}

	public void addSelectionType(SelectionType selectionType) {
		this.selectionType = selectionType;
	}

	public SelectionType getSelectionType() {
		return selectionType;
	}

	@Override
	public boolean checkIntegrity() {
		if (selectionType == null)
			return false;
		return true;
	}

}
