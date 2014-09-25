/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.event;

import org.caleydo.core.event.AEvent;

/**
 * @author Christian
 *
 */
public class RemoveGeneEvent extends AEvent {

	private int davidID;

	public RemoveGeneEvent(int davidID) {
		this.davidID = davidID;
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}

	/**
	 * @return the davidID, see {@link #davidID}
	 */
	public int getDavidID() {
		return davidID;
	}

	/**
	 * @param davidID
	 *            setter, see {@link davidID}
	 */
	public void setDavidID(int davidID) {
		this.davidID = davidID;
	}

}
