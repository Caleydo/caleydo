package org.caleydo.view.compare.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.compare.CreateSelectionTypesEvent;
import org.caleydo.view.compare.GLCompare;

public class CreateSelectionTypesListener extends AEventListener<GLCompare> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof CreateSelectionTypesEvent) {
			handler.setCreateSelctionTypes(((CreateSelectionTypesEvent) event)
					.isCreateSelectionTypes());
		}

	}

}
