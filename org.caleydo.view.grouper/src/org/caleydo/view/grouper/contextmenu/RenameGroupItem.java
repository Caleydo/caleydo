package org.caleydo.view.grouper.contextmenu;

import org.caleydo.core.view.opengl.util.overlay.contextmenu.AContextMenuItem;
import org.caleydo.view.grouper.event.RenameGroupEvent;

public class RenameGroupItem extends AContextMenuItem {

	public RenameGroupItem(int groupID) {
		super();
		setText("Rename Group");

		RenameGroupEvent event = new RenameGroupEvent(groupID);

		event.setSender(this);
		registerEvent(event);
	}
}
