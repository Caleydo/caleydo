/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.pathway.toolbar;

import org.caleydo.core.gui.SimpleAction;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.datadomain.pathway.Activator;
import org.caleydo.datadomain.pathway.listener.EnableFreePathSelectionEvent;
import org.caleydo.datadomain.pathway.listener.EnablePathSelectionEvent;

/**
 * Button for toggling path selection.
 *
 * @author Christian Partl
 *
 */
public class SelectPathAction extends SimpleAction {
	public static final String LABEL = "Toggle path selection (Ctrl + O)";
	public static final String ICON = "resources/icons/path_selection.png";
	private String eventSpace;

	// private SelectFreePathAction selectFreePathAction;

	public SelectPathAction(boolean isChecked, String eventSpace) {
		super(LABEL, ICON, new ResourceLoader(Activator.getResourceLocator()));
		setChecked(isChecked);
		this.eventSpace = eventSpace;
	}

	@Override
	public void run() {
		super.run();
		if (isChecked()) {
			EnableFreePathSelectionEvent e = new EnableFreePathSelectionEvent(false);
			e.setEventSpace(eventSpace);
			GeneralManager.get().getEventPublisher().triggerEvent(e);
			// if (selectFreePathAction != null)
			// selectFreePathAction.setChecked(false);
		}

		EnablePathSelectionEvent event = new EnablePathSelectionEvent(isChecked());
		event.setEventSpace(eventSpace);
		GeneralManager.get().getEventPublisher().triggerEvent(event);
	}

	// /**
	// * @param selectFreePathAction
	// * setter, see {@link selectFreePathAction}
	// */
	// public void setSelectFreePathAction(SelectFreePathAction selectFreePathAction) {
	// this.selectFreePathAction = selectFreePathAction;
	// }
}
