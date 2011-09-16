package org.caleydo.core.data.selection;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.view.RemoveManagedSelectionTypesEvent;

public class RemoveManagedSelectionTypesListener
	extends AEventListener<SelectionManager> {

	@Override
	public void handleEvent(AEvent event) {

		if (event instanceof RemoveManagedSelectionTypesEvent) {
			handler.removeMangagedSelectionTypes();
		}
	}

}
