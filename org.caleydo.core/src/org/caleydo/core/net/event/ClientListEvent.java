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
package org.caleydo.core.net.event;

import java.util.List;

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.event.AEvent;

/**
 * Event for distributing the list of all clients to each of the connected clients. This event should be send
 * from the server to its clients whenever a new client connects or an existing client disconnects.
 * 
 * @author Werner Puff
 */
@XmlType
@XmlRootElement
public class ClientListEvent
	extends AEvent {

	/** list of clients */
	private List<String> clientNames;

	@Override
	public boolean checkIntegrity() {
		if (clientNames == null) {
			return false;
		}
		return true;
	}

	/**
	 * Getter for {@link ClientListEvent#clientNames}
	 * 
	 * @return {@link ClientListEvent#clientNames}
	 */
	@XmlElementWrapper
	public List<String> getClientNames() {
		return clientNames;
	}

	/**
	 * Setter for {@link ClientListEvent#clientNames}
	 * 
	 * @param clientNames
	 *            new {@link ClientListEvent#clientNames} to set
	 */
	public void setClientNames(List<String> clientNames) {
		this.clientNames = clientNames;
	}

}
