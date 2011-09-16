package org.caleydo.core.view.swt.collab;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;

public class TestSerializationListener
	extends AEventListener<CollabView> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof TestSerializationEvent) {
			TestSerializationEvent testSerializationEvent = (TestSerializationEvent) event;
			handler.setSerializationText(testSerializationEvent.getSerializedText());
		}
	}

}
