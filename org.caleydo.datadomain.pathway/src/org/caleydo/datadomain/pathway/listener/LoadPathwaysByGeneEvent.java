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
package org.caleydo.datadomain.pathway.listener;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.id.IDType;

/**
 * Event to signal that pathways should be added to the bucket in which a certain gene is contained.
 * 
 * @author Marc Streit
 */
@XmlRootElement
@XmlType
public class LoadPathwaysByGeneEvent
	extends AEvent {

	/** gene ID of the idType */
	private int geneID = -1;

	private IDType idType;

	public int getGeneID() {
		return geneID;
	}

	public void setGeneID(int geneId) {
		geneID = geneId;
	}

	public IDType getIdType() {
		return idType;
	}

	public void setTableIDType(IDType idType) {
		this.idType = idType;
	}

	@Override
	public boolean checkIntegrity() {
		if (geneID == -1)
			throw new IllegalStateException("geneID was not set");
		if (idType == null)
			throw new NullPointerException("idType is null");
		return true;
	}

}