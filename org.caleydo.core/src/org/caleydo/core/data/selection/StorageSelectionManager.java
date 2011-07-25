package org.caleydo.core.data.selection;

import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.delta.StorageVADelta;

public class StorageSelectionManager
	extends
	VABasedSelectionManager<StorageSelectionManager, DimensionVirtualArray, StorageVADelta> {

	public StorageSelectionManager(IDType idType) {
		super(idType);
	}
}
