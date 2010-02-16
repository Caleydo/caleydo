package org.caleydo.core.data.selection;

import org.caleydo.core.manager.event.AEvent;

/**
 * Create or remove a selection type
 * 
 * @author alexsb
 */
public class SelectionTypeEvent
	extends AEvent {

	private SelectionType selectionType;
	private boolean isRemove = false;

	public SelectionTypeEvent() {
	}

	public SelectionTypeEvent(SelectionType selectionType) {
		this.selectionType = selectionType;
	}

	public void addSelectionType(SelectionType selectionType) {
		this.selectionType = selectionType;
	}

	public SelectionType getSelectionType() {
		return selectionType;
	}

	public boolean isRemove() {
		return isRemove;
	}

	public void setRemove(boolean isRemove) {
		this.isRemove = isRemove;
	}

	@Override
	public boolean checkIntegrity() {
		if (selectionType == null)
			return false;
		if (SelectionType.isDefaultType(selectionType)) {
			throw new IllegalArgumentException("Can not add or remove default types");
		}
		return true;
	}

}
