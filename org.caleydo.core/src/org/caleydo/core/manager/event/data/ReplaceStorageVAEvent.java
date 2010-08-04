package org.caleydo.core.manager.event.data;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.selection.StorageVAType;
import org.caleydo.core.data.selection.StorageVirtualArray;

public class ReplaceStorageVAEvent
	extends ReplaceVAEvent<StorageVirtualArray, StorageVAType> {

	public ReplaceStorageVAEvent() {
		// nothing to initialize here
	}

	public ReplaceStorageVAEvent(ISet set, String dataDomainType, StorageVAType vaType) {
		super(set, dataDomainType, vaType);
	}

	public ReplaceStorageVAEvent(ISet set, String dataDomainType, StorageVAType vaType,
		StorageVirtualArray virtualArray) {
		super(set, dataDomainType, vaType, virtualArray);
	}

}
