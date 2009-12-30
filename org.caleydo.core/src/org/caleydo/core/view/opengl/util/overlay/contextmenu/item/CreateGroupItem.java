package org.caleydo.core.view.opengl.util.overlay.contextmenu.item;

import org.caleydo.core.manager.event.view.grouper.CreateGroupEvent;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.AContextMenuItem;

public class CreateGroupItem
	extends AContextMenuItem {

	public CreateGroupItem() {
		super();
		setText("Create Group");
		CreateGroupEvent event = new CreateGroupEvent();
		event.setSender(this);
		registerEvent(event);
	}
}
