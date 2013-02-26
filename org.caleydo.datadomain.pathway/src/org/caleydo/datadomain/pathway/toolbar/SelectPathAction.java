/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.datadomain.pathway.toolbar;

import org.caleydo.core.gui.SimpleAction;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.datadomain.pathway.listener.EnablePathSelectionEvent;

/**
 * Button for toggling path selection.
 *
 * @author Christian Partl
 *
 */
public class SelectPathAction extends SimpleAction {
	public static final String LABEL = "Toggle path selection (Ctrl + O)";
	public static final String ICON = "resources/icons/view/pathway/path_selection.png";
	private String eventSpace;

	public SelectPathAction(boolean isChecked, String eventSpace) {
		super(LABEL, ICON);
		setChecked(isChecked);
		this.eventSpace = eventSpace;
	}

	@Override
	public void run() {
		super.run();
		EnablePathSelectionEvent event = new EnablePathSelectionEvent(isChecked());
		event.setEventSpace(eventSpace);
		GeneralManager.get().getEventPublisher().triggerEvent(event);
	}
}
