package org.caleydo.core.view.opengl.util.overlay.contextmenu.item;

import org.caleydo.core.manager.event.view.compare.DuplicateSetBarItemEvent;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.AContextMenuItem;

public class DuplicateSetBarElementItem
	extends AContextMenuItem {

	public DuplicateSetBarElementItem(int itemID) {
		super();
		setText("Duplicate");
		DuplicateSetBarItemEvent event = new DuplicateSetBarItemEvent(itemID);
		event.setSender(this);
		registerEvent(event);
	}

}
