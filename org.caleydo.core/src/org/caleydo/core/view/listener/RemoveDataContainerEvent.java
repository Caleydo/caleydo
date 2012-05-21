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
 * dataContainerID.
 * 
 * @author Alexander Lex
 * 
 */
public class RemoveDataContainerEvent extends AEvent {

	/** The ID of the data container to be removed */
	int dataContainerID = -1;

	/**
	 * 
	 */
	public RemoveDataContainerEvent() {
	}

	public RemoveDataContainerEvent(int dataContainerID) {
		this.dataContainerID = dataContainerID;
	}

	/**
	 * @param dataContainerID
	 *            setter, see {@link #dataContainerID}
	 */
	public void setDataContainerID(int dataContainerID) {
		this.dataContainerID = dataContainerID;
	}

	/**
	 * @return the dataContainerID, see {@link #dataContainerID}
	 */
	public int getDataContainerID() {
		return dataContainerID;
	}

	@Override
	public boolean checkIntegrity() {
		if (dataContainerID < 0)
			return false;
		return true;
	}

}
