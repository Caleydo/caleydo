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
package org.caleydo.core.event.view.selection;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.id.IDType;
import org.caleydo.core.view.vislink.SelectionPoint2DList;

/**
 * Sends new 2d connection line vertices from a caleydo-client to a server.
 * 
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class AddConnectionLineVerticesEvent
	extends AEvent {

	private IDType idType;

	private Integer connectionID;

	private SelectionPoint2DList points;

	@Override
	public boolean checkIntegrity() {
		// TODO
		return true;
	}

	public IDType getIdType() {
		return idType;
	}

	public void setIdType(IDType idType) {
		this.idType = idType;
	}

	public SelectionPoint2DList getPoints() {
		return points;
	}

	public void setPoints(SelectionPoint2DList points) {
		this.points = points;
	}

	public Integer getConnectionID() {
		return connectionID;
	}

	public void setConnectionID(Integer connectionID) {
		this.connectionID = connectionID;
	}

}
