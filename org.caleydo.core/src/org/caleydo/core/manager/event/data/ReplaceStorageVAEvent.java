package org.caleydo.core.manager.event.data;

import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.selection.StorageVAType;
import org.caleydo.core.data.selection.StorageVirtualArray;

public class ReplaceStorageVAEvent
	extends ReplaceVAEvent<StorageVirtualArray, StorageVAType> {

	public ReplaceStorageVAEvent() {
		// nothing to initialize here
	}

	public ReplaceStorageVAEvent(EIDCategory idCategory, StorageVAType vaType) {
		super(idCategory, vaType);
	}

	public ReplaceStorageVAEvent(EIDCategory idCategory, StorageVAType vaType,
		StorageVirtualArray virtualArray) {
		super(idCategory, vaType, virtualArray);
	}

}
