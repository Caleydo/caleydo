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
package org.caleydo.core.event.view.linking;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.caleydo.core.data.selection.ElementConnectionInformation;
import org.caleydo.core.event.AEvent;

/**
 * Signals the creation of a new selection.
 * 
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class AddSelectionEvent
	extends AEvent {

	/** Related connectionID of the selection, might be legal in different views */
	private Integer connectionID;

	/** {@link ElementConnectionInformation} of the selection to add */
	private ElementConnectionInformation selectedElementRep;

	@Override
	public boolean checkIntegrity() {
		if (connectionID == null || selectedElementRep == null) {
			return false;
		}
		return true;
	}

	public Integer getConnectionID() {
		return connectionID;
	}

	public void setConnectionID(Integer connectionID) {
		this.connectionID = connectionID;
	}

	public ElementConnectionInformation getSelectedElementRep() {
		return selectedElementRep;
	}

	public void setSelectedElementRep(ElementConnectionInformation selectedElementRep) {
		this.selectedElementRep = selectedElementRep;
	}

}
