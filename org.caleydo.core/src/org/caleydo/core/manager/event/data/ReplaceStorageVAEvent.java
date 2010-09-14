package org.caleydo.core.manager.event.data;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.virtualarray.StorageVirtualArray;

public class ReplaceStorageVAEvent
	extends ReplaceVAEvent<StorageVirtualArray> {

	public ReplaceStorageVAEvent() {
		// nothing to initialize here
	}

	public ReplaceStorageVAEvent(ISet set, String dataDomainType, String vaType) {
		super(set, dataDomainType, vaType);
	}

	public ReplaceStorageVAEvent(ISet set, String dataDomainType, String vaType,
		StorageVirtualArray virtualArray) {
		super(set, dataDomainType, vaType, virtualArray);
	}

}
