/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.pathway.listener;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.event.AEvent;

/**
 * Event to signal that a pathway should be loaded.
 *
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class LoadPathwayEvent
	extends AEvent {

	/** id of the pathway to load */
	private int pathwayID = -1;

	/**
	 *
	 */
	public LoadPathwayEvent() {
	}

	/**
	 * gets the id of the pathway to load
	 * 
	 * @return pathway-id to load
	 */
	public int getPathwayID() {
		return pathwayID;
	}

	/**
	 * sets the id of the pathway to load
	 *
	 * @param pathwayID
	 *            pathway-id to load
	 */
	public void setPathwayID(int pathwayID) {
		this.pathwayID = pathwayID;
	}

	@Override
	public boolean checkIntegrity() {
		if (pathwayID == -1)
			throw new IllegalStateException("pathwayID was not set");
		return true;
	}

}
