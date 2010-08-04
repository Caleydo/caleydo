package org.caleydo.core.data.selection.delta;

import org.caleydo.core.data.mapping.IDType;
import org.caleydo.core.data.selection.StorageVAType;

public class StorageVADelta
	extends VirtualArrayDelta<StorageVADelta, StorageVAType> {

	public StorageVADelta() {
	}

	public StorageVADelta(StorageVAType vaType, IDType idType) {
		super(vaType, idType);
	}

	public StorageVADelta(StorageVAType vaType, IDType idType, IDType secondaryIDType) {
		super(vaType, idType, secondaryIDType);
	}

	@Override
	public StorageVADelta getInstance() {
		return new StorageVADelta();
	}

}
