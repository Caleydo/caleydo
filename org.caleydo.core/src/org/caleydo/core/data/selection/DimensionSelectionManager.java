package org.caleydo.core.data.selection;

import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.delta.DimensionVADelta;

public class DimensionSelectionManager
	extends
	VABasedSelectionManager<DimensionSelectionManager, DimensionVirtualArray, DimensionVADelta> {

	public DimensionSelectionManager(IDType idType) {
		super(idType);
	}
}
