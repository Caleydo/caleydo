package org.caleydo.view.grouper.contextmenu;

import org.caleydo.core.view.contextmenu.ContextMenuItem;
import org.caleydo.view.grouper.event.PasteGroupsEvent;

public class PasteGroupsItem extends ContextMenuItem {

	public PasteGroupsItem(int iParentGroupID) {

		setLabel("Paste");
		
		PasteGroupsEvent event = new PasteGroupsEvent(iParentGroupID);
		event.setSender(this);
		registerEvent(event);
	}
}
