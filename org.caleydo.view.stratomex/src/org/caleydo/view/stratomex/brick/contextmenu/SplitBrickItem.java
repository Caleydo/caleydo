/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 *
 */
package org.caleydo.view.stratomex.brick.contextmenu;

import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.view.stratomex.event.SplitBrickEvent;

/**
 * @author Alexander Lex
 *
 */
public class SplitBrickItem extends AContextMenuItem {

	public SplitBrickItem(Integer connectionBandID, Boolean splitLeftBrick) {
		if (splitLeftBrick)

			setLabel("Split left brick");
		else
			setLabel("Split right brick");

		SplitBrickEvent event = new SplitBrickEvent(connectionBandID, splitLeftBrick);
		event.setSender(this);
		registerEvent(event);
	}
}
