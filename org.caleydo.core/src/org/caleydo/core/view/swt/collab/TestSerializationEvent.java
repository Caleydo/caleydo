package org.caleydo.core.view.swt.collab;

import org.caleydo.core.manager.event.AEvent;

/**
 * Test event for serialization stuff
 * 
 * @author Werner Puff
 */
public class TestSerializationEvent
	extends AEvent {

	private String serializedText = null;

	@Override
	public boolean checkIntegrity() {
		if (serializedText == null)
			throw new IllegalStateException("serializedText was not set");
		return true;
	}

	public String getSerializedText() {
		return serializedText;
	}

	public void setSerializedText(String serializedText) {
		this.serializedText = serializedText;
	}

}


