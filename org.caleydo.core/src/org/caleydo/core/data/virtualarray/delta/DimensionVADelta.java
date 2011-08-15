package org.caleydo.core.data.virtualarray.delta;

import org.caleydo.core.data.id.IDType;

public class DimensionVADelta
	extends VirtualArrayDelta<DimensionVADelta> {

	public DimensionVADelta() {
	}

	public DimensionVADelta(String vaType, IDType idType) {
		super(vaType, idType);
	}

	@Override
	public DimensionVADelta getInstance() {
		return new DimensionVADelta();
	}

}
