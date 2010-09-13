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
	
	private DeltaType vaDelta;
	
	private String label = "<unspecified>";
	
	private AFilterRepresentation filterRep;

	public void setDelta(DeltaType vaDelta) {
		this.vaDelta = vaDelta;
	}

	public DeltaType getVADelta() {
		return vaDelta;
	}

	public void createRepresentation() {
		if (filterRep != null);
			new AFilterRepresentation().open();
	}
	
	public void setFilterRep(AFilterRepresentation filterRep) {
		this.filterRep = filterRep;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
}
