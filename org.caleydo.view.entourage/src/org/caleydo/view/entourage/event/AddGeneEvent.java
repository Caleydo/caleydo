/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.entourage.event;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.id.IDType;

/**
 * Event to add a gene to the experimental data visualization of entourage (enroute).
 *
 * @author Christian
 *
 */
public class AddGeneEvent extends AEvent {

	protected Object geneID;
	protected IDType idType;

	public AddGeneEvent(Object geneID, IDType idType) {
		this.geneID = geneID;
		this.idType = idType;
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}

	/**
	 * @param idType
	 *            setter, see {@link idType}
	 */
	public void setIdType(IDType idType) {
		this.idType = idType;
	}

	/**
	 * @return the idType, see {@link #idType}
	 */
	public IDType getIdType() {
		return idType;
	}

	/**
	 * @param geneID
	 *            setter, see {@link geneID}
	 */
	public void setGeneID(Object geneID) {
		this.geneID = geneID;
	}

	/**
	 * @return the geneID, see {@link #geneID}
	 */
	public Object getGeneID() {
		return geneID;
	}

}
