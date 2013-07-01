/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.filter;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.filter.event.NewFilterEvent;
import org.caleydo.core.data.filter.event.ReEvaluateFilterListEvent;
import org.caleydo.core.data.filter.representation.AFilterRepresentation;
import org.caleydo.core.data.virtualarray.delta.VirtualArrayDelta;
import org.caleydo.core.manager.GeneralManager;

/**
 * Class for Filters. A Filter contains changes made to a virtual array. Sub-classes may additionally hold information
 * about the filter operations.
 * 
 * @author Alexander Lex
 */
public class Filter {

	private VirtualArrayDelta vaDelta;

	private VirtualArrayDelta vaDeltaUncertainty;

	private String label = "<unspecified>";

	private AFilterRepresentation filterRep;

	protected boolean isRegistered = false;

	protected ATableBasedDataDomain dataDomain;

	/** The id identifying to which perspective this filter should be applied */
	private String perspectiveID;

	/**
	 * Constructor which should be used only for de-serialization
	 */
	// public Filter() {
	// }

	/**
	 *
	 */
	public Filter(String perspectiveID) {
		this.perspectiveID = perspectiveID;
	}

	/**
	 * Should only be used for de-serialization
	 *
	 * @param perspectiveID
	 *            setter, see {@link #perspectiveID}
	 */
	public void setPerspectiveID(String perspectiveID) {
		this.perspectiveID = perspectiveID;
	}

	/**
	 * @return the perspectiveID, see {@link #perspectiveID}
	 */
	public String getPerspectiveID() {
		return perspectiveID;
	}

	public void setVADelta(VirtualArrayDelta vaDelta) {
		this.vaDelta = vaDelta;
	}

	public VirtualArrayDelta getVADelta() {
		return vaDelta;
	}

	public void setVADeltaUncertainty(VirtualArrayDelta vaDeltaUncertain) {
		this.vaDeltaUncertainty = vaDeltaUncertain;
	}

	public VirtualArrayDelta getVADeltaUncertainty() {
		return vaDeltaUncertainty;
	}

	public void setFilterRep(AFilterRepresentation filterRep) {
		this.filterRep = filterRep;
	}

	public AFilterRepresentation getFilterRep() {
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

	public void updateFilterManager() {

		if (!isRegistered()) {
			NewFilterEvent filterEvent = new NewFilterEvent();
			filterEvent.setFilter(this);
			filterEvent.setSender(this);
			filterEvent.setEventSpace(dataDomain.getDataDomainID());

			GeneralManager.get().getEventPublisher().triggerEvent(filterEvent);

			isRegistered = true;
		} else {

			ReEvaluateFilterListEvent reevaluateEvent = new ReEvaluateFilterListEvent();
			// reevaluateEvent.addFilter(filter);
			reevaluateEvent.setSender(this);
			reevaluateEvent.setEventSpace(dataDomain.getDataDomainID());

			GeneralManager.get().getEventPublisher().triggerEvent(reevaluateEvent);
		}
	}
}
