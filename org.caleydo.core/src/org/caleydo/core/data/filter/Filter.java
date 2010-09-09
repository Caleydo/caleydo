package org.caleydo.core.data.filter;

import org.caleydo.core.data.selection.IVAType;
import org.caleydo.core.data.selection.delta.VirtualArrayDelta;

public class Filter<VAType extends IVAType, DeltaType extends VirtualArrayDelta<?, VAType>> {

	// VirtualArrayDelta<deltaType extends Vi, VAType> vaDelta;

	DeltaType vaDelta;

	public void setDelta(DeltaType vaDelta) {
		this.vaDelta = vaDelta;
	}

	public DeltaType getVADelta() {
		return vaDelta;
	}

}
