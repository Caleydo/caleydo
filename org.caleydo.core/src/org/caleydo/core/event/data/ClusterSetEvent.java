/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.event.data;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.AEvent;

/**
 * Event that signals that a given set needs to be clustered.
 * 
 * @author Marc Streit
 */
@XmlRootElement
@XmlType
public class ClusterSetEvent
	extends AEvent {

	private ArrayList<TablePerspective> sets;

	/**
	 * default no-arg constructor
	 */
	public ClusterSetEvent() {
		// nothing to initialize here
	}

	public ClusterSetEvent(ArrayList<TablePerspective> sets) {
		this.sets = sets;
	}

	public ArrayList<TablePerspective> setTables() {
		return sets;
	}

	public ArrayList<TablePerspective> getTables() {
		return sets;
	}

	@Override
	public boolean checkIntegrity() {
		if (sets == null || sets.size() == 0)
			return false;
		return true;
	}

}
