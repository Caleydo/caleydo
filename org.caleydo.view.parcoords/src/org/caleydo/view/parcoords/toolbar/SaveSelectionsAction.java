/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.parcoords.toolbar;

import org.caleydo.core.gui.SimpleEventAction;
import org.caleydo.view.parcoords.Activator;
import org.caleydo.view.parcoords.listener.ApplyCurrentSelectionToVirtualArrayEvent;

public class SaveSelectionsAction extends SimpleEventAction {

	private static final String LABEL = "Save Selections";
	private static final String ICON = "resources/icons/save_selections_16.png";

	public SaveSelectionsAction() {
		super(LABEL, ICON, Activator.getResourceLoader(), new ApplyCurrentSelectionToVirtualArrayEvent());
	}
}
