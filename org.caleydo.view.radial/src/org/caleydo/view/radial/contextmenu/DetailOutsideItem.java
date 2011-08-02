package org.caleydo.view.radial.contextmenu;

import org.caleydo.core.manager.event.view.radial.DetailOutsideEvent;
import org.caleydo.core.view.contextmenu.ContextMenuItem;

public class DetailOutsideItem extends ContextMenuItem {

	public DetailOutsideItem(int elementID) {
		setLabel("Toggle Detail View (D)");

		DetailOutsideEvent event = new DetailOutsideEvent();
		event.setElementID(elementID);
		event.setSender(this);
		registerEvent(event);
	}
}
