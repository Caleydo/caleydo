/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.pathway;

import org.caleydo.core.util.logging.Logger;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.datadomain.pathway.listener.AVertexRepBasedEvent;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Generic Factory for {@link AVertexRepBasedEvent}s.
 *
 * @author Christian Partl
 *
 */
public class VertexRepBasedEventFactory extends AVertexRepBasedEventFactory {

	protected final Class<? extends AVertexRepBasedEvent> eventClass;

	public VertexRepBasedEventFactory(Class<? extends AVertexRepBasedEvent> eventClass, String eventSpace) {
		super(eventSpace);
		this.eventClass = eventClass;
	}

	/**
	 * Creates the event using the specified vertexRep.
	 *
	 * @param vertexRep
	 * @return The event, null if the event could not be created.
	 */
	@Override
	public AVertexRepBasedEvent create(PathwayVertexRep vertexRep) {
		AVertexRepBasedEvent event;
		try {
			event = eventClass.newInstance();
			event.setVertexRep(vertexRep);
			event.setEventSpace(eventSpace);
			return event;
		} catch (InstantiationException | IllegalAccessException e) {
			Logger.log(new Status(IStatus.WARNING, "VertexRepBasedContextMenuItem",
					"Could not instatiate VertexRepBasedEvent!"));
		}

		return null;
	}

}
