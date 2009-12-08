package org.caleydo.core.manager.vislink;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;

public class VisLinkSelectionListener
	extends AEventListener<VisLinkManager> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof VisLinkSelectionEvent) {
			VisLinkSelectionEvent vlse = (VisLinkSelectionEvent) event;
			handler.handleVisLinkSelection(vlse.getSelectionId());
		}
	}

}
