package org.caleydo.core.data.selection.delta;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.StorageVAType;

public class StorageVADelta
	extends VirtualArrayDelta<StorageVADelta, StorageVAType> {

	public StorageVADelta() {
	}

	public StorageVADelta(StorageVAType vaType, EIDType idType) {
		super(vaType, idType);
	}

	public StorageVADelta(StorageVAType vaType, EIDType idType, EIDType secondaryIDType) {
		super(vaType, idType, secondaryIDType);
	}

	@Override
	public StorageVADelta getInstance() {
		return new StorageVADelta();
	}

}
