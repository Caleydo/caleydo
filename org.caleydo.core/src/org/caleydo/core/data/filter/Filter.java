package org.caleydo.core.data.filter;

import org.caleydo.core.data.filter.representation.AFilterRepresentation;
import org.caleydo.core.data.virtualarray.delta.VirtualArrayDelta;
import org.caleydo.core.manager.datadomain.ATableBasedDataDomain;

/**
 * Generic base class for Filters. A Filter contains changes made to a virtual array. Sub-classes may
 * additionally hold information about the filter operations.
 * 
 * @author Alexander Lex
 * @param <VAType>
 * @param <DeltaType>
 */
public abstract class Filter<DeltaType extends VirtualArrayDelta<?>> {

	private DeltaType vaDelta;
	
	private DeltaType vaDeltaUncertainty;

	private String label = "<unspecified>";

	private AFilterRepresentation<DeltaType, ?> filterRep;

	protected boolean isRegistered = false;

	protected ATableBasedDataDomain dataDomain;

	public void setVADelta(DeltaType vaDelta) {
		this.vaDelta = vaDelta;
	}

	public DeltaType getVADelta() {
		return vaDelta;
	}

	public void setVADeltaUncertainty(DeltaType vaDeltaUncertain) {
		this.vaDeltaUncertainty = vaDeltaUncertain;
	}
	
	public DeltaType getVADeltaUncertainty() {
		return vaDeltaUncertainty;
	}
	
	public void setFilterRep(AFilterRepresentation<DeltaType, ?> filterRep) {
		this.filterRep = filterRep;
	}

	public AFilterRepresentation<DeltaType, ?> getFilterRep() {
		return filterRep;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void openRepresentation() {

		if (filterRep != null)
			filterRep.create();
	}

	public boolean isRegistered() {
		return isRegistered;
	}

	void setRegistered(boolean isRegistered) {
		this.isRegistered = isRegistered;
	}

	public void setDataDomain(ATableBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	public ATableBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	public abstract void updateFilterManager();
}
