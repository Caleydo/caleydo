package org.caleydo.core.manager.event.view.compare;

import org.caleydo.core.manager.event.AEvent;

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
	public boolean checkIntegrity() {
		return true;
	}

}
