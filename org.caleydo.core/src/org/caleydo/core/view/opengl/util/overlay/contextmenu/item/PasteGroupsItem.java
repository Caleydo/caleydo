package org.caleydo.core.view.opengl.util.overlay.contextmenu.item;

import org.caleydo.core.manager.event.view.grouper.PasteGroupsEvent;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.AContextMenuItem;

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
