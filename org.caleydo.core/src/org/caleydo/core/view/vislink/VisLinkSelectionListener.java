package org.caleydo.core.view.vislink;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;

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
