package org.caleydo.core.manager.event.data;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;

public class ReplaceRecordVAEvent
	extends ReplaceVAEvent<RecordVirtualArray> {

	public ReplaceRecordVAEvent() {
		// nothing to initialize here
	}

	public ReplaceRecordVAEvent(DataTable set, String dataDomainType, String vaType) {
		super(set, dataDomainType, vaType);
	}

	protected ReplaceRecordVAEvent(DataTable set, String dataDomainType, String vaType,
		RecordVirtualArray virtualArray) {
		super(set, dataDomainType, vaType, virtualArray);
	}

	protected ReplaceRecordVAEvent(String dataDomainType, String vaType, RecordVirtualArray virtualArray) {
		super(dataDomainType, vaType, virtualArray);
	}
}
