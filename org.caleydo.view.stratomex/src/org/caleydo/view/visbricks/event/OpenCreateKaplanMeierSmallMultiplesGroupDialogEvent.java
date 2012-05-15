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
package org.caleydo.view.visbricks.event;

import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.event.AEvent;

/**
 * Event for opening the dialog for creating small multiple kaplan meier groups
 * 
 * @author Marc Streit
 * 
 */
public class OpenCreateKaplanMeierSmallMultiplesGroupDialogEvent extends AEvent {

	/**
	 * The base data container which contains the subgroups for which the small
	 * multiples will be created.
	 */
	private DataContainer dimensionGroupDataContainer;

	/**
	 * The base dimension perspective containing all genes that will be mapped
	 * to the pathways.
	 */
	private DimensionPerspective dimensionPerspective;

	/**
	 * @param dataContainer
	 *            the base data container which contains the subgroups for which
	 *            the small multiples will be created.
	 */
	public OpenCreateKaplanMeierSmallMultiplesGroupDialogEvent(
			DataContainer dimensionGroupDataContainer,
			DimensionPerspective dimensionPerspective) {

		this.setDimensionGroupDataContainer(dimensionGroupDataContainer);
		this.setDimensionPerspective(dimensionPerspective);
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}

	/**
	 * @param dataContainer
	 *            setter, see {@link #dimensionGroupDataContainer}
	 */
	public void setDimensionGroupDataContainer(DataContainer dataContainer) {
		this.dimensionGroupDataContainer = dataContainer;
	}

	/**
	 * @return the dataContainer, see {@link #dimensionGroupDataContainer}
	 */
	public DataContainer getDimensionGroupDataContainer() {
		return dimensionGroupDataContainer;
	}

	/**
	 * @param dimensionPerspective
	 *            setter, see {@link #dimensionPerspective}
	 */
	public void setDimensionPerspective(DimensionPerspective dimensionPerspective) {
		this.dimensionPerspective = dimensionPerspective;
	}

	/**
	 * @return the dimensionPerspective, see {@link #dimensionPerspective}
	 */
	public DimensionPerspective getDimensionPerspective() {
		return dimensionPerspective;
	}
}
