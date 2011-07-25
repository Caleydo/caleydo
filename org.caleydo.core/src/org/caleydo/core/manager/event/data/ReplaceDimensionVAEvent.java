package org.caleydo.core.manager.event.data;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;

public class ReplaceDimensionVAEvent
	extends ReplaceVAEvent<DimensionVirtualArray> {

	public ReplaceDimensionVAEvent() {
		// nothing to initialize here
	}

	public ReplaceDimensionVAEvent(DataTable set, String dataDomainType, String vaType) {
		super(set, dataDomainType, vaType);
	}

	public ReplaceDimensionVAEvent(DataTable set, String dataDomainType, String vaType,
		DimensionVirtualArray virtualArray) {
		super(set, dataDomainType, vaType, virtualArray);
	}

}
