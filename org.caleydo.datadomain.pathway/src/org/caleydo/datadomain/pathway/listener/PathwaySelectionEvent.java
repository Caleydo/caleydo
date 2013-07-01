/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.pathway.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;

/**
 *
 *
 * @author Christian Partl
 *
 */
public class PathwaySelectionEvent extends AEvent {

	protected PathwayGraph pathway;

	/**
	 *
	 */
	public PathwaySelectionEvent() {
		// TODO Auto-generated constructor stub
	}

	/**
	 *
	 */
	public PathwaySelectionEvent(PathwayGraph pathway) {
		this.pathway = pathway;
	}

	@Override
	public boolean checkIntegrity() {
		return pathway != null;
	}

	/**
	 * @return the pathway, see {@link #pathway}
	 */
	public PathwayGraph getPathway() {
		return pathway;
	}

	/**
	 * @param pathway
	 *            setter, see {@link pathway}
	 */
	public void setPathway(PathwayGraph pathway) {
		this.pathway = pathway;
	}

}
