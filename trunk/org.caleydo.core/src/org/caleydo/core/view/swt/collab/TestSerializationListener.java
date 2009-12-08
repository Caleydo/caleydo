package org.caleydo.core.view.swt.collab;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;

public class TestSerializationListener
	extends AEventListener<CollabViewRep> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof TestSerializationEvent) {
			TestSerializationEvent testSerializationEvent = (TestSerializationEvent) event;
			handler.setSerializationText(testSerializationEvent.getSerializedText());
		}
	}

}
