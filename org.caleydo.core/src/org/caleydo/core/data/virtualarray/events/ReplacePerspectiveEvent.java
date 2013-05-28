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

import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.event.AEvent;

/**
 * Event that signals that the virtual array has changed. VA users have to load the new one from the UseCase
 * if only the vaType is provided, or use the va attached.
 *
 * @author Alexander Lex
 */
@XmlRootElement
@XmlType
public class ReplacePerspectiveEvent
	extends AEvent {

	private PerspectiveInitializationData data;
	private String perspectiveID = null;

	/**
	 * default no-arg constructor.
	 */
	public ReplacePerspectiveEvent() {
		// nothing to initialize here
	}

	/**
	 * If no set is specified, the use case should send this to all suitable sets
	 *
	 * @param idCategory
	 * @param perspectiveID
	 * @param virtualArray
	 */
	public ReplacePerspectiveEvent(String dataDomainID, String perspectiveID,
		PerspectiveInitializationData data) {
		this.eventSpace = dataDomainID;
		this.perspectiveID = perspectiveID;
		this.data = data;
	}

	public PerspectiveInitializationData getPerspectiveInitializationData() {
		return data;
	}

	@Override
	public boolean checkIntegrity() {
		if (eventSpace == null || perspectiveID == null)
			return false;

		return true;
	}

	public void setPerspectiveInitializationData(PerspectiveInitializationData data) {
		this.data = data;
	}

	public String getPerspectiveID() {
		return perspectiveID;
	}
}
