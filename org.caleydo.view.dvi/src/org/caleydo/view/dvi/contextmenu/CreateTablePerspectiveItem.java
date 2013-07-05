/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.dvi.contextmenu;

import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.view.dvi.event.CreateTablePerspectiveEvent;

public class CreateTablePerspectiveItem extends AContextMenuItem {

	public CreateTablePerspectiveItem(CreateTablePerspectiveEvent event) {
		setLabel("Create");
		event.setSender(this);
		registerEvent(event);
	}
}
