package org.caleydo.core.view.opengl.util.overlay.contextmenu.item;

import java.util.Set;

import org.caleydo.core.manager.event.view.grouper.CreateGroupEvent;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.AContextMenuItem;

public class CreateGroupItem
	extends AContextMenuItem {

	public CreateGroupItem(Set<Integer> setContainedGroups) {
		super();
		setText("Create Group");
		CreateGroupEvent event = new CreateGroupEvent(setContainedGroups);
		event.setSender(this);
		registerEvent(event);
	}
}
