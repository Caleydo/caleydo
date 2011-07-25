package org.caleydo.core.data.virtualarray.group;

import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.delta.StorageVADelta;

public class StorageGroupList
	extends GroupList<StorageGroupList, DimensionVirtualArray, StorageVADelta> {

	@Override
	public StorageGroupList createInstance() {
		return new StorageGroupList();
	}

}
