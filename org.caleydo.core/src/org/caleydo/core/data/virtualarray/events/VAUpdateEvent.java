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
package org.caleydo.core.data.virtualarray.events;

import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.event.AEvent;

/**
 * <p>
 * Base class for Virtual Array Updates. These events are intended to provide information which virtual array
 * has changed. Receivers are expected to go to their data structure and reload the VA from there.
 * </p>
 * <p>
 * This is intended to be created and published only by instances managing the data structures, such as
 * {@link ATableBasedDataDomain}.
 * </p>
 * 
 * @author Alexander Lex
 */
public abstract class VAUpdateEvent
	extends AEvent {

	/** the id of the associated {@link Table} */
	private String perspectiveID = null;

	/**
	 * Set the ID of the {@link Perspective} the virtual array to be updated is associated with
	 * 
	 * @param perspectiveID
	 */
	public void setPerspectiveID(String perspectiveID) {
		this.perspectiveID = perspectiveID;
	}

	/**
	 * Get the ID of the {@link Table} the virtual array to be updated is associated with
	 * 
	 * @return the id of the associated {@link Table}
	 */
	public String getPerspectiveID() {
		return perspectiveID;
	}

	@Override
	public boolean checkIntegrity() {
		if (perspectiveID == null)
			return false;
		return true;
	}

}
