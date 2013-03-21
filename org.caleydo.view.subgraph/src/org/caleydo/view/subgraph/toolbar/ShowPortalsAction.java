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
package org.caleydo.view.subgraph.toolbar;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.gui.SimpleAction;
import org.caleydo.view.subgraph.event.ShowPortalsEvent;

/**
 * Button for toggling to show portals or not.
 *
 * @author Christian Partl
 *
 */
public class ShowPortalsAction extends SimpleAction {
	public static final String LABEL = "Show Node Occurrences across Pathways (P)";
	public static final String ICON = "resources/icons/view/pathway/show_portals3.png";

	private String eventSpace;

	/**
	 * Constructor.
	 */
	public ShowPortalsAction(String eventSpace) {
		super(LABEL, ICON);
		setChecked(false);
		this.eventSpace = eventSpace;
	}

	@Override
	public void run() {
		super.run();
		ShowPortalsEvent event = new ShowPortalsEvent(isChecked());
		event.setEventSpace(eventSpace);
		EventPublisher.INSTANCE.triggerEvent(event);
	}
}
