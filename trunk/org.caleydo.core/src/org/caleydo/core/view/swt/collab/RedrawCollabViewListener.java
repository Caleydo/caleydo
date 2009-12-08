package org.caleydo.core.view.swt.collab;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;

public class RedrawCollabViewListener
	extends AEventListener<CollabViewRep> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof RedrawCollabViewEvent) {
			handler.drawView();
		}
	}

}
