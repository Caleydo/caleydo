/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
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

	protected Class<? extends AVertexRepBasedEvent> eventClass;
	protected String eventSpace = null;

	public VertexRepBasedContextMenuItem(String label, Class<? extends AVertexRepBasedEvent> eventClass) {
		setLabel(label);
		this.eventClass = eventClass;
	}

	public VertexRepBasedContextMenuItem(String label, Class<? extends AVertexRepBasedEvent> eventClass,
			String eventSpace) {
		setLabel(label);
		this.eventClass = eventClass;
		this.eventSpace = eventSpace;
	}

	/**
	 * Sets the vertexRep at the event this menu entry was initialized with and registers this event to be triggered
	 * when selecting this entry.
	 *
	 * @param vertexRep
	 */
	public void setVertexRep(PathwayVertexRep vertexRep) {

		try {
			AVertexRepBasedEvent event = eventClass.newInstance();
			event.setVertexRep(vertexRep);
			event.setEventSpace(eventSpace);
			clearEvents();
			registerEvent(event);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | SecurityException e) {
			Logger.log(new Status(IStatus.WARNING, "VertexRepBasedContextMenuItem",
					"Could not instatiate VertexRepBasedEvent!"));
		}

	}

}
