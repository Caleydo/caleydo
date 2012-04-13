/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.data.filter;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.filter.representation.AFilterRepresentation;
import org.caleydo.core.data.virtualarray.delta.VirtualArrayDelta;

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
