package org.caleydo.core.data.selection;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;

public class AddSelectionTypeListener
	extends AEventListener<SelectionManager> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof AddSelectionTypeEvent) {
			AddSelectionTypeEvent addTypeEvent = (AddSelectionTypeEvent) event;
			handler.addSelectionType(addTypeEvent.getSelectionType());
		}
	}

}
