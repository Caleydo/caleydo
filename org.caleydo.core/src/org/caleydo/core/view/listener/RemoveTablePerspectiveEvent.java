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
package org.caleydo.core.view.listener;

import org.caleydo.core.event.AEvent;

/**
 * Event that triggers the removal of a data container with the specified
 * tablePerspectiveID.
 * 
 * @author Alexander Lex
 * 
 */
public class RemoveTablePerspectiveEvent extends AEvent {

	/** The ID of the data container to be removed */
	int tablePerspectiveID = -1;

	/**
	 * 
	 */
	public RemoveTablePerspectiveEvent() {
	}

	public RemoveTablePerspectiveEvent(int tablePerspectiveID) {
		this.tablePerspectiveID = tablePerspectiveID;
	}

	/**
	 * @param tablePerspectiveID
	 *            setter, see {@link #tablePerspectiveID}
	 */
	public void setTablePerspectiveID(int tablePerspectiveID) {
		this.tablePerspectiveID = tablePerspectiveID;
	}

	/**
	 * @return the tablePerspectiveID, see {@link #tablePerspectiveID}
	 */
	public int getTablePerspectiveID() {
		return tablePerspectiveID;
	}

	@Override
	public boolean checkIntegrity() {
		if (tablePerspectiveID < 0)
			return false;
		return true;
	}

}
