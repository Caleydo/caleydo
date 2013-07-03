/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.pathway.listener;

import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;

/**
 * Event signaling that all nodes equivalent to a {@link PathwayVertexRep} (the portal) shall be indicated.
 *
 * @author Christian Partl
 *
 */
public class ShowNodeContextEvent extends AVertexRepBasedEvent {

	public ShowNodeContextEvent() {
	}

	public ShowNodeContextEvent(PathwayVertexRep vertexRep) {
		super(vertexRep);
	}
}
