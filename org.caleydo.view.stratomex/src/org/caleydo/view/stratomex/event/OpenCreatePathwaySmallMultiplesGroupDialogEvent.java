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
package org.caleydo.view.stratomex.event;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.DimensionPerspective;
import org.caleydo.core.event.AEvent;

/**
 * Event for opening the dialog for creating small multiple pathway groups
 * 
 * @author Marc Streit
 * 
 */
public class OpenCreatePathwaySmallMultiplesGroupDialogEvent extends AEvent {

	/**
	 * The base data container which contains the subgroups for which the small
	 * multiples will be created.
	 */
	private TablePerspective dimensionGroupTablePerspective;

	/**
	 * The base dimension perspective containing all genes that will be mapped
	 * to the pathways.
	 */
	private DimensionPerspective dimensionPerspective;

	/**
	 * @param tablePerspective
	 *            the base data container which contains the subgroups for which
	 *            the small multiples will be created.
	 */
	public OpenCreatePathwaySmallMultiplesGroupDialogEvent(
			TablePerspective dimensionGroupTablePerspective,
			DimensionPerspective dimensionPerspective) {

		this.setDimensionGroupTablePerspective(dimensionGroupTablePerspective);
		this.setDimensionPerspective(dimensionPerspective);
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}

	/**
	 * @param tablePerspective
	 *            setter, see {@link #dimensionGroupTablePerspective}
	 */
	public void setDimensionGroupTablePerspective(TablePerspective tablePerspective) {
		this.dimensionGroupTablePerspective = tablePerspective;
	}

	/**
	 * @return the tablePerspective, see {@link #dimensionGroupTablePerspective}
	 */
	public TablePerspective getDimensionGroupTablePerspective() {
		return dimensionGroupTablePerspective;
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
