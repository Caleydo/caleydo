/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.stratomex.brick.contextmenu;

import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.view.stratomex.event.RenameEvent;

public class RenameBrickItem extends AContextMenuItem {

	public RenameBrickItem(Integer brickID) {

		setLabel("Set Name/ Rename");

		RenameEvent event = new RenameEvent(brickID);
		event.setSender(this);
		registerEvent(event);
	}
}
