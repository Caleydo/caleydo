package org.caleydo.core.data.filter;

import org.caleydo.core.data.virtualarray.IVAType;
import org.caleydo.core.data.virtualarray.delta.VirtualArrayDelta;

/**
 * Generic base class for Filters. A Filter contains changes made to a virtual array. Sub-classes may
 * additionally hold information about the filter operations.
 * 
 * @author Alexander Lex
 * @param <VAType>
 * @param <DeltaType>
 */
public class Filter<VAType extends IVAType, DeltaType extends VirtualArrayDelta<?, VAType>> {

	DeltaType vaDelta;

	public void setDelta(DeltaType vaDelta) {
		this.vaDelta = vaDelta;
	}

	public DeltaType getVADelta() {
		return vaDelta;
	}

}
