package org.caleydo.core.manager.event.data;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;

public class DimensionReplaceVAEvent
	extends ReplaceVAEvent<DimensionVirtualArray> {

	public DimensionReplaceVAEvent() {
		// nothing to initialize here
	}

	public DimensionReplaceVAEvent(DataTable set, String dataDomainType, String vaType) {
		super(set, dataDomainType, vaType);
	}

	public DimensionReplaceVAEvent(DataTable set, String dataDomainType, String vaType,
		DimensionVirtualArray virtualArray) {
		super(set, dataDomainType, vaType, virtualArray);
	}

}
