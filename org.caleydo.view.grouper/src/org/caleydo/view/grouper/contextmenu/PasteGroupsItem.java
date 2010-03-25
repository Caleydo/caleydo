package org.caleydo.view.grouper.contextmenu;

import org.caleydo.core.view.opengl.util.overlay.contextmenu.AContextMenuItem;
import org.caleydo.view.grouper.event.PasteGroupsEvent;

public class PasteGroupsItem
	extends AContextMenuItem {

	public PasteGroupsItem(int iParentGroupID) {
		super();
		setText("Paste");
		PasteGroupsEvent event = new PasteGroupsEvent(iParentGroupID);
		event.setSender(this);
		registerEvent(event);
	}
}
