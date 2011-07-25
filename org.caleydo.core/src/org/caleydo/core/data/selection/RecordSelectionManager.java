package org.caleydo.core.data.selection;

import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.delta.RecordVADelta;

public class RecordSelectionManager
	extends VABasedSelectionManager<RecordSelectionManager, RecordVirtualArray, RecordVADelta> {

	public RecordSelectionManager(IDType idType) {
		super(idType);
	}
}
