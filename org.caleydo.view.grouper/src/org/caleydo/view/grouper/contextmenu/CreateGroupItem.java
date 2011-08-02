package org.caleydo.view.grouper.contextmenu;

import java.util.Set;

import org.caleydo.core.view.opengl.util.overlay.contextmenu.ContextMenuItem;
import org.caleydo.view.grouper.event.CreateGroupEvent;

public class CreateGroupItem extends ContextMenuItem {

	public CreateGroupItem(Set<Integer> setContainedGroups) {

		setLabel("Create Group");

		CreateGroupEvent event = new CreateGroupEvent(setContainedGroups);
		event.setSender(this);
		registerEvent(event);
	}
}
