package org.caleydo.core.manager.event.data;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;

public class ReplaceStorageVAEvent
	extends ReplaceVAEvent<DimensionVirtualArray> {

	public ReplaceStorageVAEvent() {
		// nothing to initialize here
	}

	public ReplaceStorageVAEvent(DataTable set, String dataDomainType, String vaType) {
		super(set, dataDomainType, vaType);
	}

	public ReplaceStorageVAEvent(DataTable set, String dataDomainType, String vaType,
		DimensionVirtualArray virtualArray) {
		super(set, dataDomainType, vaType, virtualArray);
	}

}
