/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.pathway;

import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.datadomain.pathway.listener.AVertexRepBasedEvent;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Context menu entry that triggers {@link AVertexRepBasedEvent}s.
 *
 * @author Christian Partl
 *
 */
public class VertexRepBasedContextMenuItem extends AContextMenuItem {

	protected VertexRepBasedEventFactory factory;

	public VertexRepBasedContextMenuItem(String label, Class<? extends AVertexRepBasedEvent> eventClass) {
		this(label, eventClass, null);
	}

	public VertexRepBasedContextMenuItem(String label, Class<? extends AVertexRepBasedEvent> eventClass,
			String eventSpace) {
		setLabel(label);
		factory = new VertexRepBasedEventFactory(eventClass, eventSpace);
	}

	/**
	 * Sets the vertexRep at the event this menu entry was initialized with and registers this event to be triggered
	 * when selecting this entry.
	 *
	 * @param vertexRep
	 */
	public void setVertexRep(PathwayVertexRep vertexRep) {

		try {
			AVertexRepBasedEvent event = factory.create(vertexRep);
			clearEvents();
			registerEvent(event);
		} catch (IllegalArgumentException | SecurityException e) {
			Logger.log(new Status(IStatus.WARNING, "VertexRepBasedContextMenuItem",
					"Could not instatiate VertexRepBasedEvent!"));
		}

	}

}
