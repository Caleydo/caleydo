package org.caleydo.core.data.selection;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.RemoveManagedSelectionTypesEvent;

public class RemoveManagedSelectionTypesListener
	extends AEventListener<SelectionManager> {

	@Override
	public void handleEvent(AEvent event) {

		if (event instanceof RemoveManagedSelectionTypesEvent) {
			handler.removeMangagedSelectionTypes();
		}
	}

}
