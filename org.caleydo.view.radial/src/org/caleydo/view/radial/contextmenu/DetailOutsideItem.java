package org.caleydo.view.radial.contextmenu;

import org.caleydo.core.event.view.radial.DetailOutsideEvent;
import org.caleydo.core.view.contextmenu.AContextMenuItem;

public class DetailOutsideItem extends AContextMenuItem {

	public DetailOutsideItem(int elementID) {
		setLabel("Toggle Detail View (D)");

		DetailOutsideEvent event = new DetailOutsideEvent();
		event.setElementID(elementID);
		event.setSender(this);
		registerEvent(event);
	}
}
