/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.datadomain.pathway.toolbar;

import org.caleydo.core.gui.SimpleAction;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.datadomain.pathway.listener.EnableFreePathSelectionEvent;

/**
 * Event to enable free selection of pathway nodes.
 *
 * @author Christian Partl
 * 
 */
public class SelectFreePathAction extends SimpleAction {

	public static final String LABEL = "Toggle free selection of nodes";
	public static final String ICON = "resources/icons/view/pathway/path_selection.png";
	private String eventSpace;

	public SelectFreePathAction(boolean isChecked, String eventSpace) {
		super(LABEL, ICON);
		setChecked(isChecked);
		this.eventSpace = eventSpace;
	}

	@Override
	public void run() {
		super.run();
		EnableFreePathSelectionEvent event = new EnableFreePathSelectionEvent(isChecked());
		event.setEventSpace(eventSpace);
		GeneralManager.get().getEventPublisher().triggerEvent(event);
	}

}
