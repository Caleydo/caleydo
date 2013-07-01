/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.pathway.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;

/**
 * Base class for events that are based on a {@link PathwayVertexRep}.
 *
 * @author Christian Partl
 *
 */
public abstract class AVertexRepBasedEvent extends AEvent {

	/**
	 * Vertex rep that serves as portal.
	 */
	protected PathwayVertexRep vertexRep;

	public AVertexRepBasedEvent() {
	}

	public AVertexRepBasedEvent(PathwayVertexRep vertexRep) {
		this.vertexRep = vertexRep;
	}

	public AVertexRepBasedEvent(AVertexRepBasedEvent template) {
		this.vertexRep = template.vertexRep;
	}

	@Override
	public boolean checkIntegrity() {
		return vertexRep != null;
	}

	/**
	 * @param vertexRep
	 *            setter, see {@link vertexRep}
	 */
	public void setVertexRep(PathwayVertexRep vertexRep) {
		this.vertexRep = vertexRep;
	}

	/**
	 * @return the vertexRep, see {@link #vertexRep}
	 */
	public PathwayVertexRep getVertexRep() {
		return vertexRep;
	}

}
