/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.pathway;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;

/**
 * @author Christian
 *
 */
public abstract class AVertexRepBasedEventFactory implements IVertexRepBasedEventFactory {

	protected final String eventSpace;

	/**
	 *
	 */
	public AVertexRepBasedEventFactory(String eventSpace) {
		this.eventSpace = eventSpace;
	}

	@Override
	public void triggerEvent(PathwayVertexRep vertexRep) {
		AEvent event = create(vertexRep);
		if (event != null) {
			EventPublisher.trigger(event);
		}
	}

}
