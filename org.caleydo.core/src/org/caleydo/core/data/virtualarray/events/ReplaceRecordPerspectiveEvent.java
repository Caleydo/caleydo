package org.caleydo.core.data.virtualarray.events;

import org.caleydo.core.data.perspective.PerspectiveInitializationData;

public class ReplaceRecordPerspectiveEvent
	extends ReplacePerspectiveEvent {

	public ReplaceRecordPerspectiveEvent() {
		// nothing to initialize here
	}


	protected ReplaceRecordPerspectiveEvent(String dataDomainID, String perspectiveID,
		PerspectiveInitializationData data) {
		super(dataDomainID, perspectiveID, data);
	}
}
