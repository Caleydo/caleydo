package org.caleydo.core.data.virtualarray.events;

import org.caleydo.core.data.virtualarray.RecordVirtualArray;

public class RecordReplaceVAEvent
	extends VAReplaceEvent<RecordVirtualArray> {

	public RecordReplaceVAEvent() {
		// nothing to initialize here
	}


	protected RecordReplaceVAEvent(String dataDomainID, String perspectiveID, RecordVirtualArray virtualArray) {
		super(dataDomainID, perspectiveID, virtualArray);
	}
}
