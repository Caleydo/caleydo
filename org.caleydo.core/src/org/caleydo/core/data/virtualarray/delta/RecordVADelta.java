package org.caleydo.core.data.virtualarray.delta;

import org.caleydo.core.data.id.IDType;

public class RecordVADelta
	extends VirtualArrayDelta<RecordVADelta> {

	public RecordVADelta() {
	}

	public RecordVADelta(String vaType, IDType idType) {
		super(vaType, idType);
	}

	public RecordVADelta(String vaType, IDType idType, IDType secondaryIDType) {
		super(vaType, idType, secondaryIDType);
	}

	@Override
	public RecordVADelta getInstance() {
		return new RecordVADelta();
	}

}