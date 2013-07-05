/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.pathway.listener;

import java.util.Set;

import org.caleydo.core.event.AEvent;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;

/**
 * Event that contains all portals that should be highlighted.
 *
 * @author Christian Partl
 *
 */
public class HighlightPortalsEvent extends AEvent {

	protected Set<PathwayVertexRep> portals;

	/**
	 *
	 */
	public HighlightPortalsEvent() {
	}

	/**
	 *
	 */
	public HighlightPortalsEvent(Set<PathwayVertexRep> portals) {
		this.portals = portals;
	}

	@Override
	public boolean checkIntegrity() {
		return portals != null;
	}

	/**
	 * @param portals
	 *            setter, see {@link portals}
	 */
	public void setPortals(Set<PathwayVertexRep> portals) {
		this.portals = portals;
	}

	/**
	 * @return the portals, see {@link #portals}
	 */
	public Set<PathwayVertexRep> getPortals() {
		return portals;
	}

}
