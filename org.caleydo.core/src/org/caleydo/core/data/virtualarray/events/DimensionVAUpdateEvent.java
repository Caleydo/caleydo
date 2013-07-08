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

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Event to signal that a {@link DimensionVirtualArray} has changed. For details see {@link VAUpdateEvent}.
 * 
 * @author Alexander Lex
 */
@XmlRootElement
@XmlType
public class DimensionVAUpdateEvent
	extends VAUpdateEvent {

	public DimensionVAUpdateEvent() {
		// TODO Auto-generated constructor stub
	}

	public DimensionVAUpdateEvent(String dataDomainID, String perspectiveID, Object sender) {
		setEventSpace(dataDomainID);
		setPerspectiveID(perspectiveID);
		setSender(sender);
	}

}
