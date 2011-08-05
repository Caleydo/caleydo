package org.caleydo.view.matchmaker.contextmenu;

import org.caleydo.core.manager.event.view.matchmaker.DuplicateTableBarItemEvent;
import org.caleydo.core.view.contextmenu.AContextMenuItem;

public class DuplicateTableBarElementItem
	extends AContextMenuItem {

	public DuplicateTableBarElementItem(int itemID) {

		setLabel("Cluster");
		
		DuplicateTableBarItemEvent event = new DuplicateTableBarItemEvent(itemID);
		event.setSender(this);
		registerEvent(event);
	}

}
