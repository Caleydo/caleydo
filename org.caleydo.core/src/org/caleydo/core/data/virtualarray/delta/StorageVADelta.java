package org.caleydo.core.data.virtualarray.delta;

import org.caleydo.core.data.mapping.IDType;

public class StorageVADelta
	extends VirtualArrayDelta<StorageVADelta> {

	public StorageVADelta() {
	}

	public StorageVADelta(String vaType, IDType idType) {
		super(vaType, idType);
	}

	public StorageVADelta(String vaType, IDType idType, IDType secondaryIDType) {
		super(vaType, idType, secondaryIDType);
	}

	@Override
	public StorageVADelta getInstance() {
		return new StorageVADelta();
	}

}
