package org.caleydo.core.data.virtualarray.delta;

import org.caleydo.core.data.id.IDType;

public class RecordVADelta
	extends VirtualArrayDelta<RecordVADelta> {

	public RecordVADelta() {
	}

	public RecordVADelta(String recordPerspectiveID, IDType idType) {
		super(recordPerspectiveID, idType);
	}

	@Override
	public RecordVADelta getInstance() {
		return new RecordVADelta();
	}

}
