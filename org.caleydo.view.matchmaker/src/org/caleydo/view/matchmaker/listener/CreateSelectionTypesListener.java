package org.caleydo.view.matchmaker.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.matchmaker.CreateSelectionTypesEvent;
import org.caleydo.view.matchmaker.GLMatchmaker;

public class CreateSelectionTypesListener extends AEventListener<GLMatchmaker> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof CreateSelectionTypesEvent) {
			handler.setCreateSelctionTypes(((CreateSelectionTypesEvent) event)
					.isCreateSelectionTypes());
		}

	}

}
