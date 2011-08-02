package org.caleydo.view.grouper.contextmenu;

import org.caleydo.core.view.contextmenu.ContextMenuItem;
import org.caleydo.view.grouper.event.RenameGroupEvent;

public class RenameGroupItem extends ContextMenuItem {

	public RenameGroupItem(int groupID) {

		setLabel("Rename Group");

		RenameGroupEvent event = new RenameGroupEvent(groupID);
		event.setSender(this);
		registerEvent(event);
	}
}
