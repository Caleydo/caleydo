package org.caleydo.core.data.virtualarray.events;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;

public class RecordReplaceVAEvent
	extends VAReplaceEvent<RecordVirtualArray> {

	public RecordReplaceVAEvent() {
		// nothing to initialize here
	}

	public RecordReplaceVAEvent(DataTable set, String dataDomainType, String vaType) {
		super(set, dataDomainType, vaType);
	}

	protected RecordReplaceVAEvent(DataTable set, String dataDomainType, String vaType,
		RecordVirtualArray virtualArray) {
		super(set, dataDomainType, vaType, virtualArray);
	}

	protected RecordReplaceVAEvent(String dataDomainType, String vaType, RecordVirtualArray virtualArray) {
		super(dataDomainType, vaType, virtualArray);
	}
}
