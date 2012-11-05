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
package org.caleydo.core.view.contextmenu;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.manager.GeneralManager;

/**
 * Abstract base class for items in the context menu. A item must be supplied with a string to display its
 * function in the context menu as well as with a list of event which can be triggered. The events are of type
 * {@link AEvent} and are published via the {@link EventPublisher}.
 *
 * @author Marc Streit
 */
public abstract class AContextMenuItem {

	private final List<AContextMenuItem> subMenuItems = new ArrayList<>();

	private String label = "<not set>";

	private final List<AEvent> events = new ArrayList<>();

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	/**
	 * Sets the event which is associated with the item. This event will be triggered when requested by the
	 * context menu. It is mandatory to set an event.
	 *
	 * @param event
	 *            the event triggered when requested by the context menu
	 */
	public void registerEvent(AEvent event) {
		this.events.add(event);
	}

	/**
	 * Triggers the supplied event via the event publishing system
	 */
	public void triggerEvent() {
		if (events != null && events.size() > 0) {
			for (AEvent event : events) {
				GeneralManager.get().getEventPublisher().triggerEvent(event);
			}
		}
	}

	protected void addSubItem(AContextMenuItem contextMenuItem) {
		subMenuItems.add(contextMenuItem);
	}

	/**
	 * @return the subMenuItems
	 */
	public List<AContextMenuItem> getSubMenuItems() {
		return subMenuItems;
	}
}
