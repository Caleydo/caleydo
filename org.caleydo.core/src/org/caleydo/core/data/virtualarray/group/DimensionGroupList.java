package org.caleydo.core.data.virtualarray.group;

import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.delta.DimensionVADelta;

public class DimensionGroupList
	extends GroupList<DimensionGroupList, DimensionVirtualArray, DimensionVADelta> {

	@Override
	public DimensionGroupList createInstance() {
		return new DimensionGroupList();
	}

}
