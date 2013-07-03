/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
