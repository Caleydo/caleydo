package org.caleydo.core.event.view.matchmaker;

import org.caleydo.core.event.AEvent;

public class CreateSelectionTypesEvent
	extends AEvent {

	private boolean createSelectionTypes;

	public CreateSelectionTypesEvent() {
	}

	public CreateSelectionTypesEvent(boolean createSelectionTypes) {
		this.createSelectionTypes = createSelectionTypes;
	}

	public void setCreateSelectionTypes(boolean createSelectionTypes) {
		this.createSelectionTypes = createSelectionTypes;
	}

	public boolean isCreateSelectionTypes() {
		return createSelectionTypes;
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}

}
