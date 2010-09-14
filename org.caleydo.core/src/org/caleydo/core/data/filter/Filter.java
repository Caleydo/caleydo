package org.caleydo.core.data.filter;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.filter.event.NewContentFilterEvent;
import org.caleydo.core.data.filter.representation.AFilterRepresentation;
import org.caleydo.core.data.virtualarray.IVAType;
import org.caleydo.core.data.virtualarray.delta.ContentVADelta;
import org.caleydo.core.data.virtualarray.delta.VirtualArrayDelta;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;

/**
 * Generic base class for Filters. A Filter contains changes made to a virtual array. Sub-classes may
 * additionally hold information about the filter operations.
 * 
 * @author Alexander Lex
 * @param <VAType>
 * @param <DeltaType>
 */
public abstract class Filter<VAType extends IVAType, DeltaType extends VirtualArrayDelta<?, VAType>> {

	private DeltaType vaDelta;

	private String label = "<unspecified>";

	private AFilterRepresentation filterRep;

	private boolean isRegistered = false;

	private ISet set;
	
	protected ASetBasedDataDomain dataDomain;

	public void setDelta(DeltaType vaDelta) {
		this.vaDelta = vaDelta;
	}

	public DeltaType getVADelta() {
		return vaDelta;
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

	public void setSet(ISet set) {
		this.set = set;
	}

	public ISet getSet() {
		return set;
	}

	public void openRepresentation() {
		filterRep.open();
	}

	public boolean isRegistered() {
		return isRegistered;
	}

	void setRegistered(boolean isRegistered) {
		this.isRegistered = isRegistered;
	}

	public void setDataDomain(ASetBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}
	
	public ASetBasedDataDomain getDataDomain() {
		return dataDomain;
	}
	
	public abstract void updateFilterManager();
}
