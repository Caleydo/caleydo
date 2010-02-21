package org.caleydo.core.data.selection;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.delta.StorageVADelta;

public class StorageSelectionManager
	extends
	VABasedSelectionManager<StorageSelectionManager, StorageVirtualArray, StorageVAType, StorageVADelta> {

	public StorageSelectionManager(EIDType idType) {
		super(idType);
	}
}
