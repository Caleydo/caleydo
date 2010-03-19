package org.caleydo.core.manager.event.data;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.selection.StorageVAType;
import org.caleydo.core.data.selection.StorageVirtualArray;

public class ReplaceStorageVAEvent
	extends ReplaceVAEvent<StorageVirtualArray, StorageVAType> {

	public ReplaceStorageVAEvent() {
		// nothing to initialize here
	}

	public ReplaceStorageVAEvent(ISet set, EIDCategory idCategory, StorageVAType vaType) {
		super(set, idCategory, vaType);
	}

	public ReplaceStorageVAEvent(ISet set, EIDCategory idCategory, StorageVAType vaType,
		StorageVirtualArray virtualArray) {
		super(set, idCategory, vaType, virtualArray);
	}

}
