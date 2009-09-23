package org.caleydo.core.view.opengl.util.overlay.contextmenu.item;

import org.caleydo.core.manager.event.view.radial.DetailOutsideEvent;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.AContextMenuItem;

public class DetailOutsideItem
	extends AContextMenuItem {

	public DetailOutsideItem(int elementID) {
		super();
		setText("Toggle Detail View (D)");
		DetailOutsideEvent event = new DetailOutsideEvent();
		event.setElementID(elementID);
		event.setSender(this);
		registerEvent(event);
	}
}
