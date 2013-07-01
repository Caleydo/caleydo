/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.radial.contextmenu;

import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.view.radial.event.DetailOutsideEvent;

public class DetailOutsideItem extends AContextMenuItem {

	public DetailOutsideItem(int elementID) {
		setLabel("Toggle Detail View (D)");

		DetailOutsideEvent event = new DetailOutsideEvent();
		event.setElementID(elementID);
		event.setSender(this);
		registerEvent(event);
	}
}
